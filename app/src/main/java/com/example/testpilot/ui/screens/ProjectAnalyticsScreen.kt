
package com.example.testpilot.ui.screens

import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.testpilot.data.local.DefectEntity
import com.example.testpilot.data.local.ProjectEntity
import com.example.testpilot.data.local.TestExecutionEntity
import com.example.testpilot.data.local.TestExecutionStepEntity
import com.example.testpilot.data.local.TestPilotDatabase
import com.example.testpilot.ui.theme.NavyBackground
import com.example.testpilot.ui.theme.NavySurface
import com.example.testpilot.ui.theme.PurplePrimary
import com.example.testpilot.ui.theme.PurpleSecondary
import com.example.testpilot.ui.theme.TextPrimary
import com.example.testpilot.ui.theme.TextSecondary
import com.example.testpilot.utils.ProjectAnalyticsPdfGenerator
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

private data class ProjectAnalyticsData(
    val project: ProjectEntity,
    val totalTestCases: Int,
    val executions: List<TestExecutionEntity>,
    val executionSteps: List<TestExecutionStepEntity>,
    val defects: List<DefectEntity>
) {
    val passedExecutions get() = executions.count { it.status == "Passed" }
    val failedExecutions get() = executions.count { it.status == "Failed" }
    val blockedExecutions get() = executions.count { it.status == "Blocked" }
    val inProgressExecutions get() = executions.count { it.status == "In Progress" }

    val completedExecutions
        get() = passedExecutions + failedExecutions + blockedExecutions

    val passRate: Float
        get() = if (completedExecutions > 0) {
            passedExecutions.toFloat() / completedExecutions * 100f
        } else {
            0f
        }

    val passedSteps get() = executionSteps.count { it.status == "Pass" }
    val failedSteps get() = executionSteps.count { it.status == "Fail" }
    val blockedSteps get() = executionSteps.count { it.status == "Blocked" }
    val notRunSteps get() = executionSteps.count { it.status == "Not Run" }

    val openDefects get() = defects.count { it.status == "Open" }
    val inProgressDefects get() = defects.count { it.status == "In Progress" }
    val resolvedDefects get() = defects.count { it.status == "Resolved" }
    val closedDefects get() = defects.count { it.status == "Closed" }

    val criticalDefects get() = defects.count { it.severity == "Critical" }
    val highDefects get() = defects.count { it.severity == "High" }
    val mediumDefects get() = defects.count { it.severity == "Medium" }
    val lowDefects get() = defects.count { it.severity == "Low" }
}

@Composable
fun ProjectAnalyticsScreen(
    projectId: Int,
    onBack: () -> Unit
) {
    val context = LocalContext.current
    val applicationContext = context.applicationContext
    val scope = rememberCoroutineScope()

    val database = remember(applicationContext) {
        TestPilotDatabase.getDatabase(applicationContext)
    }

    var analyticsData by remember {
        mutableStateOf<ProjectAnalyticsData?>(null)
    }

    var isLoading by remember {
        mutableStateOf(true)
    }

    var loadError by remember {
        mutableStateOf<String?>(null)
    }

    var isGeneratingReport by remember {
        mutableStateOf(false)
    }

    val pdfLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.CreateDocument("application/pdf")
    ) { uri ->

        if (uri != null) {
            val reportData = analyticsData

            if (reportData != null) {
                scope.launch {
                    isGeneratingReport = true

                    try {
                        withContext(Dispatchers.IO) {
                            ProjectAnalyticsPdfGenerator.generate(
                                context = applicationContext,
                                uri = uri,
                                project = reportData.project,
                                totalTestCases = reportData.totalTestCases,
                                totalExecutions = reportData.executions.size,
                                passedExecutions = reportData.passedExecutions,
                                failedExecutions = reportData.failedExecutions,
                                blockedExecutions = reportData.blockedExecutions,
                                inProgressExecutions = reportData.inProgressExecutions,
                                notRunExecutions = reportData.executions.count {
                                    it.status == "Not Run"
                                },
                                passRate = reportData.passRate,
                                totalSteps = reportData.executionSteps.size,
                                passedSteps = reportData.passedSteps,
                                failedSteps = reportData.failedSteps,
                                blockedSteps = reportData.blockedSteps,
                                notRunSteps = reportData.notRunSteps,
                                defects = reportData.defects
                            )
                        }

                        Toast.makeText(
                            context,
                            "Analytics report saved successfully!",
                            Toast.LENGTH_LONG
                        ).show()

                    } catch (exception: Exception) {
                        Toast.makeText(
                            context,
                            "Unable to generate PDF: ${exception.message}",
                            Toast.LENGTH_LONG
                        ).show()

                    } finally {
                        isGeneratingReport = false
                    }
                }
            }
        }
    }

    LaunchedEffect(projectId, database) {
        isLoading = true
        loadError = null
        analyticsData = null

        try {
            analyticsData = withContext(Dispatchers.IO) {

                val project = database.projectDao()
                    .getProjectById(projectId)
                    .first()
                    ?: throw IllegalStateException("Project not found.")

                val suites = database.testSuiteDao()
                    .getSuitesByProject(projectId)
                    .first()

                var totalTestCases = 0

                val allExecutions = mutableListOf<TestExecutionEntity>()
                val allExecutionSteps = mutableListOf<TestExecutionStepEntity>()

                for (suite in suites) {
                    val testCases = database.testCaseDao()
                        .getTestCasesBySuite(suite.id)
                        .first()

                    totalTestCases += testCases.size

                    for (testCase in testCases) {
                        val executions = database.testExecutionDao()
                            .getExecutionsByTestCase(testCase.id)
                            .first()

                        allExecutions.addAll(executions)

                        for (execution in executions) {
                            val steps = database.testExecutionStepDao()
                                .getStepsByExecution(execution.id)
                                .first()

                            allExecutionSteps.addAll(steps)
                        }
                    }
                }

                val defects = database.defectDao()
                    .getDefectsByProject(projectId)
                    .first()

                ProjectAnalyticsData(
                    project = project,
                    totalTestCases = totalTestCases,
                    executions = allExecutions,
                    executionSteps = allExecutionSteps,
                    defects = defects
                )
            }

        } catch (exception: Exception) {
            loadError = exception.message
                ?: "Unable to load project analytics."

        } finally {
            isLoading = false
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(NavyBackground)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 12.dp, vertical = 10.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = onBack) {
                Icon(
                    imageVector = Icons.Default.ArrowBack,
                    contentDescription = "Back",
                    tint = TextPrimary
                )
            }

            Column {
                Text(
                    text = "Project Analytics",
                    color = TextPrimary,
                    fontSize = 21.sp,
                    fontWeight = FontWeight.Bold
                )

                Text(
                    text = "Quality performance overview",
                    color = TextSecondary,
                    fontSize = 12.sp
                )
            }
        }

        when {
            isLoading -> {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(color = PurplePrimary)
                }
            }

            loadError != null -> {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(24.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = loadError ?: "Something went wrong.",
                        color = TextPrimary
                    )
                }
            }

            analyticsData != null -> {
                ProjectAnalyticsContent(
                    data = analyticsData!!,
                    isGeneratingReport = isGeneratingReport,
                    onDownloadReport = {
                        val safeName = analyticsData!!.project.projectName
                            .replace(Regex("[^a-zA-Z0-9_-]"), "_")

                        pdfLauncher.launch(
                            "TestPilot_${safeName}_Analytics.pdf"
                        )
                    }
                )
            }
        }
    }
}

@Composable
private fun ProjectAnalyticsContent(
    data: ProjectAnalyticsData,
    isGeneratingReport: Boolean,
    onDownloadReport: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 16.dp)
            .padding(bottom = 28.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        ProjectInformationCard(data.project)

        Text(
            text = "Project Overview",
            color = TextPrimary,
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold
        )

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            MetricCard(
                title = "Test Cases",
                value = data.totalTestCases.toString(),
                modifier = Modifier.weight(1f)
            )

            MetricCard(
                title = "Executions",
                value = data.executions.size.toString(),
                modifier = Modifier.weight(1f)
            )
        }

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            MetricCard(
                title = "Total Defects",
                value = data.defects.size.toString(),
                modifier = Modifier.weight(1f)
            )

            MetricCard(
                title = "Pass Rate",
                value = String.format("%.1f%%", data.passRate),
                modifier = Modifier.weight(1f)
            )
        }

        Text(
            text = "Test Execution Analytics",
            color = TextPrimary,
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold
        )

        AnalyticsPieChartCard(
            title = "Execution Status",
            items = listOf(
                AnalyticsChartItem("Passed", data.passedExecutions, Color(0xFF35C98A)),
                AnalyticsChartItem("Failed", data.failedExecutions, Color(0xFFFF5C6C)),
                AnalyticsChartItem("Blocked", data.blockedExecutions, Color(0xFFFFB547)),
                AnalyticsChartItem("In Progress", data.inProgressExecutions, Color(0xFF7986CB))
            )
        )

        Text(
            text = "Test Step Performance",
            color = TextPrimary,
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold
        )

        AnalyticsDistributionCard(
            title = "Test Step Results",
            items = listOf(
                AnalyticsChartItem("Passed", data.passedSteps, Color(0xFF35C98A)),
                AnalyticsChartItem("Failed", data.failedSteps, Color(0xFFFF5C6C)),
                AnalyticsChartItem("Blocked", data.blockedSteps, Color(0xFFFFB547)),
                AnalyticsChartItem("Not Run", data.notRunSteps, Color(0xFF7986CB))
            )
        )

        Text(
            text = "Defect Analytics",
            color = TextPrimary,
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold
        )

        AnalyticsPieChartCard(
            title = "Defect Status",
            items = listOf(
                AnalyticsChartItem("Open", data.openDefects, Color(0xFFFF5C6C)),
                AnalyticsChartItem("In Progress", data.inProgressDefects, Color(0xFFFFB547)),
                AnalyticsChartItem("Resolved", data.resolvedDefects, Color(0xFF35C98A)),
                AnalyticsChartItem("Closed", data.closedDefects, Color(0xFF7986CB))
            )
        )

        AnalyticsPieChartCard(
            title = "Defect Severity",
            items = listOf(
                AnalyticsChartItem("Critical", data.criticalDefects, Color(0xFFFF3B4F)),
                AnalyticsChartItem("High", data.highDefects, Color(0xFFFF8A45)),
                AnalyticsChartItem("Medium", data.mediumDefects, Color(0xFFFFC857)),
                AnalyticsChartItem("Low", data.lowDefects, Color(0xFF35C98A))
            )
        )

        // PDF REPORT DOWNLOAD

        Button(
            onClick = onDownloadReport,
            enabled = !isGeneratingReport,
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 8.dp),
            shape = RoundedCornerShape(14.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = PurplePrimary,
                contentColor = Color.White,
                disabledContainerColor = PurplePrimary.copy(alpha = 0.5f)
            )
        ) {
            Text(
                text = if (isGeneratingReport) {
                    "Preparing Report..."
                } else {
                    "Download Analytics Report (PDF)"
                },
                modifier = Modifier.padding(vertical = 8.dp),
                fontWeight = FontWeight.Bold
            )
        }
    }
}

@Composable
private fun ProjectInformationCard(
    project: ProjectEntity
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = NavySurface)
    ) {
        Column(
            modifier = Modifier.padding(18.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text(
                text = project.projectName,
                color = TextPrimary,
                fontSize = 21.sp,
                fontWeight = FontWeight.Bold
            )

            Text(
                text = project.applicationName,
                color = PurpleSecondary,
                fontSize = 14.sp,
                fontWeight = FontWeight.SemiBold
            )

            Text(
                text = "Version: ${project.version}",
                color = TextSecondary,
                fontSize = 13.sp
            )

            Text(
                text = "Status: ${project.status}",
                color = TextSecondary,
                fontSize = 13.sp
            )

            if (project.description.isNotBlank()) {
                Text(
                    text = project.description,
                    color = TextSecondary,
                    fontSize = 13.sp
                )
            }
        }
    }
}

@Composable
private fun MetricCard(
    title: String,
    value: String,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = NavySurface)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text(
                text = title,
                color = TextSecondary,
                fontSize = 12.sp
            )

            Text(
                text = value,
                color = PurpleSecondary,
                fontSize = 23.sp,
                fontWeight = FontWeight.Bold
            )
        }
    }
}
