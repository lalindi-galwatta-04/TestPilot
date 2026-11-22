
package com.example.testpilot.data.repository

import com.example.testpilot.data.local.TestCaseDao
import com.example.testpilot.data.local.TestCaseEntity
import kotlinx.coroutines.flow.Flow

class TestCaseRepository(
    private val testCaseDao: TestCaseDao
) {

    fun getTestCasesBySuite(
        suiteId: Int
    ): Flow<List<TestCaseEntity>> {
        return testCaseDao.getTestCasesBySuite(suiteId)
    }

    fun getTestCaseById(
        testCaseId: Int
    ): Flow<TestCaseEntity?> {
        return testCaseDao.getTestCaseById(testCaseId)
    }

    fun searchTestCases(
        suiteId: Int,
        query: String
    ): Flow<List<TestCaseEntity>> {
        return testCaseDao.searchTestCases(suiteId, query)
    }

    fun getTestCaseCount(
        suiteId: Int
    ): Flow<Int> {
        return testCaseDao.getTestCaseCount(suiteId)
    }

    fun getTestCaseCountByPriority(
        suiteId: Int,
        priority: String
    ): Flow<Int> {
        return testCaseDao.getTestCaseCountByPriority(
            suiteId,
            priority
        )
    }

    suspend fun insertTestCase(
        testCase: TestCaseEntity
    ): Long {
        return testCaseDao.insertTestCase(testCase)
    }

    suspend fun updateTestCase(
        testCase: TestCaseEntity
    ) {
        testCaseDao.updateTestCase(testCase)
    }

    suspend fun deleteTestCase(
        testCase: TestCaseEntity
    ) {
        testCaseDao.deleteTestCase(testCase)
    }
}
