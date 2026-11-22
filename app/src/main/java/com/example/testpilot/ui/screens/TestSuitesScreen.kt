
package com.example.testpilot.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.FactCheck
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.testpilot.data.local.TestSuiteEntity
import com.example.testpilot.data.local.TestPilotDatabase
import com.example.testpilot.data.repository.TestSuiteRepository
import com.example.testpilot.ui.theme.*
import com.example.testpilot.viewmodel.TestSuiteViewModel
import com.example.testpilot.viewmodel.TestSuiteViewModelFactory
import androidx.compose.material.icons.automirrored.filled.ArrowBack

@Composable
fun TestSuitesScreen(
    projectId: Int,
    onBack: () -> Unit,
    onOpenSuite: (Int) -> Unit
) {

    val context = LocalContext.current.applicationContext

    val repository = remember(context) {
        TestSuiteRepository(
            TestPilotDatabase
                .getDatabase(context)
                .testSuiteDao()
        )
    }

    val factory = remember(repository) {
        TestSuiteViewModelFactory(repository)
    }

    val suiteViewModel: TestSuiteViewModel = viewModel(
        key = "test_suites_$projectId",
        factory = factory
    )

    val suites by suiteViewModel.suites.collectAsState()
    val suiteCount by suiteViewModel.suiteCount.collectAsState()

    var showAddDialog by remember {
        mutableStateOf(false)
    }

    var suiteToEdit by remember {
        mutableStateOf<TestSuiteEntity?>(null)
    }

    var suiteToDelete by remember {
        mutableStateOf<TestSuiteEntity?>(null)
    }

    LaunchedEffect(projectId) {
        suiteViewModel.loadSuites(projectId)
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
            androidx.compose.material3.IconButton(
                onClick = onBack
            ) {
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
                    text = "Test Suites",
                    color = TextPrimary,
                    fontSize = 25.sp,
                    fontWeight = FontWeight.Bold
                )

                Text(
                    text = "Organise your testing activities",
                    color = TextSecondary,
                    fontSize = 13.sp
                )
            }
        }

        Spacer(modifier = Modifier.height(22.dp))

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
                Icon(
                    imageVector = Icons.Default.FactCheck,
                    contentDescription = null,
                    tint = PurpleSecondary,
                    modifier = Modifier.size(38.dp)
                )

                Spacer(modifier = Modifier.width(14.dp))

                Column {
                    Text(
                        text = "Total Test Suites",
                        color = TextSecondary,
                        fontSize = 13.sp
                    )

                    Text(
                        text = suiteCount.toString(),
                        color = TextPrimary,
                        fontSize = 25.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(22.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Your Test Suites",
                color = TextPrimary,
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.weight(1f)
            )

            Text(
                text = "$suiteCount suites",
                color = PurpleSecondary,
                fontSize = 13.sp
            )
        }

        Spacer(modifier = Modifier.height(14.dp))

        // Suite list
        if (suites.isEmpty()) {

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Icon(
                    imageVector = Icons.Default.FactCheck,
                    contentDescription = null,
                    tint = PurpleSecondary,
                    modifier = Modifier.size(58.dp)
                )

                Spacer(modifier = Modifier.height(14.dp))

                Text(
                    text = "No test suites yet",
                    color = TextPrimary,
                    fontWeight = FontWeight.Bold,
                    fontSize = 17.sp
                )

                Spacer(modifier = Modifier.height(6.dp))

                Text(
                    text = "Create your first test suite for this project.",
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
                    items = suites,
                    key = { it.id }
                ) { suite ->

                    TestSuiteCard(
                        suite = suite,
                        onClick = {
                            onOpenSuite(suite.id)
                        },
                        onEdit = {
                            suiteToEdit = suite
                        },
                        onDelete = {
                            suiteToDelete = suite
                        }
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Add suite button
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

            Text("Add New Test Suite")
        }
    }

    // Add dialog
    if (showAddDialog) {
        SuiteFormDialog(
            title = "Add Test Suite",
            confirmText = "Create Suite",
            onDismiss = {
                showAddDialog = false
            },
            onSave = { name, description ->
                suiteViewModel.addSuite(
                    projectId = projectId,
                    name = name,
                    description = description
                )
                showAddDialog = false
            }
        )
    }

    // Edit dialog
    suiteToEdit?.let { suite ->
        SuiteFormDialog(
            title = "Edit Test Suite",
            confirmText = "Save Changes",
            initialName = suite.name,
            initialDescription = suite.description,
            onDismiss = {
                suiteToEdit = null
            },
            onSave = { name, description ->
                suiteViewModel.updateSuite(
                    suite.copy(
                        name = name,
                        description = description
                    )
                )
                suiteToEdit = null
            }
        )
    }

    // Delete confirmation
    suiteToDelete?.let { suite ->
        AlertDialog(
            onDismissRequest = {
                suiteToDelete = null
            },
            containerColor = NavySurface,
            title = {
                Text(
                    text = "Delete Test Suite?",
                    color = TextPrimary
                )
            },
            text = {
                Text(
                    text = "Are you sure you want to delete \"${suite.name}\"? Its test cases and steps will also be deleted.",
                    color = TextSecondary
                )
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        suiteViewModel.deleteSuite(suite)
                        suiteToDelete = null
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
                        suiteToDelete = null
                    }
                ) {
                    Text("Cancel")
                }
            }
        )
    }
}

@Composable
private fun TestSuiteCard(
    suite: TestSuiteEntity,
    onClick: () -> Unit,
    onEdit: () -> Unit,
    onDelete: () -> Unit
) {
    Card(
        onClick = onClick,
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(18.dp),
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
            Icon(
                imageVector = Icons.Default.FactCheck,
                contentDescription = null,
                tint = PurpleSecondary,
                modifier = Modifier.size(32.dp)
            )

            Spacer(modifier = Modifier.width(12.dp))

            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = suite.name,
                    color = TextPrimary,
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp
                )

                Spacer(modifier = Modifier.height(5.dp))

                Text(
                    text = suite.description.ifBlank {
                        "No description provided."
                    },
                    color = TextSecondary,
                    fontSize = 13.sp
                )
            }

            IconButton(onClick = onEdit) {
                Icon(
                    imageVector = Icons.Default.Edit,
                    contentDescription = "Edit suite",
                    tint = PurpleSecondary
                )
            }

            IconButton(onClick = onDelete) {
                Icon(
                    imageVector = Icons.Default.Delete,
                    contentDescription = "Delete suite",
                    tint = FailureRed
                )
            }
        }
    }
}

@Composable
private fun SuiteFormDialog(
    title: String,
    confirmText: String,
    initialName: String = "",
    initialDescription: String = "",
    onDismiss: () -> Unit,
    onSave: (String, String) -> Unit
) {
    var name by remember(initialName) {
        mutableStateOf(initialName)
    }

    var description by remember(initialDescription) {
        mutableStateOf(initialDescription)
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
                    value = name,
                    onValueChange = { name = it },
                    label = {
                        Text("Suite Name")
                    },
                    placeholder = {
                        Text("e.g. Login Testing")
                    },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(12.dp))

                OutlinedTextField(
                    value = description,
                    onValueChange = { description = it },
                    label = {
                        Text("Description")
                    },
                    placeholder = {
                        Text("Describe what this suite will test")
                    },
                    modifier = Modifier.fillMaxWidth(),
                    minLines = 3,
                    maxLines = 5
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    onSave(
                        name.trim(),
                        description.trim()
                    )
                },
                enabled = name.isNotBlank(),
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
