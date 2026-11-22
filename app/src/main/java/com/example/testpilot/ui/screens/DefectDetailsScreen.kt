
package com.example.testpilot.ui.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.testpilot.data.local.DefectEntity
import com.example.testpilot.data.local.TestPilotDatabase
import com.example.testpilot.data.repository.DefectRepository
import com.example.testpilot.ui.theme.NavyBackground
import com.example.testpilot.ui.theme.NavySurface
import com.example.testpilot.ui.theme.PurplePrimary
import com.example.testpilot.viewmodel.DefectViewModel
import com.example.testpilot.viewmodel.DefectViewModelFactory
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

private val DetailsWhite = Color(0xFFFFFFFF)
private val DetailsCard = Color(0xFF202A42)
private val DetailsMuted = Color(0xFF9CA3AF)
private val DetailsRed = Color(0xFFEF5350)
private val DetailsOrange = Color(0xFFFFB74D)
private val DetailsGreen = Color(0xFF66BB6A)
private val DetailsBlue = Color(0xFF64B5F6)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DefectDetailsScreen(
    defectId: Int,
    onBack: () -> Unit
) {

    val context = LocalContext.current

    val database = remember {
        TestPilotDatabase.getDatabase(context)
    }

    val repository = remember {
        DefectRepository(database.defectDao())
    }

    val factory = remember {
        DefectViewModelFactory(repository)
    }

    val viewModel: DefectViewModel = viewModel(
        key = "defect_details_$defectId",
        factory = factory
    )

    val defect by viewModel.getDefectById(defectId)
        .collectAsState(initial = null)

    val project by database.projectDao()
        .getProjectById(defect?.projectId ?: -1)
        .collectAsState(initial = null)

    var showEditDialog by remember {
        mutableStateOf(false)
    }

    var showDeleteDialog by remember {
        mutableStateOf(false)
    }

    var showStatusMenu by remember {
        mutableStateOf(false)
    }

    Scaffold(
        containerColor = NavyBackground,

        topBar = {
            TopAppBar(
                title = {
                    Text(
                        "Defect Details",
                        color = DetailsWhite,
                        fontWeight = FontWeight.Bold
                    )
                },

                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(
                            Icons.Default.ArrowBack,
                            contentDescription = "Back",
                            tint = DetailsWhite
                        )
                    }
                },

                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = NavySurface
                )
            )
        }
    ) { padding ->

        if (defect == null) {

            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator(
                    color = PurplePrimary
                )
            }

        } else {

            val item = defect!!

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .verticalScroll(rememberScrollState())
                    .padding(16.dp),

                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {

                // Defect ID
                Text(
                    text = "DEF-${item.id.toString().padStart(3, '0')}",
                    color = Color(0xFFB9A2FF),
                    fontWeight = FontWeight.Bold,
                    fontSize = 13.sp
                )

                // Defect title
                Text(
                    text = item.title,
                    color = DetailsWhite,
                    fontSize = 23.sp,
                    fontWeight = FontWeight.Bold
                )

                // Severity and status
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {

                    DetailBadge(
                        text = item.severity,
                        color = severityColor(item.severity)
                    )

                    DetailBadge(
                        text = item.status,
                        color = statusColor(item.status)
                    )
                }

                // Update status
                DetailSection("Defect Status") {

                    Text(
                        text = "Update the current status of this defect.",
                        color = DetailsMuted,
                        fontSize = 12.sp
                    )

                    Box {

                        OutlinedButton(
                            onClick = {
                                showStatusMenu = true
                            },
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text(
                                text = "Change Status: ${item.status}",
                                color = DetailsWhite
                            )
                        }

                        DropdownMenu(
                            expanded = showStatusMenu,
                            onDismissRequest = {
                                showStatusMenu = false
                            }
                        ) {

                            listOf(
                                "Open",
                                "In Progress",
                                "Resolved",
                                "Closed"
                            ).forEach { status ->

                                DropdownMenuItem(
                                    text = {
                                        Text(status)
                                    },
                                    onClick = {

                                        viewModel.updateDefect(
                                            item.copy(
                                                status = status
                                            )
                                        )

                                        showStatusMenu = false
                                    }
                                )
                            }
                        }
                    }
                }

                // Project
                DetailSection("Project") {
                    DetailValue(
                        project?.projectName ?: "Project unavailable"
                    )
                }

                // Description
                DetailSection("Description") {
                    DetailValue(item.description)
                }

                // Priority
                DetailSection("Priority") {
                    DetailValue(item.priority)
                }

                // Steps to reproduce
                DetailSection("Steps to Reproduce") {
                    DetailValue(item.stepsToReproduce)
                }

                // Expected result
                DetailSection("Expected Result") {
                    DetailValue(item.expectedResult)
                }

                // Actual result
                DetailSection("Actual Result") {
                    DetailValue(item.actualResult)
                }

                // Reported date
                DetailSection("Reported Date") {
                    DetailValue(
                        formatDate(item.reportedAt)
                    )
                }

                // Updated date
                DetailSection("Last Updated") {
                    DetailValue(
                        formatDate(item.updatedAt)
                    )
                }

                // Linked test case
                DetailSection("Linked Test Case") {
                    DetailValue(
                        item.testCaseId?.toString() ?: "Not linked"
                    )
                }

                // Linked execution
                DetailSection("Linked Execution") {
                    DetailValue(
                        item.executionId?.toString() ?: "Not linked"
                    )
                }

                // Edit button
                Button(
                    onClick = {
                        showEditDialog = true
                    },
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = PurplePrimary
                    ),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Icon(
                        Icons.Default.Edit,
                        contentDescription = null
                    )

                    Spacer(Modifier.width(8.dp))

                    Text("Edit Defect")
                }

                // Delete button
                OutlinedButton(
                    onClick = {
                        showDeleteDialog = true
                    },
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.outlinedButtonColors(
                        contentColor = DetailsRed
                    ),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Icon(
                        Icons.Default.Delete,
                        contentDescription = null
                    )

                    Spacer(Modifier.width(8.dp))

                    Text("Delete Defect")
                }
            }
        }
    }

    // Edit dialog
    if (showEditDialog && defect != null) {

        EditDefectDialog(
            defect = defect!!,

            onDismiss = {
                showEditDialog = false
            },

            onSave = { updatedDefect ->

                viewModel.updateDefect(updatedDefect)

                showEditDialog = false
            }
        )
    }

    // Delete confirmation dialog
    if (showDeleteDialog && defect != null) {

        AlertDialog(
            onDismissRequest = {
                showDeleteDialog = false
            },

            containerColor = NavySurface,

            title = {
                Text(
                    "Delete Defect?",
                    color = DetailsWhite,
                    fontWeight = FontWeight.Bold
                )
            },

            text = {
                Text(
                    "Are you sure you want to permanently delete " +
                            "\"${defect!!.title}\"? This action cannot be undone.",
                    color = DetailsMuted
                )
            },

            confirmButton = {
                Button(
                    onClick = {

                        viewModel.deleteDefect(defect!!)

                        showDeleteDialog = false

                        onBack()
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = DetailsRed
                    )
                ) {
                    Text("Delete")
                }
            },

            dismissButton = {
                TextButton(
                    onClick = {
                        showDeleteDialog = false
                    }
                ) {
                    Text(
                        "Cancel",
                        color = DetailsMuted
                    )
                }
            }
        )
    }
}

// --------------------------------------------------
// EDIT DEFECT DIALOG
// --------------------------------------------------

@Composable
private fun EditDefectDialog(
    defect: DefectEntity,
    onDismiss: () -> Unit,
    onSave: (DefectEntity) -> Unit
) {

    var title by remember(defect.id) {
        mutableStateOf(defect.title)
    }

    var description by remember(defect.id) {
        mutableStateOf(defect.description)
    }

    var severity by remember(defect.id) {
        mutableStateOf(defect.severity)
    }

    var priority by remember(defect.id) {
        mutableStateOf(defect.priority)
    }

    var stepsToReproduce by remember(defect.id) {
        mutableStateOf(defect.stepsToReproduce)
    }

    var expectedResult by remember(defect.id) {
        mutableStateOf(defect.expectedResult)
    }

    var actualResult by remember(defect.id) {
        mutableStateOf(defect.actualResult)
    }

    var showValidation by remember {
        mutableStateOf(false)
    }

    AlertDialog(
        onDismissRequest = onDismiss,

        containerColor = NavySurface,

        title = {
            Text(
                "Edit Defect",
                color = DetailsWhite,
                fontWeight = FontWeight.Bold
            )
        },

        text = {

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(max = 500.dp)
                    .verticalScroll(rememberScrollState()),

                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {

                EditTextField(
                    value = title,
                    onValueChange = {
                        title = it
                    },
                    label = "Defect Title *"
                )

                EditTextField(
                    value = description,
                    onValueChange = {
                        description = it
                    },
                    label = "Description",
                    minLines = 2
                )

                EditDropdown(
                    label = "Severity",
                    selectedValue = severity,
                    options = listOf(
                        "Critical",
                        "High",
                        "Medium",
                        "Low"
                    ),
                    onValueSelected = {
                        severity = it
                    }
                )

                EditDropdown(
                    label = "Priority",
                    selectedValue = priority,
                    options = listOf(
                        "High",
                        "Medium",
                        "Low"
                    ),
                    onValueSelected = {
                        priority = it
                    }
                )

                EditTextField(
                    value = stepsToReproduce,
                    onValueChange = {
                        stepsToReproduce = it
                    },
                    label = "Steps to Reproduce",
                    minLines = 2
                )

                EditTextField(
                    value = expectedResult,
                    onValueChange = {
                        expectedResult = it
                    },
                    label = "Expected Result",
                    minLines = 2
                )

                EditTextField(
                    value = actualResult,
                    onValueChange = {
                        actualResult = it
                    },
                    label = "Actual Result",
                    minLines = 2
                )

                if (showValidation && title.isBlank()) {
                    Text(
                        "Please enter a defect title.",
                        color = DetailsRed,
                        fontSize = 12.sp
                    )
                }
            }
        },

        confirmButton = {
            Button(
                onClick = {

                    showValidation = true

                    if (title.isNotBlank()) {

                        onSave(
                            defect.copy(
                                title = title.trim(),
                                description = description.trim(),
                                severity = severity,
                                priority = priority,
                                stepsToReproduce = stepsToReproduce.trim(),
                                expectedResult = expectedResult.trim(),
                                actualResult = actualResult.trim(),
                                updatedAt = System.currentTimeMillis()
                            )
                        )
                    }
                },
                colors = ButtonDefaults.buttonColors(
                    containerColor = PurplePrimary
                )
            ) {
                Text("Save Changes")
            }
        },

        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text(
                    "Cancel",
                    color = DetailsMuted
                )
            }
        }
    )
}

// --------------------------------------------------
// REUSABLE EDIT FIELD
// --------------------------------------------------

@Composable
private fun EditTextField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    minLines: Int = 1
) {

    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        label = {
            Text(label)
        },
        modifier = Modifier.fillMaxWidth(),
        minLines = minLines
    )
}

// --------------------------------------------------
// REUSABLE DROPDOWN
// --------------------------------------------------

@Composable
private fun EditDropdown(
    label: String,
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

        OutlinedTextField(
            value = selectedValue,
            onValueChange = {},
            readOnly = true,
            label = {
                Text(label)
            },
            modifier = Modifier.fillMaxWidth()
        )

        Box(
            modifier = Modifier
                .matchParentSize()
                .clickable {
                    expanded = true
                }
        )

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

// --------------------------------------------------
// DETAIL SECTION
// --------------------------------------------------

@Composable
private fun DetailSection(
    title: String,
    content: @Composable ColumnScope.() -> Unit
) {

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = DetailsCard
        )
    ) {

        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {

            Text(
                text = title,
                color = Color(0xFFB9A2FF),
                fontWeight = FontWeight.SemiBold,
                fontSize = 14.sp
            )

            content()
        }
    }
}

// --------------------------------------------------
// DETAIL VALUE
// --------------------------------------------------

@Composable
private fun DetailValue(value: String) {

    Text(
        text = value.ifBlank {
            "Not provided"
        },
        color = DetailsWhite,
        fontSize = 14.sp
    )
}

// --------------------------------------------------
// DETAIL BADGE
// --------------------------------------------------

@Composable
private fun DetailBadge(
    text: String,
    color: Color
) {

    Surface(
        color = color.copy(alpha = 0.15f),
        shape = RoundedCornerShape(8.dp)
    ) {

        Text(
            text = text,
            color = color,
            fontSize = 12.sp,
            fontWeight = FontWeight.SemiBold,
            modifier = Modifier.padding(
                horizontal = 12.dp,
                vertical = 7.dp
            )
        )
    }
}

// --------------------------------------------------
// COLOUR HELPERS
// --------------------------------------------------

private fun severityColor(severity: String): Color {

    return when (severity) {
        "Critical" -> DetailsRed
        "High" -> DetailsOrange
        "Medium" -> DetailsBlue
        "Low" -> DetailsGreen
        else -> DetailsMuted
    }
}

private fun statusColor(status: String): Color {

    return when (status) {
        "Open" -> DetailsOrange
        "In Progress" -> DetailsBlue
        "Resolved" -> DetailsGreen
        "Closed" -> Color(0xFFB9A2FF)
        else -> DetailsMuted
    }
}

// --------------------------------------------------
// DATE FORMATTER
// --------------------------------------------------

private fun formatDate(timestamp: Long): String {

    return SimpleDateFormat(
        "dd MMM yyyy, hh:mm a",
        Locale.getDefault()
    ).format(Date(timestamp))
}
