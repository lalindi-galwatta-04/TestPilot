
package com.example.testpilot.data.local

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface TestExecutionDao {

    @Insert(onConflict = OnConflictStrategy.ABORT)
    suspend fun insertExecution(
        execution: TestExecutionEntity
    ): Long

    @Update
    suspend fun updateExecution(
        execution: TestExecutionEntity
    )

    @Delete
    suspend fun deleteExecution(
        execution: TestExecutionEntity
    )

    @Query(
        """
        SELECT * FROM test_executions
        WHERE testCaseId = :testCaseId
        ORDER BY executedAt DESC
        """
    )
    fun getExecutionsByTestCase(
        testCaseId: Int
    ): Flow<List<TestExecutionEntity>>

    @Query(
        """
        SELECT * FROM test_executions
        WHERE id = :executionId
        LIMIT 1
        """
    )
    fun getExecutionById(
        executionId: Int
    ): Flow<TestExecutionEntity?>

    @Query(
        """
        SELECT COUNT(*) FROM test_executions
        WHERE testCaseId = :testCaseId
        """
    )
    fun getExecutionCount(
        testCaseId: Int
    ): Flow<Int>

    // --------------------------------
    // ANALYTICS QUERIES
    // --------------------------------

    // Total number of executions
    @Query("SELECT COUNT(*) FROM test_executions")
    fun getTotalExecutionCount(): Flow<Int>

    // Count executions by status
    @Query(
        """
        SELECT COUNT(*) FROM test_executions
        WHERE status = :status
        """
    )
    fun getExecutionCountByStatus(
        status: String
    ): Flow<Int>

    // Count completed executions
    @Query(
        """
        SELECT COUNT(*) FROM test_executions
        WHERE status IN ('Passed', 'Failed', 'Blocked')
        """
    )
    fun getCompletedExecutionCount(): Flow<Int>


    // --------------------------------
    // RECENT ACTIVITY
    // --------------------------------

    @Query(
        """
        SELECT *
        FROM (
            SELECT
                'PROJECT' AS activityType,
                projectName AS title,
                'New project created' AS description,
                status AS status,
                createdAt AS timestamp
            FROM projects

            UNION ALL

            SELECT
                'EXECUTION' AS activityType,
                tc.title AS title,
                'Test execution recorded' AS description,
                te.status AS status,
                te.executedAt AS timestamp
            FROM test_executions te
            INNER JOIN test_cases tc
                ON te.testCaseId = tc.id
            INNER JOIN test_suites ts
                ON tc.suiteId = ts.id
            INNER JOIN projects p
                ON ts.projectId = p.id
        )
        ORDER BY timestamp DESC
        LIMIT 10
        """
    )
    fun getRecentActivities(): Flow<List<RecentActivityItem>>

}
