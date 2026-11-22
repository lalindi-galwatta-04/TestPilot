package com.example.testpilot.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.History
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import kotlinx.coroutines.launch
import kotlinx.coroutines.flow.first

import com.example.testpilot.data.local.TestExecutionEntity
import com.example.testpilot.data.local.DefectEntity
import com.example.testpilot.data.local.TestExecutionStepEntity
import com.example.testpilot.data.local.TestPilotDatabase
import com.example.testpilot.data.repository.TestExecutionRepository
import com.example.testpilot.data.repository.DefectRepository
import com.example.testpilot.ui.theme.*
import com.example.testpilot.viewmodel.TestExecutionViewModel
import com.example.testpilot.viewmodel.TestExecutionViewModelFactory

@Composable
fun TestExecutionScreen(
    testCaseId: Int,
    onBack: () -> Unit
) {

    val context = LocalContext.current.applicationContext

    val database = remember(context) {
        TestPilotDatabase.getDatabase(context)
    }

    val repository = remember(database) {
        TestExecutionRepository(
            executionDao = database.testExecutionDao(),
            executionStepDao = database.testExecutionStepDao()
        )
    }

    val factory = remember(repository) {
        TestExecutionViewModelFactory(repository)
    }

    val executionViewModel: TestExecutionViewModel = viewModel(
        key = "test_execution_$testCaseId",
        factory = factory
    )

    val sourceStepsFlow = remember(database, testCaseId) {
        database.testStepDao().getStepsByTestCase(testCaseId)
    }

    val sourceSteps by sourceStepsFlow.collectAsState(
        initial = emptyList()
    )

    val executions by executionViewModel.executions.collectAsState()
    val executionSteps by executionViewModel.executionSteps.collectAsState()
    val currentExecution by executionViewModel.currentExecution.collectAsState()
    val executionCount by executionViewModel.executionCount.collectAsState()
    val completionMessage by executionViewModel.completionMessage.collectAsState()
    val stepSaveMessage by executionViewModel.stepSaveMessage.collectAsState()

    var showHistory by remember {
        mutableStateOf(false)
    }

    var showNotesDialog by remember {
        mutableStateOf(false)
    }

    var executionNotes by remember {
        mutableStateOf("")
    }

    var selectedDefectStep by remember {
        mutableStateOf<TestExecutionStepEntity?>(null)
    }

    var selectedDefectActualResult by remember {
        mutableStateOf("")
    }

    val coroutineScope = rememberCoroutineScope()

    val snackbarHostState = remember {
        SnackbarHostState()
    }

    // Load execution history
    LaunchedEffect(testCaseId) {
        executionViewModel.loadExecutions(testCaseId)
    }

    // Completion feedback
    LaunchedEffect(completionMessage) {
        completionMessage?.let { message ->
            snackbarHostState.showSnackbar(message)
            executionViewModel.clearCompletionMessage()
        }
    }

    // Actual result save feedback
    LaunchedEffect(stepSaveMessage) {
        stepSaveMessage?.let { message ->
            snackbarHostState.showSnackbar(message)
            executionViewModel.clearStepSaveMessage()
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(NavyBackground)
    ) {

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(20.dp)
        ) {

            // Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {

                IconButton(onClick = onBack) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Back",
                        tint = TextPrimary
                    )
                }

                Column(
                    modifier = Modifier.weight(1f)
                ) {
                    Text(
                        text = "Test Execution",
                        color = TextPrimary,
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Bold
                    )

                    Text(
                        text = "Test Case #$testCaseId",
                        color = TextSecondary,
                        fontSize = 13.sp
                    )
                }

                IconButton(
                    onClick = {
                        showHistory = !showHistory
                    }
                ) {
                    Icon(
                        imageVector = Icons.Default.History,
                        contentDescription = "Execution history",
                        tint = PurpleSecondary
                    )
                }
            }

            Spacer(modifier = Modifier.height(18.dp))

            if (showHistory) {

                // EXECUTION HISTORY
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Execution History",
                        color = TextPrimary,
                        fontSize = 19.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.weight(1f)
                    )

                    TextButton(
                        onClick = {
                            showHistory = false
                        }
                    ) {
                        Text("Back")
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                if (executions.isEmpty()) {

                    EmptyExecutionMessage(
                        message = "No execution history yet."
                    )

                } else {

                    LazyColumn(
                        modifier = Modifier.weight(1f),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        items(
                            items = executions,
                            key = { it.id }
                        ) { execution ->

                            ExecutionHistoryCard(
                                execution = execution,
                                onClick = {
                                    executionViewModel.loadExecution(
                                        execution.id
                                    )
                                    showHistory = false
                                }
                            )
                        }
                    }
                }

            } else if (currentExecution == null) {

                // START NEW EXECUTION
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(18.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = NavySurface
                    )
                ) {

                    Column(
                        modifier = Modifier.padding(20.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {

                        Text(
                            text = "Ready to Test?",
                            color = TextPrimary,
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold
                        )

                        Spacer(modifier = Modifier.height(10.dp))

                        Text(
                            text = "${sourceSteps.size} testing steps available",
                            color = TextSecondary,
                            fontSize = 14.sp
                        )

                        Spacer(modifier = Modifier.height(18.dp))

                        Button(
                            onClick = {
                                executionViewModel.startExecution(
                                    testCaseId = testCaseId,
                                    sourceSteps = sourceSteps
                                )
                            },
                            enabled = sourceSteps.isNotEmpty(),
                            modifier = Modifier.fillMaxWidth(),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = PurplePrimary
                            )
                        ) {

                            Icon(
                                imageVector = Icons.Default.Add,
                                contentDescription = null
                            )

                            Spacer(modifier = Modifier.width(8.dp))

                            Text("Start New Execution")
                        }

                        if (sourceSteps.isEmpty()) {

                            Spacer(modifier = Modifier.height(8.dp))

                            Text(
                                text = "Add test steps before starting an execution.",
                                color = WarningOrange,
                                fontSize = 12.sp
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = "Previous Executions: $executionCount",
                    color = TextSecondary,
                    fontSize = 14.sp
                )

                if (executions.isNotEmpty()) {
                    TextButton(
                        onClick = {
                            showHistory = true
                        }
                    ) {
                        Text("View Execution History")
                    }
                }

                Spacer(modifier = Modifier.weight(1f))

            } else {

                // CURRENT OR HISTORICAL EXECUTION
                val execution = currentExecution!!
                val isInProgress = execution.status == "In Progress"

                val testedSteps = executionSteps.count {
                    it.status != "Not Run"
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {

                    Column(
                        modifier = Modifier.weight(1f)
                    ) {
                        Text(
                            text = "Execution #${execution.id}",
                            color = TextPrimary,
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold
                        )

                        Text(
                            text = "$testedSteps of ${executionSteps.size} steps tested",
                            color = TextSecondary,
                            fontSize = 12.sp
                        )
                    }

                    ExecutionStatusBadge(execution.status)
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Completed execution information
                if (!isInProgress) {

                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = SuccessGreen.copy(alpha = 0.12f)
                        )
                    ) {

                        Column(
                            modifier = Modifier.padding(16.dp)
                        ) {

                            Text(
                                text = when (execution.status) {
                                    "Passed", "Pass" ->
                                        "✓ Execution Passed"

                                    "Failed", "Fail" ->
                                        "✕ Execution Failed"

                                    "Blocked" ->
                                        "⚠ Execution Blocked"

                                    else ->
                                        "Execution Completed"
                                },
                                color = TextPrimary,
                                fontWeight = FontWeight.Bold,
                                fontSize = 17.sp
                            )

                            Spacer(modifier = Modifier.height(6.dp))

                            Text(
                                text = "This execution is completed. Its results are available in read-only mode.",
                                color = TextSecondary,
                                fontSize = 13.sp
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))
                }

                if (executionSteps.isEmpty()) {

                    EmptyExecutionMessage(
                        message = "No steps were saved for this execution."
                    )

                } else {

                    LazyColumn(
                        modifier = Modifier.weight(1f),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {

                        items(
                            items = executionSteps,
                            key = { it.id }
                        ) { step ->

                            ExecutionStepCard(
                                step = step,
                                editable = isInProgress,

                                onSaveActualResult = { actual ->
                                    executionViewModel.saveActualResult(
                                        step = step,
                                        actualResult = actual
                                    )
                                },

                                onStatusChange = { status ->
                                    executionViewModel.updateExecutionStep(
                                        step.copy(status = status)
                                    )
                                },

                                onReportDefect = { actual ->
                                    selectedDefectStep = step
                                    selectedDefectActualResult = actual
                                }
                            )
                        }
                    }
                }

                if (isInProgress) {

                    Spacer(modifier = Modifier.height(12.dp))

                    OutlinedButton(
                        onClick = {
                            executionNotes = execution.notes
                            showNotesDialog = true
                        },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("Add Execution Notes")
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    val allStepsTested =
                        executionSteps.isNotEmpty() &&
                                executionSteps.all {
                                    it.status == "Pass" ||
                                            it.status == "Fail" ||
                                            it.status == "Blocked"
                                }

                    Button(
                        onClick = {
                            executionViewModel.completeExecution()
                        },
                        enabled = allStepsTested,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(52.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = SuccessGreen,
                            disabledContainerColor = NavySurface
                        )
                    ) {

                        Icon(
                            imageVector = Icons.Default.Check,
                            contentDescription = null
                        )

                        Spacer(modifier = Modifier.width(8.dp))

                        Text("Complete Execution")
                    }

                    if (!allStepsTested) {

                        Spacer(modifier = Modifier.height(6.dp))

                        Text(
                            text = "Test every step before completing this execution.",
                            color = WarningOrange,
                            fontSize = 12.sp,
                            modifier = Modifier.align(Alignment.CenterHorizontally)
                        )
                    }

                } else {

                    // Actions after completion
                    Spacer(modifier = Modifier.height(12.dp))

                    OutlinedButton(
                        onClick = {
                            showHistory = true
                        },
                        modifier = Modifier.fillMaxWidth()
                    ) {

                        Icon(
                            imageVector = Icons.Default.History,
                            contentDescription = null
                        )

                        Spacer(modifier = Modifier.width(8.dp))

                        Text("View Execution History")
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    Button(
                        onClick = onBack,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(52.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = PurplePrimary
                        )
                    ) {
                        Text("Back to Test Steps")
                    }
                }
            }
        }

        // Snackbar messages
        SnackbarHost(
            hostState = snackbarHostState,
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(16.dp)
        )
    }

    // Execution notes dialog
    if (
        showNotesDialog &&
        currentExecution?.status == "In Progress"
    ) {

        AlertDialog(
            onDismissRequest = {
                showNotesDialog = false
            },
            containerColor = NavySurface,

            title = {
                Text(
                    text = "Execution Notes",
                    color = TextPrimary
                )
            },

            text = {
                OutlinedTextField(
                    value = executionNotes,
                    onValueChange = {
                        executionNotes = it
                    },
                    placeholder = {
                        Text("Enter execution notes...")
                    },
                    modifier = Modifier.fillMaxWidth(),
                    minLines = 3,
                    maxLines = 5
                )
            },

            confirmButton = {
                TextButton(
                    onClick = {
                        executionViewModel.updateExecutionNotes(
                            executionNotes
                        )
                        showNotesDialog = false
                    }
                ) {
                    Text("Save")
                }
            },

            dismissButton = {
                TextButton(
                    onClick = {
                        showNotesDialog = false
                    }
                ) {
                    Text("Cancel")
                }
            }
        )
    }

    // Defect reporting dialog
    selectedDefectStep?.let { failedStep ->
        ReportExecutionDefectDialog(
            testCaseId = testCaseId,
            step = failedStep,
            initialActualResult = selectedDefectActualResult,
            onDismiss = {
                selectedDefectStep = null
            },
            onSave = { title, description, severity, priority, stepsToReproduce, actualResult ->
                coroutineScope.launch {
                    try {
                        // Resolve the project through Test Case -> Test Suite -> Project.
                        val testCase = database.testCaseDao()
                            .getTestCaseById(testCaseId)
                            .first()

                        if (testCase == null) {
                            snackbarHostState.showSnackbar(
                                "Could not find the test case. Defect was not saved."
                            )
                            return@launch
                        }

                        val testSuite = database.testSuiteDao()
                            .getSuiteById(testCase.suiteId)
                            .first()

                        if (testSuite == null) {
                            snackbarHostState.showSnackbar(
                                "Could not find the test suite. Defect was not saved."
                            )
                            return@launch
                        }

                        val defectRepository = DefectRepository(
                            database.defectDao()
                        )

                        defectRepository.insertDefect(
                            DefectEntity(
                                title = title.trim(),
                                description = description.trim(),
                                severity = severity,
                                priority = priority,
                                status = "Open",
                                projectId = testSuite.projectId,
                                testCaseId = testCaseId,
                                executionId = currentExecution?.id,
                                stepsToReproduce = stepsToReproduce.trim(),
                                expectedResult = failedStep.expectedResult,
                                actualResult = actualResult.trim()
                            )
                        )

                        selectedDefectStep = null
                        snackbarHostState.showSnackbar(
                            "Defect reported successfully."
                        )
                    } catch (exception: Exception) {
                        snackbarHostState.showSnackbar(
                            "Could not report defect: ${exception.localizedMessage ?: "Unknown error"}"
                        )
                    }
                }
            }
        )
    }
}

// INDIVIDUAL EXECUTION STEP CARD
@Composable
private fun ExecutionStepCard(
    step: TestExecutionStepEntity,
    editable: Boolean,
    onSaveActualResult: (String) -> Unit,
    onStatusChange: (String) -> Unit,
    onReportDefect: (String) -> Unit
) {

    var actualResult by remember(step.id, step.actualResult) {
        mutableStateOf(step.actualResult)
    }

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(
            containerColor = NavySurface
        )
    ) {

        Column(
            modifier = Modifier.padding(16.dp)
        ) {

            Text(
                text = "Step ${step.stepNumber}",
                color = PurpleSecondary,
                fontSize = 15.sp,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(10.dp))

            Text(
                text = "Action",
                color = TextSecondary,
                fontSize = 12.sp
            )

            Text(
                text = step.action,
                color = TextPrimary,
                fontSize = 14.sp
            )

            Spacer(modifier = Modifier.height(10.dp))

            Text(
                text = "Expected Result",
                color = TextSecondary,
                fontSize = 12.sp
            )

            Text(
                text = step.expectedResult,
                color = TextPrimary,
                fontSize = 14.sp
            )

            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = "Actual Result",
                color = PurpleSecondary,
                fontSize = 13.sp,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(4.dp))

            OutlinedTextField(
                value = actualResult,
                onValueChange = {
                    actualResult = it
                },
                readOnly = !editable,
                label = {
                    Text("What actually happened?")
                },
                placeholder = {
                    Text("Enter the observed result...")
                },
                modifier = Modifier.fillMaxWidth(),
                minLines = 2,
                maxLines = 4
            )

            if (editable) {

                Spacer(modifier = Modifier.height(6.dp))

                Text(
                    text = "Record what actually happened during this test step.",
                    color = TextSecondary,
                    fontSize = 11.sp
                )

                Spacer(modifier = Modifier.height(8.dp))

                Button(
                    onClick = {
                        onSaveActualResult(actualResult)
                    },
                    enabled = actualResult.isNotBlank(),
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = PurplePrimary
                    )
                ) {
                    Text("Save Actual Result")
                }

                Spacer(modifier = Modifier.height(14.dp))

                Text(
                    text = "Select Test Result",
                    color = TextSecondary,
                    fontSize = 12.sp
                )

                Spacer(modifier = Modifier.height(8.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {

                    ResultButton(
                        text = "Pass",
                        selected = step.status == "Pass",
                        color = SuccessGreen,
                        onClick = {
                            onStatusChange("Pass")
                        },
                        modifier = Modifier.weight(1f)
                    )

                    ResultButton(
                        text = "Fail",
                        selected = step.status == "Fail",
                        color = FailureRed,
                        onClick = {
                            onStatusChange("Fail")
                        },
                        modifier = Modifier.weight(1f)
                    )

                    ResultButton(
                        text = "Blocked",
                        selected = step.status == "Blocked",
                        color = WarningOrange,
                        onClick = {
                            onStatusChange("Blocked")
                        },
                        modifier = Modifier.weight(1f)
                    )
                }

                if (step.status == "Fail") {
                    Spacer(modifier = Modifier.height(10.dp))

                    OutlinedButton(
                        onClick = {
                            onReportDefect(actualResult)
                        },
                        modifier = Modifier.fillMaxWidth(),
                        colors = ButtonDefaults.outlinedButtonColors(
                            contentColor = FailureRed
                        )
                    ) {
                        Text("Report Defect")
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            ExecutionStatusBadge(step.status)
        }
    }
}

// REPORT DEFECT FROM A FAILED EXECUTION STEP
@Composable
private fun ReportExecutionDefectDialog(
    testCaseId: Int,
    step: TestExecutionStepEntity,
    initialActualResult: String,
    onDismiss: () -> Unit,
    onSave: (
        title: String,
        description: String,
        severity: String,
        priority: String,
        stepsToReproduce: String,
        actualResult: String
    ) -> Unit
) {
    var title by remember(step.id) {
        mutableStateOf("Failed: ${step.action.take(55)}")
    }
    var description by remember(step.id) {
        mutableStateOf("Test step ${step.stepNumber} failed during execution.")
    }
    var severity by remember(step.id) {
        mutableStateOf("Medium")
    }
    var priority by remember(step.id) {
        mutableStateOf("Medium")
    }
    var stepsToReproduce by remember(step.id) {
        mutableStateOf("Step ${step.stepNumber}: ${step.action}")
    }
    var actualResult by remember(step.id) {
        mutableStateOf(initialActualResult)
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = NavySurface,
        title = {
            Text(
                text = "Report Defect",
                color = TextPrimary,
                fontWeight = FontWeight.Bold
            )
        },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(max = 520.dp)
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Text(
                    text = "Linked to Test Case #$testCaseId · Step ${step.stepNumber}",
                    color = PurpleSecondary,
                    fontSize = 12.sp
                )

                OutlinedTextField(
                    value = title,
                    onValueChange = { title = it },
                    label = { Text("Defect title") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )

                OutlinedTextField(
                    value = description,
                    onValueChange = { description = it },
                    label = { Text("Description") },
                    modifier = Modifier.fillMaxWidth(),
                    minLines = 2,
                    maxLines = 4
                )

                OutlinedTextField(
                    value = severity,
                    onValueChange = { severity = it },
                    label = { Text("Severity (Low / Medium / High / Critical)") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )

                OutlinedTextField(
                    value = priority,
                    onValueChange = { priority = it },
                    label = { Text("Priority (Low / Medium / High / Critical)") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )

                OutlinedTextField(
                    value = stepsToReproduce,
                    onValueChange = { stepsToReproduce = it },
                    label = { Text("Steps to reproduce") },
                    modifier = Modifier.fillMaxWidth(),
                    minLines = 2,
                    maxLines = 4
                )

                OutlinedTextField(
                    value = step.expectedResult,
                    onValueChange = {},
                    readOnly = true,
                    label = { Text("Expected result") },
                    modifier = Modifier.fillMaxWidth(),
                    minLines = 2
                )

                OutlinedTextField(
                    value = actualResult,
                    onValueChange = { actualResult = it },
                    label = { Text("Actual result") },
                    modifier = Modifier.fillMaxWidth(),
                    minLines = 2,
                    maxLines = 4
                )

                Text(
                    text = "The defect will be linked automatically to this test case, its project and the current execution.",
                    color = TextSecondary,
                    fontSize = 11.sp
                )
            }
        },
        confirmButton = {
            TextButton(
                onClick = {
                    onSave(
                        title,
                        description,
                        severity,
                        priority,
                        stepsToReproduce,
                        actualResult
                    )
                },
                enabled = title.isNotBlank() && description.isNotBlank()
            ) {
                Text("Save Defect", color = PurpleSecondary)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel", color = TextSecondary)
            }
        }
    )
}

// RESULT SELECTION BUTTON
@Composable
private fun ResultButton(
    text: String,
    selected: Boolean,
    color: Color,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {

    Button(
        onClick = onClick,
        modifier = modifier,
        contentPadding = PaddingValues(
            horizontal = 4.dp,
            vertical = 8.dp
        ),
        colors = ButtonDefaults.buttonColors(
            containerColor = if (selected) {
                color
            } else {
                color.copy(alpha = 0.18f)
            },
            contentColor = if (selected) {
                NavyBackground
            } else {
                color
            }
        )
    ) {

        Text(
            text = text,
            fontSize = 11.sp,
            fontWeight = FontWeight.Bold
        )
    }
}

// EXECUTION HISTORY CARD
@Composable
private fun ExecutionHistoryCard(
    execution: TestExecutionEntity,
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
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {

            Column(
                modifier = Modifier.weight(1f)
            ) {

                Text(
                    text = "Execution #${execution.id}",
                    color = TextPrimary,
                    fontWeight = FontWeight.Bold
                )

                Text(
                    text = java.text.SimpleDateFormat(
                        "dd MMM yyyy, hh:mm a",
                        java.util.Locale.getDefault()
                    ).format(
                        java.util.Date(execution.executedAt)
                    ),
                    color = TextSecondary,
                    fontSize = 11.sp
                )
            }

            ExecutionStatusBadge(execution.status)
        }
    }
}

// EXECUTION STATUS BADGE
@Composable
private fun ExecutionStatusBadge(
    status: String
) {

    val displayStatus = when (status) {
        "Pass" -> "Passed"
        "Fail" -> "Failed"
        else -> status
    }

    val color = when (status) {
        "Pass", "Passed" -> SuccessGreen
        "Fail", "Failed" -> FailureRed
        "Blocked" -> WarningOrange
        "In Progress" -> PurpleSecondary
        else -> TextSecondary
    }

    Surface(
        color = color.copy(alpha = 0.15f),
        shape = RoundedCornerShape(20.dp)
    ) {

        Text(
            text = displayStatus,
            color = color,
            fontSize = 11.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(
                horizontal = 10.dp,
                vertical = 6.dp
            )
        )
    }
}

// EMPTY STATE
@Composable
private fun EmptyExecutionMessage(
    message: String
) {

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(180.dp),
        contentAlignment = Alignment.Center
    ) {

        Text(
            text = message,
            color = TextSecondary,
            fontSize = 14.sp
        )
    }
}
