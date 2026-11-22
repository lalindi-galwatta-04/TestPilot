
package com.example.testpilot.data.repository

import com.example.testpilot.data.local.TestSuiteDao
import com.example.testpilot.data.local.TestSuiteEntity
import kotlinx.coroutines.flow.Flow

class TestSuiteRepository(
    private val testSuiteDao: TestSuiteDao
) {

    fun getSuitesByProject(
        projectId: Int
    ): Flow<List<TestSuiteEntity>> {
        return testSuiteDao.getSuitesByProject(projectId)
    }

    fun getSuiteById(
        suiteId: Int
    ): Flow<TestSuiteEntity?> {
        return testSuiteDao.getSuiteById(suiteId)
    }

    fun getSuiteCount(
        projectId: Int
    ): Flow<Int> {
        return testSuiteDao.getSuiteCount(projectId)
    }

    suspend fun insertSuite(
        suite: TestSuiteEntity
    ): Long {
        return testSuiteDao.insertSuite(suite)
    }

    suspend fun updateSuite(
        suite: TestSuiteEntity
    ) {
        testSuiteDao.updateSuite(suite)
    }

    suspend fun deleteSuite(
        suite: TestSuiteEntity
    ) {
        testSuiteDao.deleteSuite(suite)
    }
}
