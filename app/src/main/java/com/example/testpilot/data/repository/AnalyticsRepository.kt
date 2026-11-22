
package com.example.testpilot.data.repository

import com.example.testpilot.data.local.DefectDao
import com.example.testpilot.data.local.ProjectDao
import com.example.testpilot.data.local.TestCaseDao
import com.example.testpilot.data.local.TestExecutionDao
import com.example.testpilot.data.local.TestExecutionStepDao
import com.example.testpilot.data.local.ProjectQualityOverview
import kotlinx.coroutines.flow.Flow

class AnalyticsRepository(
    private val projectDao: ProjectDao,
    private val testCaseDao: TestCaseDao,
    private val testExecutionDao: TestExecutionDao,
    private val testExecutionStepDao: TestExecutionStepDao,
    private val defectDao: DefectDao
) {

    // ------------------------------------------------
    // GENERAL STATISTICS
    // ------------------------------------------------

    fun getTotalProjectCount() =
        projectDao.getProjectCount()

    fun getTotalTestCaseCount() =
        testCaseDao.getTotalTestCaseCount()

    fun getTotalExecutionCount() =
        testExecutionDao.getTotalExecutionCount()

    fun getTotalDefectCount() =
        defectDao.getDefectCount()


    // Recent dashboard activity
    fun getRecentActivities() =
        testExecutionDao.getRecentActivities()



    // ------------------------------------------------
    // TEST EXECUTION STATISTICS
    // ------------------------------------------------

    fun getPassedExecutionCount() =
        testExecutionDao.getExecutionCountByStatus("Passed")

    fun getFailedExecutionCount() =
        testExecutionDao.getExecutionCountByStatus("Failed")

    fun getBlockedExecutionCount() =
        testExecutionDao.getExecutionCountByStatus("Blocked")

    fun getCompletedExecutionCount() =
        testExecutionDao.getCompletedExecutionCount()

    fun getInProgressExecutionCount() =
        testExecutionDao.getExecutionCountByStatus("In Progress")


    // ------------------------------------------------
    // TEST STEP STATISTICS
    // ------------------------------------------------

    fun getTotalExecutionStepCount() =
        testExecutionStepDao.getTotalExecutionStepCount()

    fun getPassedStepCount() =
        testExecutionStepDao.getExecutionStepCountByStatus("Pass")

    fun getFailedStepCount() =
        testExecutionStepDao.getExecutionStepCountByStatus("Fail")

    fun getBlockedStepCount() =
        testExecutionStepDao.getExecutionStepCountByStatus("Blocked")

    fun getTestedStepCount() =
        testExecutionStepDao.getTestedStepCount()

    fun getNotRunStepCount() =
        testExecutionStepDao.getNotRunStepCount()


    // ------------------------------------------------
    // DEFECT STATUS STATISTICS
    // ------------------------------------------------

    fun getOpenDefectCount() =
        defectDao.getDefectCountByStatus("Open")

    fun getInProgressDefectCount() =
        defectDao.getDefectCountByStatus("In Progress")

    fun getResolvedDefectCount() =
        defectDao.getDefectCountByStatus("Resolved")

    fun getClosedDefectCount() =
        defectDao.getDefectCountByStatus("Closed")


    // ------------------------------------------------
    // DEFECT SEVERITY STATISTICS
    // ------------------------------------------------

    fun getCriticalDefectCount() =
        defectDao.getDefectCountBySeverity("Critical")

    fun getHighDefectCount() =
        defectDao.getDefectCountBySeverity("High")

    fun getMediumDefectCount() =
        defectDao.getDefectCountBySeverity("Medium")

    fun getLowDefectCount() =
        defectDao.getDefectCountBySeverity("Low")

    // --------------------------------------------
    // PROJECT-WISE QUALITY ANALYTICS
    // --------------------------------------------

    fun getProjectQualityOverview(): Flow<List<ProjectQualityOverview>> =
        projectDao.getProjectQualityOverview()
}
