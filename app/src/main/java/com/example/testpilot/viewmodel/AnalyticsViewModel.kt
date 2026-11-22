
package com.example.testpilot.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.testpilot.data.repository.AnalyticsRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import com.example.testpilot.data.local.ProjectQualityOverview
import com.example.testpilot.data.local.RecentActivityItem

class AnalyticsViewModel(
    private val repository: AnalyticsRepository
) : ViewModel() {

    private val sharingStarted = SharingStarted.WhileSubscribed(5000)

    // -----------------------------------------
    // GENERAL STATISTICS
    // -----------------------------------------

    val totalProjects: StateFlow<Int> =
        repository.getTotalProjectCount()
            .stateIn(viewModelScope, sharingStarted, 0)

    val totalTestCases: StateFlow<Int> =
        repository.getTotalTestCaseCount()
            .stateIn(viewModelScope, sharingStarted, 0)

    val totalExecutions: StateFlow<Int> =
        repository.getTotalExecutionCount()
            .stateIn(viewModelScope, sharingStarted, 0)

    val totalDefects: StateFlow<Int> =
        repository.getTotalDefectCount()
            .stateIn(viewModelScope, sharingStarted, 0)


    // Recent dashboard activity
    val recentActivities: StateFlow<List<RecentActivityItem>> =
        repository.getRecentActivities()
            .stateIn(
                viewModelScope,
                sharingStarted,
                emptyList()
            )



    // -----------------------------------------
    // EXECUTION STATISTICS
    // -----------------------------------------

    val passedExecutions: StateFlow<Int> =
        repository.getPassedExecutionCount()
            .stateIn(viewModelScope, sharingStarted, 0)

    val failedExecutions: StateFlow<Int> =
        repository.getFailedExecutionCount()
            .stateIn(viewModelScope, sharingStarted, 0)

    val blockedExecutions: StateFlow<Int> =
        repository.getBlockedExecutionCount()
            .stateIn(viewModelScope, sharingStarted, 0)

    val completedExecutions: StateFlow<Int> =
        repository.getCompletedExecutionCount()
            .stateIn(viewModelScope, sharingStarted, 0)

    val inProgressExecutions: StateFlow<Int> =
        repository.getInProgressExecutionCount()
            .stateIn(viewModelScope, sharingStarted, 0)


    // -----------------------------------------
    // PASS RATE
    // -----------------------------------------

    val passRate: StateFlow<Float> =
        combine(passedExecutions, completedExecutions) { passed, completed ->
            if (completed > 0) {
                (passed.toFloat() / completed.toFloat()) * 100f
            } else {
                0f
            }
        }.stateIn(viewModelScope, sharingStarted, 0f)


    // -----------------------------------------
    // TEST STEP STATISTICS
    // -----------------------------------------

    val totalExecutionSteps: StateFlow<Int> =
        repository.getTotalExecutionStepCount()
            .stateIn(viewModelScope, sharingStarted, 0)

    val passedSteps: StateFlow<Int> =
        repository.getPassedStepCount()
            .stateIn(viewModelScope, sharingStarted, 0)

    val failedSteps: StateFlow<Int> =
        repository.getFailedStepCount()
            .stateIn(viewModelScope, sharingStarted, 0)

    val blockedSteps: StateFlow<Int> =
        repository.getBlockedStepCount()
            .stateIn(viewModelScope, sharingStarted, 0)

    val testedSteps: StateFlow<Int> =
        repository.getTestedStepCount()
            .stateIn(viewModelScope, sharingStarted, 0)

    val notRunSteps: StateFlow<Int> =
        repository.getNotRunStepCount()
            .stateIn(viewModelScope, sharingStarted, 0)


    // -----------------------------------------
    // DEFECT STATUS STATISTICS
    // -----------------------------------------

    val openDefects: StateFlow<Int> =
        repository.getOpenDefectCount()
            .stateIn(viewModelScope, sharingStarted, 0)

    val inProgressDefects: StateFlow<Int> =
        repository.getInProgressDefectCount()
            .stateIn(viewModelScope, sharingStarted, 0)

    val resolvedDefects: StateFlow<Int> =
        repository.getResolvedDefectCount()
            .stateIn(viewModelScope, sharingStarted, 0)

    val closedDefects: StateFlow<Int> =
        repository.getClosedDefectCount()
            .stateIn(viewModelScope, sharingStarted, 0)


    // -----------------------------------------
    // DEFECT SEVERITY STATISTICS
    // -----------------------------------------

    val criticalDefects: StateFlow<Int> =
        repository.getCriticalDefectCount()
            .stateIn(viewModelScope, sharingStarted, 0)

    val highDefects: StateFlow<Int> =
        repository.getHighDefectCount()
            .stateIn(viewModelScope, sharingStarted, 0)

    val mediumDefects: StateFlow<Int> =
        repository.getMediumDefectCount()
            .stateIn(viewModelScope, sharingStarted, 0)

    val lowDefects: StateFlow<Int> =
        repository.getLowDefectCount()
            .stateIn(viewModelScope, sharingStarted, 0)

    // -----------------------------------------
    // PROJECT-WISE QUALITY ANALYTICS
    // -----------------------------------------

    val projectQualityOverview: StateFlow<List<ProjectQualityOverview>> =
        repository.getProjectQualityOverview()
            .stateIn(
                viewModelScope,
                sharingStarted,
                emptyList()
            )
}
