
package com.example.testpilot.data.local

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface TestExecutionStepDao {

    @Insert(onConflict = OnConflictStrategy.ABORT)
    suspend fun insertExecutionStep(
        step: TestExecutionStepEntity
    ): Long

    @Insert(onConflict = OnConflictStrategy.ABORT)
    suspend fun insertExecutionSteps(
        steps: List<TestExecutionStepEntity>
    )

    @Update
    suspend fun updateExecutionStep(
        step: TestExecutionStepEntity
    )

    @Delete
    suspend fun deleteExecutionStep(
        step: TestExecutionStepEntity
    )

    @Query(
        """
        SELECT * FROM test_execution_steps
        WHERE executionId = :executionId
        ORDER BY stepNumber ASC
        """
    )
    fun getStepsByExecution(
        executionId: Int
    ): Flow<List<TestExecutionStepEntity>>

    @Query(
        """
        DELETE FROM test_execution_steps
        WHERE executionId = :executionId
        """
    )
    suspend fun deleteStepsByExecution(
        executionId: Int
    )

    // --------------------------------
    // ANALYTICS QUERIES
    // --------------------------------

    // Total executed steps
    @Query("SELECT COUNT(*) FROM test_execution_steps")
    fun getTotalExecutionStepCount(): Flow<Int>

    // Count steps by result status
    @Query(
        """
        SELECT COUNT(*) FROM test_execution_steps
        WHERE status = :status
        """
    )
    fun getExecutionStepCountByStatus(
        status: String
    ): Flow<Int>

    // Count steps that have been tested
    @Query(
        """
        SELECT COUNT(*) FROM test_execution_steps
        WHERE status IN ('Pass', 'Fail', 'Blocked')
        """
    )
    fun getTestedStepCount(): Flow<Int>

    // Count steps that have not been executed
    @Query(
        """
    SELECT COUNT(*) FROM test_execution_steps
    WHERE status = 'Not Run'
    """
    )
    fun getNotRunStepCount(): Flow<Int>
}
