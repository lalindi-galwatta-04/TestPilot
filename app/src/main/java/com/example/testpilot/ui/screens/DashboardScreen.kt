
package com.example.testpilot.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.BugReport
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Folder
import androidx.compose.material.icons.filled.Science
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.compose.viewModel

import com.example.testpilot.data.local.RecentActivityItem
import com.example.testpilot.data.local.TestPilotDatabase
import com.example.testpilot.data.repository.AnalyticsRepository
import com.example.testpilot.ui.theme.*
import com.example.testpilot.viewmodel.AnalyticsViewModel

import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun DashboardScreen(
    onNavigate: (String) -> Unit
) {

    val context = LocalContext.current.applicationContext

    // -----------------------------------------
    // DATABASE
    // -----------------------------------------

    val database = remember(context) {
        TestPilotDatabase.getDatabase(context)
    }

    // -----------------------------------------
    // ANALYTICS REPOSITORY
    // -----------------------------------------

    val repository = remember(database) {
        AnalyticsRepository(
            projectDao = database.projectDao(),
            testCaseDao = database.testCaseDao(),
            testExecutionDao = database.testExecutionDao(),
            testExecutionStepDao = database.testExecutionStepDao(),
            defectDao = database.defectDao()
        )
    }

    // -----------------------------------------
    // VIEWMODEL FACTORY
    // -----------------------------------------

    val factory = remember(repository) {
        object : ViewModelProvider.Factory {

            @Suppress("UNCHECKED_CAST")
            override fun <T : ViewModel> create(
                modelClass: Class<T>
            ): T {

                if (
                    modelClass.isAssignableFrom(
                        AnalyticsViewModel::class.java
                    )
                ) {
                    return AnalyticsViewModel(repository) as T
                }

                throw IllegalArgumentException(
                    "Unknown ViewModel class"
                )
            }
        }
    }

    // -----------------------------------------
    // ANALYTICS VIEWMODEL
    // -----------------------------------------

    val analyticsViewModel: AnalyticsViewModel = viewModel(
        key = "dashboard_analytics",
        factory = factory
    )

    // -----------------------------------------
    // GENERAL STATISTICS
    // -----------------------------------------

    val projectCount by
    analyticsViewModel.totalProjects.collectAsState()

    val testCaseCount by
    analyticsViewModel.totalTestCases.collectAsState()

    // -----------------------------------------
    // EXECUTION STATISTICS
    // -----------------------------------------

    val passedCount by
    analyticsViewModel.passedExecutions.collectAsState()

    val failedCount by
    analyticsViewModel.failedExecutions.collectAsState()

    val blockedCount by
    analyticsViewModel.blockedExecutions.collectAsState()

    val inProgressCount by
    analyticsViewModel.inProgressExecutions.collectAsState()

    val totalExecutions by
    analyticsViewModel.totalExecutions.collectAsState()

    val completedExecutions by
    analyticsViewModel.completedExecutions.collectAsState()

    // -----------------------------------------
    // TEST STEP STATISTICS
    // -----------------------------------------

    val notRunStepCount by
    analyticsViewModel.notRunSteps.collectAsState()

    // -----------------------------------------
    // RECENT ACTIVITY
    // -----------------------------------------

    val recentActivities by
    analyticsViewModel.recentActivities.collectAsState()

    // -----------------------------------------
    // DASHBOARD UI
    // -----------------------------------------

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(NavyBackground)
            .verticalScroll(rememberScrollState())
            .padding(20.dp)
    ) {

        // HEADER

        Text(
            text = "TESTPILOT",
            color = PurpleSecondary,
            fontSize = 13.sp,
            fontWeight = FontWeight.Bold,
            letterSpacing = 2.sp
        )

        Spacer(modifier = Modifier.height(12.dp))

        Text(
            text = "WELCOME!",
            color = TextPrimary,
            fontSize = 27.sp,
            fontWeight = FontWeight.Bold
        )

        Spacer(modifier = Modifier.height(5.dp))

        Text(
            text = "Here's your quality overview.",
            color = TextSecondary,
            fontSize = 14.sp
        )

        Spacer(modifier = Modifier.height(25.dp))

        // -------------------------------------
        // QA OVERVIEW
        // -------------------------------------

        SectionTitle("QA Overview")

        Spacer(modifier = Modifier.height(14.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {

            StatCard(
                title = "Projects",
                value = projectCount.toString(),
                icon = Icons.Default.Folder,
                accent = PurpleSecondary,
                modifier = Modifier.weight(1f)
            )

            StatCard(
                title = "Test Cases",
                value = testCaseCount.toString(),
                icon = Icons.Default.Science,
                accent = Color(0xFF38BDF8),
                modifier = Modifier.weight(1f)
            )
        }

        Spacer(modifier = Modifier.height(12.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {

            StatCard(
                title = "Passed",
                value = passedCount.toString(),
                icon = Icons.Default.CheckCircle,
                accent = SuccessGreen,
                modifier = Modifier.weight(1f)
            )

            StatCard(
                title = "Failed",
                value = failedCount.toString(),
                icon = Icons.Default.BugReport,
                accent = FailureRed,
                modifier = Modifier.weight(1f)
            )
        }

        Spacer(modifier = Modifier.height(28.dp))

        // -------------------------------------
        // EXECUTION SUMMARY
        // -------------------------------------

        SectionTitle("Execution Summary")

        Spacer(modifier = Modifier.height(14.dp))

        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(18.dp),
            colors = CardDefaults.cardColors(
                containerColor = NavySurface
            )
        ) {

            Column(
                modifier = Modifier.padding(18.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {

                // Total and completed executions

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {

                    Column {

                        Text(
                            text = "Total executions",
                            color = TextSecondary,
                            fontSize = 12.sp
                        )

                        Text(
                            text = totalExecutions.toString(),
                            color = TextPrimary,
                            fontSize = 27.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    Column(
                        horizontalAlignment = Alignment.End
                    ) {

                        Text(
                            text = "Completed",
                            color = TextSecondary,
                            fontSize = 12.sp
                        )

                        Text(
                            text = completedExecutions.toString(),
                            color = SuccessGreen,
                            fontSize = 27.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }

                HorizontalDivider(
                    color = NavySurfaceVariant
                )

                // Execution status

                Text(
                    text = "Execution status",
                    color = TextPrimary,
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 14.sp
                )

                ExecutionStatusRow(
                    label = "Passed",
                    count = passedCount,
                    color = SuccessGreen
                )

                ExecutionStatusRow(
                    label = "Failed",
                    count = failedCount,
                    color = FailureRed
                )

                ExecutionStatusRow(
                    label = "Blocked",
                    count = blockedCount,
                    color = WarningOrange
                )

                ExecutionStatusRow(
                    label = "In Progress",
                    count = inProgressCount,
                    color = PurpleSecondary
                )

                HorizontalDivider(
                    color = NavySurfaceVariant
                )

                // Not Run test steps

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {

                    Column {

                        Text(
                            text = "Test steps not run",
                            color = TextPrimary,
                            fontWeight = FontWeight.SemiBold,
                            fontSize = 13.sp
                        )

                        Spacer(modifier = Modifier.height(3.dp))

                        Text(
                            text = "Steps awaiting execution",
                            color = TextSecondary,
                            fontSize = 11.sp
                        )
                    }

                    Text(
                        text = notRunStepCount.toString(),
                        color = TextSecondary,
                        fontSize = 22.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(28.dp))

        // -------------------------------------
        // QUICK ACTIONS
        // -------------------------------------

        SectionTitle("Quick Actions")

        Spacer(modifier = Modifier.height(14.dp))

        QuickActionCard(
            title = "Manage Projects",
            description = "Create and organise testing projects",
            icon = Icons.Default.Folder,
            onClick = {
                onNavigate(ScreenRoute.PROJECTS)
            }
        )

        Spacer(modifier = Modifier.height(10.dp))

        QuickActionCard(
            title = "Test Cases",
            description = "Create and manage test scenarios",
            icon = Icons.Default.Science,
            onClick = {
                onNavigate(ScreenRoute.TEST_CASES)
            }
        )

        Spacer(modifier = Modifier.height(10.dp))

        QuickActionCard(
            title = "Report a Defect",
            description = "Record and track software bugs",
            icon = Icons.Default.BugReport,
            onClick = {
                onNavigate(ScreenRoute.DEFECTS)
            }
        )

        Spacer(modifier = Modifier.height(28.dp))

        // -------------------------------------
        // RECENT ACTIVITY
        // -------------------------------------

        SectionTitle("Recent Activity")

        Spacer(modifier = Modifier.height(14.dp))

        if (recentActivities.isEmpty()) {

            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(18.dp),
                colors = CardDefaults.cardColors(
                    containerColor = NavySurface
                )
            ) {

                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {

                    Icon(
                        imageVector = Icons.Default.Science,
                        contentDescription = null,
                        tint = PurpleSecondary,
                        modifier = Modifier.size(35.dp)
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    Text(
                        text = "No recent activity yet",
                        color = TextPrimary,
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 15.sp
                    )

                    Spacer(modifier = Modifier.height(5.dp))

                    Text(
                        text = "Create a project or execute a test case to see activity here.",
                        color = TextSecondary,
                        fontSize = 12.sp
                    )
                }
            }

        } else {

            Column(
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {

                recentActivities.forEach { activity ->

                    RecentActivityCard(
                        activity = activity
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))
    }
}

// =============================================
// SECTION TITLE
// =============================================

@Composable
private fun SectionTitle(
    title: String
) {

    Text(
        text = title,
        color = TextPrimary,
        fontSize = 18.sp,
        fontWeight = FontWeight.Bold
    )
}

// =============================================
// STATISTICS CARD
// =============================================

@Composable
private fun StatCard(
    title: String,
    value: String,
    icon: ImageVector,
    accent: Color,
    modifier: Modifier = Modifier
) {

    Card(
        modifier = modifier,
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(
            containerColor = NavySurface
        )
    ) {

        Column(
            modifier = Modifier.padding(16.dp)
        ) {

            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = accent,
                modifier = Modifier.size(25.dp)
            )

            Spacer(modifier = Modifier.height(15.dp))

            Text(
                text = value,
                color = TextPrimary,
                fontSize = 26.sp,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = title,
                color = TextSecondary,
                fontSize = 13.sp
            )
        }
    }
}

// =============================================
// EXECUTION STATUS ROW
// =============================================

@Composable
private fun ExecutionStatusRow(
    label: String,
    count: Int,
    color: Color
) {

    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {

        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {

            Surface(
                modifier = Modifier.size(9.dp),
                shape = RoundedCornerShape(50),
                color = color
            ) {}

            Text(
                text = label,
                color = TextSecondary,
                fontSize = 13.sp
            )
        }

        Text(
            text = count.toString(),
            color = TextPrimary,
            fontWeight = FontWeight.SemiBold,
            fontSize = 14.sp
        )
    }
}

// =============================================
// QUICK ACTION CARD
// =============================================

@Composable
private fun QuickActionCard(
    title: String,
    description: String,
    icon: ImageVector,
    onClick: () -> Unit
) {

    Card(
        onClick = onClick,
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = NavySurface
        )
    ) {

        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {

            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = PurpleSecondary,
                modifier = Modifier.size(27.dp)
            )

            Spacer(modifier = Modifier.width(14.dp))

            Column(
                modifier = Modifier.weight(1f)
            ) {

                Text(
                    text = title,
                    color = TextPrimary,
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 14.sp
                )

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = description,
                    color = TextSecondary,
                    fontSize = 11.sp
                )
            }

            Icon(
                imageVector = Icons.Default.ArrowForward,
                contentDescription = "Open",
                tint = TextSecondary
            )
        }
    }
}

// =============================================
// RECENT ACTIVITY CARD
// =============================================

@Composable
private fun RecentActivityCard(
    activity: RecentActivityItem
) {

    val isProject = activity.activityType == "PROJECT"

    val displayStatus = if (isProject) {
        "NEW"
    } else {
        activity.status.uppercase()
    }

    val statusColor = when (displayStatus) {
        "PASSED" -> SuccessGreen
        "FAILED" -> FailureRed
        "BLOCKED" -> WarningOrange
        "IN PROGRESS" -> PurpleSecondary
        "NEW" -> PurpleSecondary
        else -> TextSecondary
    }

    val formattedTime = remember(activity.timestamp) {
        SimpleDateFormat(
            "dd MMM yyyy, hh:mm a",
            Locale.getDefault()
        ).format(Date(activity.timestamp))
    }

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = NavySurface
        )
    ) {

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(15.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {

            // Activity indicator

            Surface(
                modifier = Modifier.size(10.dp),
                shape = RoundedCornerShape(50),
                color = statusColor
            ) {}

            Spacer(modifier = Modifier.width(13.dp))

            // Activity information

            Column(
                modifier = Modifier.weight(1f)
            ) {

                Text(
                    text = activity.title,
                    color = TextPrimary,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.SemiBold
                )

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = activity.description,
                    color = TextSecondary,
                    fontSize = 12.sp
                )

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = formattedTime,
                    color = TextSecondary,
                    fontSize = 10.sp
                )
            }

            Spacer(modifier = Modifier.width(8.dp))

            // Status

            Surface(
                shape = RoundedCornerShape(8.dp),
                color = statusColor.copy(alpha = 0.15f)
            ) {

                Text(
                    text = displayStatus,
                    color = statusColor,
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(
                        horizontal = 9.dp,
                        vertical = 6.dp
                    )
                )
            }
        }
    }
}

// =============================================
// NAVIGATION ROUTES
// =============================================

private object ScreenRoute {

    const val PROJECTS = "projects"
    const val TEST_CASES = "test_cases"
    const val DEFECTS = "defects"
}
