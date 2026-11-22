
package com.example.testpilot.data.repository

import com.example.testpilot.data.local.DefectDao
import com.example.testpilot.data.local.DefectEntity
import kotlinx.coroutines.flow.Flow

class DefectRepository(
    private val defectDao: DefectDao
) {

    suspend fun insertDefect(defect: DefectEntity): Long {
        return defectDao.insertDefect(defect)
    }

    suspend fun updateDefect(defect: DefectEntity) {
        defectDao.updateDefect(defect)
    }

    suspend fun deleteDefect(defect: DefectEntity) {
        defectDao.deleteDefect(defect)
    }

    fun getAllDefects(): Flow<List<DefectEntity>> {
        return defectDao.getAllDefects()
    }

    fun getDefectsByProject(projectId: Int): Flow<List<DefectEntity>> {
        return defectDao.getDefectsByProject(projectId)
    }

    fun getDefectById(defectId: Int): Flow<DefectEntity?> {
        return defectDao.getDefectById(defectId)
    }

    fun getDefectsByStatus(status: String): Flow<List<DefectEntity>> {
        return defectDao.getDefectsByStatus(status)
    }

    fun getDefectsBySeverity(severity: String): Flow<List<DefectEntity>> {
        return defectDao.getDefectsBySeverity(severity)
    }

    fun searchDefects(searchQuery: String): Flow<List<DefectEntity>> {
        return defectDao.searchDefects(searchQuery)
    }

    fun getDefectCount(): Flow<Int> {
        return defectDao.getDefectCount()
    }

    fun getDefectCountByStatus(status: String): Flow<Int> {
        return defectDao.getDefectCountByStatus(status)
    }

    fun getDefectCountBySeverity(severity: String): Flow<Int> {
        return defectDao.getDefectCountBySeverity(severity)
    }
}
