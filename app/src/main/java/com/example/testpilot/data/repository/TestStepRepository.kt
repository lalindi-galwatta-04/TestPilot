
package com.example.testpilot.data.repository

import com.example.testpilot.data.local.TestStepDao
import com.example.testpilot.data.local.TestStepEntity
import kotlinx.coroutines.flow.Flow

class TestStepRepository(
    private val testStepDao: TestStepDao
) {

    fun getStepsByTestCase(
        testCaseId: Int
    ): Flow<List<TestStepEntity>> {
        return testStepDao.getStepsByTestCase(testCaseId)
    }

    suspend fun insertStep(
        step: TestStepEntity
    ): Long {
        return testStepDao.insertStep(step)
    }

    suspend fun insertSteps(
        steps: List<TestStepEntity>
    ) {
        testStepDao.insertSteps(steps)
    }

    suspend fun updateStep(
        step: TestStepEntity
    ) {
        testStepDao.updateStep(step)
    }

    suspend fun deleteStep(
        step: TestStepEntity
    ) {
        testStepDao.deleteStep(step)
    }

    suspend fun deleteStepsByTestCase(
        testCaseId: Int
    ) {
        testStepDao.deleteStepsByTestCase(testCaseId)
    }
}
