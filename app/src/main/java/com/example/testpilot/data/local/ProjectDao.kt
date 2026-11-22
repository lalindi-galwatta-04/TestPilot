
package com.example.testpilot.data.local

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface ProjectDao {

    @Insert(onConflict = OnConflictStrategy.ABORT)
    suspend fun insertProject(project: ProjectEntity): Long

    @Update
    suspend fun updateProject(project: ProjectEntity)

    @Delete
    suspend fun deleteProject(project: ProjectEntity)

    @Query("SELECT * FROM projects ORDER BY createdAt DESC")
    fun getAllProjects(): Flow<List<ProjectEntity>>

    @Query("SELECT * FROM projects WHERE id = :projectId")
    fun getProjectById(projectId: Int): Flow<ProjectEntity?>

    @Query("""
        SELECT * FROM projects
        WHERE projectName LIKE '%' || :searchQuery || '%'
        OR applicationName LIKE '%' || :searchQuery || '%'
        ORDER BY createdAt DESC
    """)
    fun searchProjects(searchQuery: String): Flow<List<ProjectEntity>>

    @Query("SELECT * FROM projects WHERE status = :status ORDER BY createdAt DESC")
    fun getProjectsByStatus(status: String): Flow<List<ProjectEntity>>

    @Query("SELECT COUNT(*) FROM projects")
    fun getProjectCount(): Flow<Int>


    // --------------------------------------------
    // PROJECT-WISE QUALITY ANALYTICS
    // --------------------------------------------

    @Query(
        """
        SELECT
            p.id AS projectId,
            p.projectName AS projectName,

            COALESCE(tc_stats.totalTestCases, 0) AS totalTestCases,

            COALESCE(exec_stats.totalExecutions, 0) AS totalExecutions,

            COALESCE(exec_stats.passedExecutions, 0) AS passedExecutions,

            COALESCE(exec_stats.failedExecutions, 0) AS failedExecutions,
            
            COALESCE(exec_stats.blockedExecutions, 0) AS blockedExecutions,

            COALESCE(defect_stats.totalDefects, 0) AS totalDefects,

            COALESCE(defect_stats.openDefects, 0) AS openDefects

        FROM projects p

        LEFT JOIN (
            SELECT
                ts.projectId,
                COUNT(tc.id) AS totalTestCases
            FROM test_suites ts
            LEFT JOIN test_cases tc
                ON tc.suiteId = ts.id
            GROUP BY ts.projectId
        ) AS tc_stats
            ON tc_stats.projectId = p.id

        LEFT JOIN (
            SELECT
                ts.projectId,
                COUNT(te.id) AS totalExecutions,
                SUM(CASE WHEN te.status = 'Passed' THEN 1 ELSE 0 END)
                    AS passedExecutions,
                SUM(CASE WHEN te.status = 'Failed' THEN 1 ELSE 0 END)
                    AS failedExecutions,
                SUM(CASE WHEN te.status = 'Blocked' THEN 1 ELSE 0 END)
                    AS blockedExecutions
            FROM test_suites ts
            INNER JOIN test_cases tc
                ON tc.suiteId = ts.id
            INNER JOIN test_executions te
                ON te.testCaseId = tc.id
            GROUP BY ts.projectId
        ) AS exec_stats
            ON exec_stats.projectId = p.id

        LEFT JOIN (
            SELECT
                projectId,
                COUNT(id) AS totalDefects,
                SUM(CASE WHEN status = 'Open' THEN 1 ELSE 0 END)
                    AS openDefects
            FROM defects
            GROUP BY projectId
        ) AS defect_stats
            ON defect_stats.projectId = p.id

        ORDER BY p.createdAt DESC
        """
    )
    fun getProjectQualityOverview(): Flow<List<ProjectQualityOverview>>

}
