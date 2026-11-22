
package com.example.testpilot.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Science
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.testpilot.data.local.TestCaseEntity
import com.example.testpilot.data.local.TestPilotDatabase
import com.example.testpilot.data.repository.TestCaseRepository
import com.example.testpilot.ui.theme.*
import com.example.testpilot.viewmodel.TestCaseViewModel
import com.example.testpilot.viewmodel.TestCaseViewModelFactory

@Composable
fun TestCasesScreen(
    suiteId: Int,
    onBack: () -> Unit,
    onOpenTestCaseSteps: (Int) -> Unit
) {

    val context = LocalContext.current.applicationContext

    val repository = remember(context) {
        TestCaseRepository(
            TestPilotDatabase
                .getDatabase(context)
                .testCaseDao()
        )
    }

    val factory = remember(repository) {
        TestCaseViewModelFactory(repository)
    }

    val testCaseViewModel: TestCaseViewModel = viewModel(
        key = "test_cases_$suiteId",
        factory = factory
    )

    val testCases by testCaseViewModel.testCases.collectAsState()
    val testCaseCount by testCaseViewModel.testCaseCount.collectAsState()
    val searchQuery by testCaseViewModel.searchQuery.collectAsState()

    var showAddDialog by remember {
        mutableStateOf(false)
    }

    var testCaseToEdit by remember {
        mutableStateOf<TestCaseEntity?>(null)
    }

    var testCaseToDelete by remember {
        mutableStateOf<TestCaseEntity?>(null)
    }

    LaunchedEffect(suiteId) {
        testCaseViewModel.loadTestCases(suiteId)
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

            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = "Test Cases",
                    color = TextPrimary,
                    fontSize = 25.sp,
                    fontWeight = FontWeight.Bold
                )

                Text(
                    text = "Manage your testing scenarios",
                    color = TextSecondary,
                    fontSize = 13.sp
                )
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        // Summary
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
                Icon(
                    imageVector = Icons.Default.Science,
                    contentDescription = null,
                    tint = PurpleSecondary,
                    modifier = Modifier.size(38.dp)
                )

                Spacer(modifier = Modifier.width(14.dp))

                Column {
                    Text(
                        text = "Total Test Cases",
                        color = TextSecondary,
                        fontSize = 13.sp
                    )

                    Text(
                        text = testCaseCount.toString(),
                        color = TextPrimary,
                        fontSize = 25.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(18.dp))

        // Search
        OutlinedTextField(
            value = searchQuery,
            onValueChange = testCaseViewModel::searchTestCases,
            modifier = Modifier.fillMaxWidth(),
            placeholder = {
                Text("Search test cases...")
            },
            leadingIcon = {
                Icon(
                    imageVector = Icons.Default.Search,
                    contentDescription = "Search"
                )
            },
            singleLine = true,
            shape = RoundedCornerShape(14.dp)
        )

        Spacer(modifier = Modifier.height(18.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Your Test Cases",
                color = TextPrimary,
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.weight(1f)
            )

            Text(
                text = "$testCaseCount cases",
                color = PurpleSecondary,
                fontSize = 13.sp
            )
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Test case list
        if (testCases.isEmpty()) {

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Science,
                    contentDescription = null,
                    tint = PurpleSecondary,
                    modifier = Modifier.size(58.dp)
                )

                Spacer(modifier = Modifier.height(14.dp))

                Text(
                    text = if (searchQuery.isNotBlank()) {
                        "No matching test cases"
                    } else {
                        "No test cases yet"
                    },
                    color = TextPrimary,
                    fontWeight = FontWeight.Bold,
                    fontSize = 17.sp
                )

                Spacer(modifier = Modifier.height(6.dp))

                Text(
                    text = if (searchQuery.isNotBlank()) {
                        "Try another search term."
                    } else {
                        "Create your first test case."
                    },
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
                    items = testCases,
                    key = { it.id }
                ) { testCase ->

                    TestCaseCard(
                        testCase = testCase,
                        onEdit = {
                            testCaseToEdit = testCase
                        },
                        onDelete = {
                            testCaseToDelete = testCase
                        },
                        onClick = {
                            onOpenTestCaseSteps(testCase.id)
                        }
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Add button
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

            Text("Add New Test Case")
        }
    }

    // Add dialog
    if (showAddDialog) {
        TestCaseFormDialog(
            title = "Add Test Case",
            confirmText = "Create Test Case",
            onDismiss = {
                showAddDialog = false
            },
            onSave = { title, description, preconditions, priority, testType ->

                testCaseViewModel.addTestCase(
                    suiteId = suiteId,
                    title = title,
                    description = description,
                    preconditions = preconditions,
                    priority = priority,
                    testType = testType
                )

                showAddDialog = false
            }
        )
    }

    // Edit dialog
    testCaseToEdit?.let { testCase ->
        TestCaseFormDialog(
            title = "Edit Test Case",
            confirmText = "Save Changes",
            initialTitle = testCase.title,
            initialDescription = testCase.description,
            initialPreconditions = testCase.preconditions,
            initialPriority = testCase.priority,
            initialTestType = testCase.testType,
            onDismiss = {
                testCaseToEdit = null
            },
            onSave = { title, description, preconditions, priority, testType ->

                testCaseViewModel.updateTestCase(
                    testCase.copy(
                        title = title,
                        description = description,
                        preconditions = preconditions,
                        priority = priority,
                        testType = testType
                    )
                )

                testCaseToEdit = null
            }
        )
    }

    // Delete confirmation
    testCaseToDelete?.let { testCase ->

        AlertDialog(
            onDismissRequest = {
                testCaseToDelete = null
            },
            containerColor = NavySurface,
            title = {
                Text(
                    text = "Delete Test Case?",
                    color = TextPrimary
                )
            },
            text = {
                Text(
                    text = "Are you sure you want to delete \"${testCase.title}\"?",
                    color = TextSecondary
                )
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        testCaseViewModel.deleteTestCase(testCase)
                        testCaseToDelete = null
                    }
                ) {
                    Text(
                        text = "Delete",
                        color = FailureRed
                    )
                }
            },
            dismissButton = {
                TextButton(
                    onClick = {
                        testCaseToDelete = null
                    }
                ) {
                    Text("Cancel")
                }
            }
        )
    }
}

@Composable
private fun TestCaseCard(
    testCase: TestCaseEntity,
    onEdit: () -> Unit,
    onDelete: () -> Unit,
    onClick: () -> Unit
) {

    val priorityColor = when (testCase.priority) {
        "High" -> FailureRed
        "Medium" -> WarningOrange
        else -> SuccessGreen
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() },
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
                Text(
                    text = testCase.title,
                    color = TextPrimary,
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp,
                    modifier = Modifier.weight(1f)
                )

                Surface(
                    color = priorityColor.copy(alpha = 0.15f),
                    shape = RoundedCornerShape(20.dp)
                ) {
                    Text(
                        text = testCase.priority,
                        color = priorityColor,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.SemiBold,
                        modifier = Modifier.padding(
                            horizontal = 10.dp,
                            vertical = 5.dp
                        )
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = testCase.description.ifBlank {
                    "No description provided."
                },
                color = TextSecondary,
                fontSize = 13.sp
            )

            Spacer(modifier = Modifier.height(10.dp))

            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = testCase.testType,
                    color = PurpleSecondary,
                    fontSize = 12.sp,
                    modifier = Modifier.weight(1f)
                )

                IconButton(onClick = onEdit) {
                    Icon(
                        imageVector = Icons.Default.Edit,
                        contentDescription = "Edit test case",
                        tint = PurpleSecondary
                    )
                }

                IconButton(onClick = onDelete) {
                    Icon(
                        imageVector = Icons.Default.Delete,
                        contentDescription = "Delete test case",
                        tint = FailureRed
                    )
                }
            }
        }
    }
}

@Composable
private fun TestCaseFormDialog(
    title: String,
    confirmText: String,
    initialTitle: String = "",
    initialDescription: String = "",
    initialPreconditions: String = "",
    initialPriority: String = "Medium",
    initialTestType: String = "Functional",
    onDismiss: () -> Unit,
    onSave: (
        String,
        String,
        String,
        String,
        String
    ) -> Unit
) {

    var testTitle by remember(initialTitle) {
        mutableStateOf(initialTitle)
    }

    var description by remember(initialDescription) {
        mutableStateOf(initialDescription)
    }

    var preconditions by remember(initialPreconditions) {
        mutableStateOf(initialPreconditions)
    }

    var priority by remember(initialPriority) {
        mutableStateOf(initialPriority)
    }

    var testType by remember(initialTestType) {
        mutableStateOf(initialTestType)
    }

    val priorities = listOf("Low", "Medium", "High")

    val testTypes = listOf(
        "Functional",
        "UI",
        "Performance",
        "Security"
    )

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
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(max = 520.dp)
            ) {

                OutlinedTextField(
                    value = testTitle,
                    onValueChange = { testTitle = it },
                    label = {
                        Text("Test Case Title")
                    },
                    placeholder = {
                        Text("e.g. Verify valid login")
                    },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(10.dp))

                OutlinedTextField(
                    value = description,
                    onValueChange = { description = it },
                    label = {
                        Text("Description")
                    },
                    placeholder = {
                        Text("Describe what you want to test")
                    },
                    modifier = Modifier.fillMaxWidth(),
                    minLines = 2,
                    maxLines = 3
                )

                Spacer(modifier = Modifier.height(10.dp))

                OutlinedTextField(
                    value = preconditions,
                    onValueChange = { preconditions = it },
                    label = {
                        Text("Preconditions")
                    },
                    placeholder = {
                        Text("e.g. User has a registered account")
                    },
                    modifier = Modifier.fillMaxWidth(),
                    minLines = 2,
                    maxLines = 3
                )

                Spacer(modifier = Modifier.height(14.dp))

                Text(
                    text = "Priority",
                    color = TextSecondary,
                    fontSize = 13.sp
                )

                Spacer(modifier = Modifier.height(6.dp))

                SimpleDropdown(
                    selectedValue = priority,
                    options = priorities,
                    onValueSelected = { priority = it }
                )

                Spacer(modifier = Modifier.height(12.dp))

                Text(
                    text = "Test Type",
                    color = TextSecondary,
                    fontSize = 13.sp
                )

                Spacer(modifier = Modifier.height(6.dp))

                SimpleDropdown(
                    selectedValue = testType,
                    options = testTypes,
                    onValueSelected = { testType = it }
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    onSave(
                        testTitle.trim(),
                        description.trim(),
                        preconditions.trim(),
                        priority,
                        testType
                    )
                },
                enabled = testTitle.isNotBlank(),
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

@Composable
private fun SimpleDropdown(
    selectedValue: String,
    options: List<String>,
    onValueSelected: (String) -> Unit
) {

    var expanded by remember {
        mutableStateOf(false)
    }

    Box(
        modifier = Modifier.fillMaxWidth()
    ) {

        OutlinedButton(
            onClick = {
                expanded = true
            },
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(10.dp)
        ) {
            Text(
                text = selectedValue,
                modifier = Modifier.weight(1f)
            )

            Text("▼")
        }

        DropdownMenu(
            expanded = expanded,
            onDismissRequest = {
                expanded = false
            }
        ) {
            options.forEach { option ->

                DropdownMenuItem(
                    text = {
                        Text(option)
                    },
                    onClick = {
                        onValueSelected(option)
                        expanded = false
                    }
                )
            }
        }
    }
}


