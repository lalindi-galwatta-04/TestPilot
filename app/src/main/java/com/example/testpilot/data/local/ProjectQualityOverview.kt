
package com.example.testpilot.data.local

data class ProjectQualityOverview(
    val projectId: Int,
    val projectName: String,
    val totalTestCases: Int,
    val totalExecutions: Int,
    val passedExecutions: Int,
    val failedExecutions: Int,
    val blockedExecutions: Int,
    val totalDefects: Int,
    val openDefects: Int
) {
    val completedExecutions: Int
        get() = passedExecutions + failedExecutions + blockedExecutions

    val passRate: Float
        get() = if (completedExecutions > 0) {
            (passedExecutions.toFloat() / completedExecutions) * 100f
        } else {
            0f
        }
}
