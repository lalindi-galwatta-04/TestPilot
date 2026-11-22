
package com.example.testpilot.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.testpilot.data.local.TestStepEntity
import com.example.testpilot.data.repository.TestStepRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class TestStepViewModel(
    private val repository: TestStepRepository
) : ViewModel() {

    private val _steps =
        MutableStateFlow<List<TestStepEntity>>(emptyList())

    val steps: StateFlow<List<TestStepEntity>> =
        _steps.asStateFlow()

    fun loadSteps(testCaseId: Int) {
        viewModelScope.launch {
            repository.getStepsByTestCase(testCaseId).collect {
                _steps.value = it
            }
        }
    }

    fun addStep(
        testCaseId: Int,
        stepNumber: Int,
        action: String,
        expectedResult: String
    ) {
        viewModelScope.launch {
            repository.insertStep(
                TestStepEntity(
                    testCaseId = testCaseId,
                    stepNumber = stepNumber,
                    action = action.trim(),
                    expectedResult = expectedResult.trim()
                )
            )
        }
    }

    fun updateStep(step: TestStepEntity) {
        viewModelScope.launch {
            repository.updateStep(
                step.copy(
                    action = step.action.trim(),
                    expectedResult = step.expectedResult.trim()
                )
            )
        }
    }

    fun deleteStep(step: TestStepEntity) {
        viewModelScope.launch {
            repository.deleteStep(step)
        }
    }

    fun replaceSteps(
        testCaseId: Int,
        newSteps: List<TestStepEntity>
    ) {
        viewModelScope.launch {
            repository.deleteStepsByTestCase(testCaseId)

            if (newSteps.isNotEmpty()) {
                repository.insertSteps(
                    newSteps.mapIndexed { index, step ->
                        step.copy(
                            id = 0,
                            testCaseId = testCaseId,
                            stepNumber = index + 1,
                            action = step.action.trim(),
                            expectedResult = step.expectedResult.trim()
                        )
                    }
                )
            }
        }
    }
}
