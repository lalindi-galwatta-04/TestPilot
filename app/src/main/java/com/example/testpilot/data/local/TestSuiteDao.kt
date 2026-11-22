
package com.example.testpilot.data.local

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface TestSuiteDao {

    @Insert(onConflict = OnConflictStrategy.ABORT)
    suspend fun insertSuite(suite: TestSuiteEntity): Long

    @Update
    suspend fun updateSuite(suite: TestSuiteEntity)

    @Delete
    suspend fun deleteSuite(suite: TestSuiteEntity)

    @Query(
        "SELECT * FROM test_suites " +
                "WHERE projectId = :projectId ORDER BY createdAt DESC"
    )
    fun getSuitesByProject(projectId: Int): Flow<List<TestSuiteEntity>>

    @Query("SELECT * FROM test_suites WHERE id = :suiteId")
    fun getSuiteById(suiteId: Int): Flow<TestSuiteEntity?>

    @Query("SELECT COUNT(*) FROM test_suites WHERE projectId = :projectId")
    fun getSuiteCount(projectId: Int): Flow<Int>
}
