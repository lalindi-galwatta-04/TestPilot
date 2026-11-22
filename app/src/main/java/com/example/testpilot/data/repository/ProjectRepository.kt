
package com.example.testpilot.data.repository

import com.example.testpilot.data.local.ProjectDao
import com.example.testpilot.data.local.ProjectEntity
import kotlinx.coroutines.flow.Flow

class ProjectRepository(
    private val projectDao: ProjectDao
) {

    fun getAllProjects(): Flow<List<ProjectEntity>> {
        return projectDao.getAllProjects()
    }

    fun getProjectById(id: Int): Flow<ProjectEntity?> {
        return projectDao.getProjectById(id)
    }

    fun searchProjects(query: String): Flow<List<ProjectEntity>> {
        return projectDao.searchProjects(query)
    }

    fun getProjectsByStatus(status: String): Flow<List<ProjectEntity>> {
        return projectDao.getProjectsByStatus(status)
    }

    fun getProjectCount(): Flow<Int> {
        return projectDao.getProjectCount()
    }

    suspend fun insertProject(project: ProjectEntity): Long {
        return projectDao.insertProject(project)
    }

    suspend fun updateProject(project: ProjectEntity) {
        projectDao.updateProject(project)
    }

    suspend fun deleteProject(project: ProjectEntity) {
        projectDao.deleteProject(project)
    }
}
