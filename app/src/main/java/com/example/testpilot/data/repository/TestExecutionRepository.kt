
package com.example.testpilot.data.repository

import com.example.testpilot.data.local.TestExecutionDao
import com.example.testpilot.data.local.TestExecutionEntity
import com.example.testpilot.data.local.TestExecutionStepDao
import com.example.testpilot.data.local.TestExecutionStepEntity
import kotlinx.coroutines.flow.Flow

class TestExecutionRepository(
    private val executionDao: TestExecutionDao,
    private val executionStepDao: TestExecutionStepDao
) {

    // Test Execution operations

    fun getExecutionsByTestCase(
        testCaseId: Int
    ): Flow<List<TestExecutionEntity>> {
        return executionDao.getExecutionsByTestCase(testCaseId)
    }

    fun getExecutionById(
        executionId: Int
    ): Flow<TestExecutionEntity?> {
        return executionDao.getExecutionById(executionId)
    }

    fun getExecutionCount(
        testCaseId: Int
    ): Flow<Int> {
        return executionDao.getExecutionCount(testCaseId)
    }

    suspend fun insertExecution(
        execution: TestExecutionEntity
    ): Long {
        return executionDao.insertExecution(execution)
    }

    suspend fun updateExecution(
        execution: TestExecutionEntity
    ) {
        executionDao.updateExecution(execution)
    }

    suspend fun deleteExecution(
        execution: TestExecutionEntity
    ) {
        executionDao.deleteExecution(execution)
    }

    // Test Execution Step operations

    fun getStepsByExecution(
        executionId: Int
    ): Flow<List<TestExecutionStepEntity>> {
        return executionStepDao.getStepsByExecution(executionId)
    }

    suspend fun insertExecutionSteps(
        steps: List<TestExecutionStepEntity>
    ) {
        executionStepDao.insertExecutionSteps(steps)
    }

    suspend fun insertExecutionStep(
        step: TestExecutionStepEntity
    ): Long {
        return executionStepDao.insertExecutionStep(step)
    }

    suspend fun updateExecutionStep(
        step: TestExecutionStepEntity
    ) {
        executionStepDao.updateExecutionStep(step)
    }

    suspend fun deleteExecutionStep(
        step: TestExecutionStepEntity
    ) {
        executionStepDao.deleteExecutionStep(step)
    }

    suspend fun deleteStepsByExecution(
        executionId: Int
    ) {
        executionStepDao.deleteStepsByExecution(executionId)
    }
}
