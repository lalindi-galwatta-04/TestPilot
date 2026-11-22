
package com.example.testpilot.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.testpilot.data.local.TestCaseEntity
import com.example.testpilot.data.repository.TestCaseRepository
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class TestCaseViewModel(
    private val repository: TestCaseRepository
) : ViewModel() {

    private val _testCases =
        MutableStateFlow<List<TestCaseEntity>>(emptyList())

    val testCases: StateFlow<List<TestCaseEntity>> =
        _testCases.asStateFlow()

    private val _testCaseCount = MutableStateFlow(0)

    val testCaseCount: StateFlow<Int> =
        _testCaseCount.asStateFlow()

    private val _searchQuery = MutableStateFlow("")

    val searchQuery: StateFlow<String> =
        _searchQuery.asStateFlow()

    private var currentSuiteId: Int? = null

    private var testCasesJob: Job? = null
    private var countJob: Job? = null

    fun loadTestCases(suiteId: Int) {

        currentSuiteId = suiteId

        testCasesJob?.cancel()
        countJob?.cancel()

        _searchQuery.value = ""

        testCasesJob = viewModelScope.launch {
            repository.getTestCasesBySuite(suiteId).collect {
                _testCases.value = it
            }
        }

        countJob = viewModelScope.launch {
            repository.getTestCaseCount(suiteId).collect {
                _testCaseCount.value = it
            }
        }
    }

    fun searchTestCases(query: String) {

        _searchQuery.value = query

        val suiteId = currentSuiteId ?: return

        testCasesJob?.cancel()

        testCasesJob = viewModelScope.launch {

            if (query.isBlank()) {
                repository.getTestCasesBySuite(suiteId).collect {
                    _testCases.value = it
                }
            } else {
                repository.searchTestCases(
                    suiteId,
                    query.trim()
                ).collect {
                    _testCases.value = it
                }
            }
        }
    }

    fun addTestCase(
        suiteId: Int,
        title: String,
        description: String,
        preconditions: String,
        priority: String,
        testType: String
    ) {
        viewModelScope.launch {
            repository.insertTestCase(
                TestCaseEntity(
                    suiteId = suiteId,
                    title = title.trim(),
                    description = description.trim(),
                    preconditions = preconditions.trim(),
                    priority = priority,
                    testType = testType
                )
            )
        }
    }

    fun updateTestCase(testCase: TestCaseEntity) {
        viewModelScope.launch {
            repository.updateTestCase(
                testCase.copy(
                    title = testCase.title.trim(),
                    description = testCase.description.trim(),
                    preconditions = testCase.preconditions.trim(),
                    updatedAt = System.currentTimeMillis()
                )
            )
        }
    }

    fun deleteTestCase(testCase: TestCaseEntity) {
        viewModelScope.launch {
            repository.deleteTestCase(testCase)
        }
    }
}
