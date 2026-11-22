
package com.example.testpilot.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.testpilot.data.local.TestSuiteEntity
import com.example.testpilot.data.repository.TestSuiteRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class TestSuiteViewModel(
    private val repository: TestSuiteRepository
) : ViewModel() {

    private val _suites = MutableStateFlow<List<TestSuiteEntity>>(emptyList())
    val suites: StateFlow<List<TestSuiteEntity>> = _suites.asStateFlow()

    private val _suiteCount = MutableStateFlow(0)
    val suiteCount: StateFlow<Int> = _suiteCount.asStateFlow()

    fun loadSuites(projectId: Int) {
        viewModelScope.launch {
            repository.getSuitesByProject(projectId).collect {
                _suites.value = it
            }
        }

        viewModelScope.launch {
            repository.getSuiteCount(projectId).collect {
                _suiteCount.value = it
            }
        }
    }

    fun addSuite(
        projectId: Int,
        name: String,
        description: String
    ) {
        viewModelScope.launch {
            repository.insertSuite(
                TestSuiteEntity(
                    projectId = projectId,
                    name = name.trim(),
                    description = description.trim()
                )
            )
        }
    }

    fun updateSuite(suite: TestSuiteEntity) {
        viewModelScope.launch {
            repository.updateSuite(
                suite.copy(
                    name = suite.name.trim(),
                    description = suite.description.trim(),
                    updatedAt = System.currentTimeMillis()
                )
            )
        }
    }

    fun deleteSuite(suite: TestSuiteEntity) {
        viewModelScope.launch {
            repository.deleteSuite(suite)
        }
    }
}
