
package com.example.testpilot.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.testpilot.data.local.DefectEntity
import com.example.testpilot.data.repository.DefectRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class DefectViewModel(
    private val repository: DefectRepository
) : ViewModel() {

    // All defects
    val defects = repository.getAllDefects()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    // Search query
    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    // Selected status filter
    private val _selectedStatus = MutableStateFlow("All")
    val selectedStatus: StateFlow<String> = _selectedStatus.asStateFlow()

    // Selected severity filter
    private val _selectedSeverity = MutableStateFlow("All")
    val selectedSeverity: StateFlow<String> = _selectedSeverity.asStateFlow()

    // Selected project filter
    private val _selectedProjectId = MutableStateFlow<Int?>(null)
    val selectedProjectId: StateFlow<Int?> = _selectedProjectId.asStateFlow()

    // Defect count
    val defectCount = repository.getDefectCount()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = 0
        )

    // Add a new defect
    fun addDefect(defect: DefectEntity) {
        viewModelScope.launch {
            repository.insertDefect(defect)
        }
    }

    // Update an existing defect
    fun updateDefect(defect: DefectEntity) {
        viewModelScope.launch {
            repository.updateDefect(
                defect.copy(updatedAt = System.currentTimeMillis())
            )
        }
    }

    // Delete a defect
    fun deleteDefect(defect: DefectEntity) {
        viewModelScope.launch {
            repository.deleteDefect(defect)
        }
    }

    // Update search query
    fun updateSearchQuery(query: String) {
        _searchQuery.value = query
    }

    // Update status filter
    fun updateStatusFilter(status: String) {
        _selectedStatus.value = status
    }

    // Update severity filter
    fun updateSeverityFilter(severity: String) {
        _selectedSeverity.value = severity
    }

    // Update project filter
    fun updateProjectFilter(projectId: Int?) {
        _selectedProjectId.value = projectId
    }

    // Reset all filters
    fun clearFilters() {
        _searchQuery.value = ""
        _selectedStatus.value = "All"
        _selectedSeverity.value = "All"
        _selectedProjectId.value = null
    }

    // Get defects by project
    fun getDefectsByProject(projectId: Int) =
        repository.getDefectsByProject(projectId)

    // Get a defect by ID
    fun getDefectById(defectId: Int) =
        repository.getDefectById(defectId)

    // Get defects by status
    fun getDefectsByStatus(status: String) =
        repository.getDefectsByStatus(status)

    // Get defects by severity
    fun getDefectsBySeverity(severity: String) =
        repository.getDefectsBySeverity(severity)

    // Search defects
    fun searchDefects(query: String) =
        repository.searchDefects(query)

    // Analytics: count defects by status
    fun getDefectCountByStatus(status: String): Flow<Int> =
        repository.getDefectCountByStatus(status)

    // Analytics: count defects by severity
    fun getDefectCountBySeverity(severity: String): Flow<Int> =
        repository.getDefectCountBySeverity(severity)
}
