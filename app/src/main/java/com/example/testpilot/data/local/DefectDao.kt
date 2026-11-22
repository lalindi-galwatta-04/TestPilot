
package com.example.testpilot.data.local

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface DefectDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertDefect(defect: DefectEntity): Long

    @Update
    suspend fun updateDefect(defect: DefectEntity)

    @Delete
    suspend fun deleteDefect(defect: DefectEntity)

    @Query("SELECT * FROM defects ORDER BY reportedAt DESC")
    fun getAllDefects(): Flow<List<DefectEntity>>

    @Query("""
        SELECT * FROM defects
        WHERE projectId = :projectId
        ORDER BY reportedAt DESC
    """)
    fun getDefectsByProject(projectId: Int): Flow<List<DefectEntity>>

    @Query("SELECT * FROM defects WHERE id = :defectId")
    fun getDefectById(defectId: Int): Flow<DefectEntity?>

    @Query("""
        SELECT * FROM defects
        WHERE status = :status
        ORDER BY reportedAt DESC
    """)
    fun getDefectsByStatus(status: String): Flow<List<DefectEntity>>

    @Query("""
        SELECT * FROM defects
        WHERE severity = :severity
        ORDER BY reportedAt DESC
    """)
    fun getDefectsBySeverity(severity: String): Flow<List<DefectEntity>>

    @Query("""
        SELECT * FROM defects
        WHERE title LIKE '%' || :searchQuery || '%'
        OR description LIKE '%' || :searchQuery || '%'
        ORDER BY reportedAt DESC
    """)
    fun searchDefects(searchQuery: String): Flow<List<DefectEntity>>

    @Query("SELECT COUNT(*) FROM defects")
    fun getDefectCount(): Flow<Int>

    @Query("SELECT COUNT(*) FROM defects WHERE status = :status")
    fun getDefectCountByStatus(status: String): Flow<Int>

    @Query("SELECT COUNT(*) FROM defects WHERE severity = :severity")
    fun getDefectCountBySeverity(severity: String): Flow<Int>
}
