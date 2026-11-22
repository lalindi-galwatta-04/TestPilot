
package com.example.testpilot.data.local

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface TestStepDao {

    @Insert(onConflict = OnConflictStrategy.ABORT)
    suspend fun insertStep(step: TestStepEntity): Long

    @Insert(onConflict = OnConflictStrategy.ABORT)
    suspend fun insertSteps(steps: List<TestStepEntity>)

    @Update
    suspend fun updateStep(step: TestStepEntity)

    @Delete
    suspend fun deleteStep(step: TestStepEntity)

    @Query(
        "SELECT * FROM test_steps " +
                "WHERE testCaseId = :testCaseId ORDER BY stepNumber ASC"
    )
    fun getStepsByTestCase(
        testCaseId: Int
    ): Flow<List<TestStepEntity>>

    @Query("DELETE FROM test_steps WHERE testCaseId = :testCaseId")
    suspend fun deleteStepsByTestCase(testCaseId: Int)
}
