
package com.example.testpilot.data.local

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface TestCaseDao {

    @Insert(onConflict = OnConflictStrategy.ABORT)
    suspend fun insertTestCase(testCase: TestCaseEntity): Long

    @Update
    suspend fun updateTestCase(testCase: TestCaseEntity)

    @Delete
    suspend fun deleteTestCase(testCase: TestCaseEntity)

    @Query(
        "SELECT * FROM test_cases " +
                "WHERE suiteId = :suiteId ORDER BY createdAt DESC"
    )
    fun getTestCasesBySuite(suiteId: Int): Flow<List<TestCaseEntity>>

    @Query("SELECT * FROM test_cases WHERE id = :testCaseId")
    fun getTestCaseById(testCaseId: Int): Flow<TestCaseEntity?>

    @Query("SELECT COUNT(*) FROM test_cases")
    fun getTotalTestCaseCount(): Flow<Int>

    @Query(
        """
        SELECT * FROM test_cases
        WHERE suiteId = :suiteId
        AND (
            title LIKE '%' || :query || '%'
            OR description LIKE '%' || :query || '%'
        )
        ORDER BY createdAt DESC
        """
    )
    fun searchTestCases(
        suiteId: Int,
        query: String
    ): Flow<List<TestCaseEntity>>

    @Query("SELECT COUNT(*) FROM test_cases WHERE suiteId = :suiteId")
    fun getTestCaseCount(suiteId: Int): Flow<Int>

    @Query(
        "SELECT COUNT(*) FROM test_cases " +
                "WHERE suiteId = :suiteId AND priority = :priority"
    )
    fun getTestCaseCountByPriority(
        suiteId: Int,
        priority: String
    ): Flow<Int>
}
