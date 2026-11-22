
package com.example.testpilot.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.testpilot.data.local.TestExecutionEntity
import com.example.testpilot.data.local.TestExecutionStepEntity
import com.example.testpilot.data.local.TestStepEntity
import com.example.testpilot.data.repository.TestExecutionRepository
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

class TestExecutionViewModel(
    private val repository: TestExecutionRepository
) : ViewModel() {

    // Execution history
    private val _executions =
        MutableStateFlow<List<TestExecutionEntity>>(emptyList())

    val executions: StateFlow<List<TestExecutionEntity>> =
        _executions.asStateFlow()

    // Steps belonging to the selected execution
    private val _executionSteps =
        MutableStateFlow<List<TestExecutionStepEntity>>(emptyList())

    val executionSteps: StateFlow<List<TestExecutionStepEntity>> =
        _executionSteps.asStateFlow()

    // Total execution count
    private val _executionCount = MutableStateFlow(0)

    val executionCount: StateFlow<Int> =
        _executionCount.asStateFlow()

    // Currently selected execution
    private val _currentExecution =
        MutableStateFlow<TestExecutionEntity?>(null)

    val currentExecution: StateFlow<TestExecutionEntity?> =
        _currentExecution.asStateFlow()

    // Completion feedback
    private val _completionMessage =
        MutableStateFlow<String?>(null)

    val completionMessage: StateFlow<String?> =
        _completionMessage.asStateFlow()

    // Actual result save feedback
    private val _stepSaveMessage =
        MutableStateFlow<String?>(null)

    val stepSaveMessage: StateFlow<String?> =
        _stepSaveMessage.asStateFlow()

    private var historyJob: Job? = null
    private var countJob: Job? = null
    private var stepsJob: Job? = null
    private var executionJob: Job? = null

    // Load execution history
    fun loadExecutions(testCaseId: Int) {

        historyJob?.cancel()
        countJob?.cancel()

        historyJob = viewModelScope.launch {
            repository.getExecutionsByTestCase(testCaseId)
                .collect { result ->
                    _executions.value = result
                }
        }

        countJob = viewModelScope.launch {
            repository.getExecutionCount(testCaseId)
                .collect { count ->
                    _executionCount.value = count
                }
        }
    }

    // Start a new execution
    fun startExecution(
        testCaseId: Int,
        sourceSteps: List<TestStepEntity>
    ) {

        if (sourceSteps.isEmpty()) return

        viewModelScope.launch {

            _completionMessage.value = null
            _stepSaveMessage.value = null

            val execution = TestExecutionEntity(
                testCaseId = testCaseId,
                status = "In Progress"
            )

            val executionId =
                repository.insertExecution(execution).toInt()

            // Create snapshots of the original test steps
            val snapshots = sourceSteps.map { step ->
                TestExecutionStepEntity(
                    executionId = executionId,
                    stepNumber = step.stepNumber,
                    action = step.action,
                    expectedResult = step.expectedResult,
                    actualResult = "",
                    status = "Not Run"
                )
            }

            repository.insertExecutionSteps(snapshots)

            loadExecution(executionId)
            loadExecutions(testCaseId)
        }
    }

    // Load a selected execution and its results
    fun loadExecution(executionId: Int) {

        stepsJob?.cancel()
        executionJob?.cancel()

        _completionMessage.value = null
        _stepSaveMessage.value = null

        stepsJob = viewModelScope.launch {
            repository.getStepsByExecution(executionId)
                .collect { result ->
                    _executionSteps.value = result
                }
        }

        executionJob = viewModelScope.launch {
            repository.getExecutionById(executionId)
                .collect { result ->
                    _currentExecution.value = result
                }
        }
    }

    // Save actual result and provide feedback
    fun saveActualResult(
        step: TestExecutionStepEntity,
        actualResult: String
    ) {

        viewModelScope.launch {

            val execution = repository
                .getExecutionById(step.executionId)
                .first() ?: return@launch

            // Do not allow changes after completion
            if (execution.status != "In Progress") {
                _stepSaveMessage.value =
                    "Completed executions cannot be edited."
                return@launch
            }

            if (actualResult.isBlank()) {
                _stepSaveMessage.value =
                    "Please enter an actual result before saving."
                return@launch
            }

            repository.updateExecutionStep(
                step.copy(actualResult = actualResult)
            )

            _stepSaveMessage.value =
                "Actual result for Step ${step.stepNumber} saved successfully!"
        }
    }

    // Update an individual step status
    fun updateExecutionStep(
        step: TestExecutionStepEntity
    ) {

        viewModelScope.launch {

            val execution = repository
                .getExecutionById(step.executionId)
                .first() ?: return@launch

            if (execution.status != "In Progress") {
                return@launch
            }

            repository.updateExecutionStep(step)
        }
    }

    // Save execution notes
    fun updateExecutionNotes(notes: String) {

        viewModelScope.launch {

            val execution = _currentExecution.value
                ?: return@launch

            if (execution.status != "In Progress") {
                return@launch
            }

            repository.updateExecution(
                execution.copy(notes = notes)
            )
        }
    }

    // Complete an execution
    fun completeExecution() {

        viewModelScope.launch {

            val execution = _currentExecution.value
                ?: return@launch

            if (execution.status != "In Progress") {

                _completionMessage.value =
                    "This execution has already been completed."

                return@launch
            }

            // Read the latest step results from Room
            val latestSteps = repository
                .getStepsByExecution(execution.id)
                .first()

            if (latestSteps.isEmpty()) {

                _completionMessage.value =
                    "There are no steps to complete."

                return@launch
            }

            // Every step must have a result
            val allStepsTested = latestSteps.all { step ->
                step.status == "Pass" ||
                        step.status == "Fail" ||
                        step.status == "Blocked"
            }

            if (!allStepsTested) {

                _completionMessage.value =
                    "Please test every step before completing."

                return@launch
            }

            // Calculate final execution status
            val finalStatus =
                calculateFinalStatus(latestSteps)

            repository.updateExecution(
                execution.copy(
                    status = finalStatus,
                    executedAt = System.currentTimeMillis()
                )
            )

            // Display completion feedback
            _completionMessage.value =
                when (finalStatus) {

                    "Passed" ->
                        "Execution completed successfully! All steps passed."

                    "Failed" ->
                        "Execution completed. One or more steps failed."

                    "Blocked" ->
                        "Execution completed with blocked steps."

                    else ->
                        "Execution completed."
                }
        }
    }

    // Calculate the final execution outcome
    private fun calculateFinalStatus(
        steps: List<TestExecutionStepEntity>
    ): String {

        return when {

            steps.any { it.status == "Fail" } ->
                "Failed"

            steps.any { it.status == "Blocked" } ->
                "Blocked"

            steps.all { it.status == "Pass" } ->
                "Passed"

            else ->
                "In Progress"
        }
    }

    // Clear completion feedback
    fun clearCompletionMessage() {
        _completionMessage.value = null
    }

    // Clear actual result save feedback
    fun clearStepSaveMessage() {
        _stepSaveMessage.value = null
    }

    // Delete an execution
    fun deleteExecution(
        execution: TestExecutionEntity
    ) {

        viewModelScope.launch {

            repository.deleteExecution(execution)

            if (_currentExecution.value?.id == execution.id) {
                _currentExecution.value = null
                _executionSteps.value = emptyList()
            }

            loadExecutions(execution.testCaseId)
        }
    }
}
