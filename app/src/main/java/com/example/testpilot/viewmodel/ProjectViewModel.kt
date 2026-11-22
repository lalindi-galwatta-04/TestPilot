
package com.example.testpilot.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.testpilot.data.local.ProjectEntity
import com.example.testpilot.data.repository.ProjectRepository
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

data class ProjectUiState(
    val projects: List<ProjectEntity> = emptyList(),
    val searchQuery: String = "",
    val selectedStatus: String = "All",
    val isLoading: Boolean = true,
    val errorMessage: String? = null
)

@OptIn(ExperimentalCoroutinesApi::class)
class ProjectViewModel(
    private val repository: ProjectRepository
) : ViewModel() {

    private val searchQuery = MutableStateFlow("")
    private val selectedStatus = MutableStateFlow("All")

    val uiState: StateFlow<ProjectUiState> =
        combine(
            repository.getAllProjects(),
            searchQuery,
            selectedStatus
        ) { projects, query, status ->

            val filteredProjects = projects.filter { project ->

                val matchesSearch =
                    project.projectName.contains(query, ignoreCase = true) ||
                            project.applicationName.contains(query, ignoreCase = true)

                val matchesStatus =
                    status == "All" || project.status == status

                matchesSearch && matchesStatus
            }

            ProjectUiState(
                projects = filteredProjects,
                searchQuery = query,
                selectedStatus = status,
                isLoading = false
            )
        }.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = ProjectUiState()
        )

    fun getProjectById(id: Int): Flow<ProjectEntity?> {
        return repository.getProjectById(id)
    }

    fun updateSearchQuery(query: String) {
        searchQuery.value = query
    }

    fun updateSelectedStatus(status: String) {
        selectedStatus.value = status
    }

    fun addProject(
        projectName: String,
        description: String,
        applicationName: String,
        version: String,
        startDate: Long,
        endDate: Long?
    ) {
        viewModelScope.launch {
            try {
                repository.insertProject(
                    ProjectEntity(
                        projectName = projectName.trim(),
                        description = description.trim(),
                        applicationName = applicationName.trim(),
                        version = version.trim(),
                        status = "Active",
                        startDate = startDate,
                        endDate = endDate
                    )
                )
            } catch (exception: Exception) {
                // Error reporting will be handled through UI state.
            }
        }
    }

    fun updateProject(project: ProjectEntity) {
        viewModelScope.launch {
            try {
                repository.updateProject(
                    project.copy(
                        updatedAt = System.currentTimeMillis()
                    )
                )
            } catch (exception: Exception) {
                // Error reporting will be handled through UI state.
            }
        }
    }

    fun deleteProject(project: ProjectEntity) {
        viewModelScope.launch {
            try {
                repository.deleteProject(project)
            } catch (exception: Exception) {
                // Error reporting will be handled through UI state.
            }
        }
    }
}
