
package com.example.testpilot.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.lifecycle.ViewModelProvider

import com.example.testpilot.data.local.TestStepEntity
import com.example.testpilot.data.local.TestPilotDatabase
import com.example.testpilot.data.repository.TestStepRepository
import com.example.testpilot.ui.theme.*
import com.example.testpilot.viewmodel.TestStepViewModel

@Composable
fun TestStepsScreen(
    testCaseId: Int,
    onBack: () -> Unit,
    onStartExecution: () -> Unit
) {
    val context = LocalContext.current.applicationContext

    val repository = remember(context) {
        TestStepRepository(
            TestPilotDatabase
                .getDatabase(context)
                .testStepDao()
        )
    }

    val factory = remember(repository) {
        object : ViewModelProvider.Factory {
            @Suppress("UNCHECKED_CAST")
            override fun <T : androidx.lifecycle.ViewModel> create(
                modelClass: Class<T>
            ): T {
                return TestStepViewModel(repository) as T
            }
        }
    }

    val stepViewModel: TestStepViewModel = viewModel(
        key = "test_steps_$testCaseId",
        factory = factory
    )

    val steps by stepViewModel.steps.collectAsState()

    var showAddDialog by remember {
        mutableStateOf(false)
    }

    var stepToEdit by remember {
        mutableStateOf<TestStepEntity?>(null)
    }

    var stepToDelete by remember {
        mutableStateOf<TestStepEntity?>(null)
    }

    LaunchedEffect(testCaseId) {
        stepViewModel.loadSteps(testCaseId)
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(NavyBackground)
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

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = "Test Steps",
                    color = TextPrimary,
                    fontSize = 25.sp,
                    fontWeight = FontWeight.Bold
                )

                Text(
                    text = "Manage execution instructions",
                    color = TextSecondary,
                    fontSize = 13.sp
                )
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        // Summary card
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(18.dp),
            colors = CardDefaults.cardColors(
                containerColor = NavySurface
            )
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(18.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "☑",
                    color = PurpleSecondary,
                    fontSize = 32.sp
                )

                Spacer(modifier = Modifier.width(14.dp))

                Column {
                    Text(
                        text = "Total Steps",
                        color = TextSecondary,
                        fontSize = 13.sp
                    )

                    Text(
                        text = steps.size.toString(),
                        color = TextPrimary,
                        fontSize = 25.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        Text(
            text = "Execution Steps",
            color = TextPrimary,
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold
        )

        Spacer(modifier = Modifier.height(12.dp))

        if (steps.isEmpty()) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Text(
                    text = "No test steps yet",
                    color = TextPrimary,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold
                )

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = "Add your first execution step.",
                    color = TextSecondary,
                    fontSize = 13.sp
                )
            }
        } else {
            LazyColumn(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(
                    items = steps,
                    key = { it.id }
                ) { step ->

                    val currentIndex =
                        steps.indexOfFirst { it.id == step.id }

                    TestStepCard(
                        step = step,
                        canMoveUp = currentIndex > 0,
                        canMoveDown = currentIndex < steps.lastIndex,
                        onEdit = {
                            stepToEdit = step
                        },
                        onDelete = {
                            stepToDelete = step
                        },
                        onMoveUp = {
                            val updated = steps.toMutableList()
                            val item = updated.removeAt(currentIndex)
                            updated.add(currentIndex - 1, item)

                            stepViewModel.replaceSteps(
                                testCaseId,
                                updated
                            )
                        },
                        onMoveDown = {
                            val updated = steps.toMutableList()
                            val item = updated.removeAt(currentIndex)
                            updated.add(currentIndex + 1, item)

                            stepViewModel.replaceSteps(
                                testCaseId,
                                updated
                            )
                        }
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Add New Step button
        Button(
            onClick = {
                showAddDialog = true
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(54.dp),
            shape = RoundedCornerShape(14.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = PurplePrimary
            )
        ) {
            Icon(
                imageVector = Icons.Default.Add,
                contentDescription = null
            )

            Spacer(modifier = Modifier.width(8.dp))

            Text("Add New Step")
        }

        Spacer(modifier = Modifier.height(10.dp))

        // NEW: Start Execution button
        Button(
            onClick = onStartExecution,
            enabled = steps.isNotEmpty(),
            modifier = Modifier
                .fillMaxWidth()
                .height(54.dp),
            shape = RoundedCornerShape(14.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = SuccessGreen,
                disabledContainerColor = NavySurface
            )
        ) {
            Text(
                text = "Start Execution",
                fontWeight = FontWeight.Bold
            )
        }
    }

    // Add dialog
    if (showAddDialog) {
        TestStepFormDialog(
            title = "Add Test Step",
            confirmText = "Create Step",
            onDismiss = {
                showAddDialog = false
            },
            onSave = { action, expectedResult ->
                stepViewModel.addStep(
                    testCaseId = testCaseId,
                    stepNumber = steps.size + 1,
                    action = action,
                    expectedResult = expectedResult
                )
                showAddDialog = false
            }
        )
    }

    // Edit dialog
    stepToEdit?.let { step ->
        TestStepFormDialog(
            title = "Edit Test Step",
            confirmText = "Save Changes",
            initialAction = step.action,
            initialExpectedResult = step.expectedResult,
            onDismiss = {
                stepToEdit = null
            },
            onSave = { action, expectedResult ->
                stepViewModel.updateStep(
                    step.copy(
                        action = action,
                        expectedResult = expectedResult
                    )
                )
                stepToEdit = null
            }
        )
    }

    // Delete confirmation
    stepToDelete?.let { step ->
        AlertDialog(
            onDismissRequest = {
                stepToDelete = null
            },
            containerColor = NavySurface,
            title = {
                Text(
                    "Delete Test Step?",
                    color = TextPrimary
                )
            },
            text = {
                Text(
                    "Are you sure you want to delete this step?",
                    color = TextSecondary
                )
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        stepViewModel.deleteStep(step)
                        stepToDelete = null
                    }
                ) {
                    Text(
                        "Delete",
                        color = FailureRed
                    )
                }
            },
            dismissButton = {
                TextButton(
                    onClick = {
                        stepToDelete = null
                    }
                ) {
                    Text("Cancel")
                }
            }
        )
    }
}

@Composable
private fun TestStepCard(
    step: TestStepEntity,
    canMoveUp: Boolean,
    canMoveDown: Boolean,
    onEdit: () -> Unit,
    onDelete: () -> Unit,
    onMoveUp: () -> Unit,
    onMoveDown: () -> Unit
) {
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
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Surface(
                    color = PurplePrimary.copy(alpha = 0.2f),
                    shape = RoundedCornerShape(10.dp)
                ) {
                    Text(
                        text = "Step ${step.stepNumber}",
                        color = PurpleSecondary,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(
                            horizontal = 12.dp,
                            vertical = 7.dp
                        )
                    )
                }

                Spacer(modifier = Modifier.weight(1f))

                IconButton(
                    onClick = onMoveUp,
                    enabled = canMoveUp
                ) {
                    Icon(
                        Icons.Default.KeyboardArrowUp,
                        contentDescription = "Move up",
                        tint = if (canMoveUp) PurpleSecondary
                        else TextSecondary
                    )
                }

                IconButton(
                    onClick = onMoveDown,
                    enabled = canMoveDown
                ) {
                    Icon(
                        Icons.Default.KeyboardArrowDown,
                        contentDescription = "Move down",
                        tint = if (canMoveDown) PurpleSecondary
                        else TextSecondary
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = "Action",
                color = PurpleSecondary,
                fontWeight = FontWeight.Bold,
                fontSize = 13.sp
            )

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = step.action,
                color = TextPrimary,
                fontSize = 14.sp
            )

            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = "Expected Result",
                color = PurpleSecondary,
                fontWeight = FontWeight.Bold,
                fontSize = 13.sp
            )

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = step.expectedResult,
                color = TextSecondary,
                fontSize = 14.sp
            )

            Spacer(modifier = Modifier.height(10.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End
            ) {
                IconButton(onClick = onEdit) {
                    Icon(
                        Icons.Default.Edit,
                        contentDescription = "Edit step",
                        tint = PurpleSecondary
                    )
                }

                IconButton(onClick = onDelete) {
                    Icon(
                        Icons.Default.Delete,
                        contentDescription = "Delete step",
                        tint = FailureRed
                    )
                }
            }
        }
    }
}

@Composable
private fun TestStepFormDialog(
    title: String,
    confirmText: String,
    initialAction: String = "",
    initialExpectedResult: String = "",
    onDismiss: () -> Unit,
    onSave: (String, String) -> Unit
) {
    var action by remember(initialAction) {
        mutableStateOf(initialAction)
    }

    var expectedResult by remember(initialExpectedResult) {
        mutableStateOf(initialExpectedResult)
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = NavySurface,
        title = {
            Text(
                text = title,
                color = TextPrimary
            )
        },
        text = {
            Column {
                OutlinedTextField(
                    value = action,
                    onValueChange = { action = it },
                    label = {
                        Text("Action")
                    },
                    placeholder = {
                        Text("e.g. Enter a valid email")
                    },
                    modifier = Modifier.fillMaxWidth(),
                    minLines = 2,
                    maxLines = 4
                )

                Spacer(modifier = Modifier.height(12.dp))

                OutlinedTextField(
                    value = expectedResult,
                    onValueChange = { expectedResult = it },
                    label = {
                        Text("Expected Result")
                    },
                    placeholder = {
                        Text("e.g. Email is accepted")
                    },
                    modifier = Modifier.fillMaxWidth(),
                    minLines = 2,
                    maxLines = 4
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    onSave(
                        action.trim(),
                        expectedResult.trim()
                    )
                },
                enabled = action.isNotBlank() &&
                        expectedResult.isNotBlank(),
                colors = ButtonDefaults.buttonColors(
                    containerColor = PurplePrimary
                )
            ) {
                Text(confirmText)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}
