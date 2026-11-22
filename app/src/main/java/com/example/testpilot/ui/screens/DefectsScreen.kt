
package com.example.testpilot.ui.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.BugReport
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.compose.ui.platform.LocalContext
import com.example.testpilot.data.local.DefectEntity
import com.example.testpilot.data.local.ProjectEntity
import com.example.testpilot.data.local.TestPilotDatabase
import com.example.testpilot.data.repository.DefectRepository
import com.example.testpilot.ui.theme.NavySurface
import com.example.testpilot.ui.theme.PurplePrimary
import com.example.testpilot.ui.theme.TextSecondary
import com.example.testpilot.viewmodel.DefectViewModel
import com.example.testpilot.viewmodel.DefectViewModelFactory
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

private val DefectWhite = Color(0xFFFFFFFF)
private val DefectCard = Color(0xFF202A42)
private val DefectMuted = Color(0xFF9CA3AF)
private val DefectPurple = Color(0xFFB9A2FF)
private val DefectRed = Color(0xFFEF5350)
private val DefectOrange = Color(0xFFFFB74D)
private val DefectGreen = Color(0xFF66BB6A)
private val DefectBlue = Color(0xFF64B5F6)

@Composable
fun DefectsScreen(
onOpenDefect: (Int) -> Unit = {}
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
        factory = factory
    )

    val defects by viewModel.defects.collectAsState()

    val projects by database.projectDao()
        .getAllProjects()
        .collectAsState(initial = emptyList())

    val searchQuery by viewModel.searchQuery.collectAsState()
    val selectedStatus by viewModel.selectedStatus.collectAsState()
    val selectedSeverity by viewModel.selectedSeverity.collectAsState()

    var showAddDialog by remember {
        mutableStateOf(false)
    }


    val filteredDefects = remember(
        defects,
        searchQuery,
        selectedStatus,
        selectedSeverity
    ) {
        val normalizedSearch = searchQuery.trim()

        defects.filter { defect ->

            val matchesSearch =
                normalizedSearch.isBlank() ||
                        defect.title.contains(
                            normalizedSearch,
                            ignoreCase = true
                        ) ||
                        defect.description.contains(
                            normalizedSearch,
                            ignoreCase = true
                        )

            val matchesStatus =
                selectedStatus.equals("All", ignoreCase = true) ||
                        defect.status.trim().equals(
                            selectedStatus.trim(),
                            ignoreCase = true
                        )

            val matchesSeverity =
                selectedSeverity.equals("All", ignoreCase = true) ||
                        defect.severity.trim().equals(
                            selectedSeverity.trim(),
                            ignoreCase = true
                        )

            matchesSearch && matchesStatus && matchesSeverity
        }
    }


    val totalCount = defects.size
    val openCount = defects.count { it.status == "Open" }
    val criticalCount = defects.count { it.severity == "Critical" }
    val resolvedCount = defects.count {
        it.status == "Resolved" || it.status == "Closed"
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 16.dp)
            .padding(top = 20.dp)
            .padding(bottom = 24.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {

        // Header
        Column(
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Text(
                text = "Defects",
                color = DefectWhite,
                fontSize = 26.sp,
                fontWeight = FontWeight.Bold
            )

            Text(
                text = "Manage and track software defects",
                color = TextSecondary,
                fontSize = 14.sp
            )
        }

        // Summary cards
        Column(
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {

            Row(
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                DefectSummaryCard(
                    title = "Total Defects",
                    count = totalCount,
                    accent = DefectPurple,
                    modifier = Modifier.weight(1f)
                )

                DefectSummaryCard(
                    title = "Open",
                    count = openCount,
                    accent = DefectOrange,
                    modifier = Modifier.weight(1f)
                )
            }

            Row(
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                DefectSummaryCard(
                    title = "Critical",
                    count = criticalCount,
                    accent = DefectRed,
                    modifier = Modifier.weight(1f)
                )

                DefectSummaryCard(
                    title = "Resolved",
                    count = resolvedCount,
                    accent = DefectGreen,
                    modifier = Modifier.weight(1f)
                )
            }
        }

        // Add defect button
        Button(
            onClick = {
                showAddDialog = true
            },
            modifier = Modifier.fillMaxWidth(),
            colors = ButtonDefaults.buttonColors(
                containerColor = PurplePrimary,
                contentColor = DefectWhite
            ),
            shape = RoundedCornerShape(12.dp)
        ) {
            Icon(
                imageVector = Icons.Default.Add,
                contentDescription = null
            )

            Spacer(modifier = Modifier.width(8.dp))

            Text("Report New Defect")
        }

        // Search field
        OutlinedTextField(
            value = searchQuery,
            onValueChange = viewModel::updateSearchQuery,
            modifier = Modifier.fillMaxWidth(),
            placeholder = {
                Text(
                    "Search defects...",
                    color = DefectMuted
                )
            },
            leadingIcon = {
                Icon(
                    imageVector = Icons.Default.Search,
                    contentDescription = "Search",
                    tint = DefectMuted
                )
            },
            singleLine = true,
            shape = RoundedCornerShape(12.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedTextColor = DefectWhite,
                unfocusedTextColor = DefectWhite,
                focusedBorderColor = PurplePrimary,
                unfocusedBorderColor = DefectMuted,
                cursorColor = PurplePrimary,
                focusedContainerColor = DefectCard,
                unfocusedContainerColor = DefectCard
            )
        )

        // Status filters
        Text(
            text = "Filter by Status",
            color = DefectWhite,
            fontWeight = FontWeight.SemiBold
        )

        Row(
            modifier = Modifier.horizontalScroll(
                rememberScrollState()
            ),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {

            listOf(
                "All",
                "Open",
                "In Progress",
                "Resolved",
                "Closed"
            ).forEach { status ->

                FilterChip(
                    selected = selectedStatus == status,
                    onClick = {
                        viewModel.updateStatusFilter(status)
                    },
                    label = {
                        Text(status)
                    },
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = PurplePrimary,
                        selectedLabelColor = DefectWhite,
                        containerColor = DefectCard,
                        labelColor = DefectMuted
                    )
                )
            }
        }

        // Severity filters
        Text(
            text = "Filter by Severity",
            color = DefectWhite,
            fontWeight = FontWeight.SemiBold
        )

        Row(
            modifier = Modifier.horizontalScroll(
                rememberScrollState()
            ),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {

            listOf(
                "All",
                "Critical",
                "High",
                "Medium",
                "Low"
            ).forEach { severity ->

                FilterChip(
                    selected = selectedSeverity == severity,
                    onClick = {
                        viewModel.updateSeverityFilter(severity)
                    },
                    label = {
                        Text(severity)
                    },
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = PurplePrimary,
                        selectedLabelColor = DefectWhite,
                        containerColor = DefectCard,
                        labelColor = DefectMuted
                    )
                )
            }
        }

        // Defect list heading
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "All Defects",
                color = DefectWhite,
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold
            )

            Text(
                text = "${filteredDefects.size} found",
                color = DefectMuted,
                fontSize = 12.sp
            )
        }

        // Defect list
        if (filteredDefects.isEmpty()) {

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(min = 180.dp),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.BugReport,
                        contentDescription = null,
                        tint = DefectMuted,
                        modifier = Modifier.size(42.dp)
                    )

                    Text(
                        text = "No defects found",
                        color = DefectWhite,
                        fontWeight = FontWeight.SemiBold
                    )

                    Text(
                        text = "Report a new defect or change your filters.",
                        color = DefectMuted,
                        fontSize = 12.sp
                    )
                }
            }

        } else {

            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {

                filteredDefects.forEach { defect ->

                    val projectName = projects.find {
                        it.id == defect.projectId
                    }?.projectName ?: "Unknown Project"

                    DefectItemCard(
                        defect = defect,
                        projectName = projectName,
                        onClick = {
                            onOpenDefect(defect.id)
                        }
                    )
                }
            }
        }
    }

    // Add defect dialog
    if (showAddDialog) {
        AddDefectDialog(
            projects = projects,
            onDismiss = {
                showAddDialog = false
            },
            onSave = { defect ->
                viewModel.addDefect(defect)
                showAddDialog = false
            }
        )
    }
}

@Composable
private fun DefectSummaryCard(
    title: String,
    count: Int,
    accent: Color,
    modifier: Modifier = Modifier
) {

    Card(
        modifier = modifier,
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(
            containerColor = DefectCard
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {

            Text(
                text = title,
                color = DefectMuted,
                fontSize = 12.sp
            )

            Text(
                text = count.toString(),
                color = accent,
                fontSize = 26.sp,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

@Composable
private fun DefectItemCard(
    defect: DefectEntity,
    projectName: String,
    onClick: () -> Unit
) {

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(
            containerColor = DefectCard
        )
    ) {

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {

            Text(
                text = "DEF-${defect.id.toString().padStart(3, '0')}",
                color = DefectPurple,
                fontSize = 11.sp,
                fontWeight = FontWeight.SemiBold
            )

            Text(
                text = defect.title,
                color = DefectWhite,
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold
            )

            Text(
                text = projectName,
                color = DefectMuted,
                fontSize = 12.sp
            )

            if (defect.description.isNotBlank()) {
                Text(
                    text = defect.description,
                    color = DefectMuted,
                    fontSize = 13.sp,
                    maxLines = 2
                )
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {

                DefectLabel(
                    text = defect.severity,
                    color = severityColor(defect.severity)
                )

                DefectLabel(
                    text = defect.status,
                    color = statusColor(defect.status)
                )
            }

            Text(
                text = "Reported: ${
                    SimpleDateFormat(
                        "dd MMM yyyy",
                        Locale.getDefault()
                    ).format(Date(defect.reportedAt))
                }",
                color = DefectMuted,
                fontSize = 11.sp
            )
        }
    }
}

@Composable
private fun DefectLabel(
    text: String,
    color: Color
) {
    Surface(
        shape = RoundedCornerShape(8.dp),
        color = color.copy(alpha = 0.15f)
    ) {
        Text(
            text = text,
            color = color,
            fontSize = 11.sp,
            fontWeight = FontWeight.SemiBold,
            modifier = Modifier.padding(
                horizontal = 10.dp,
                vertical = 6.dp
            )
        )
    }
}

private fun severityColor(severity: String): Color {
    return when (severity) {
        "Critical" -> DefectRed
        "High" -> DefectOrange
        "Medium" -> DefectBlue
        "Low" -> DefectGreen
        else -> DefectMuted
    }
}

private fun statusColor(status: String): Color {
    return when (status) {
        "Open" -> DefectOrange
        "In Progress" -> DefectBlue
        "Resolved" -> DefectGreen
        "Closed" -> DefectPurple
        else -> DefectMuted
    }
}

@Composable
private fun AddDefectDialog(
    projects: List<ProjectEntity>,
    onDismiss: () -> Unit,
    onSave: (DefectEntity) -> Unit
) {

    var title by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var severity by remember { mutableStateOf("Medium") }
    var priority by remember { mutableStateOf("Medium") }
    var selectedProject by remember {
        mutableStateOf<ProjectEntity?>(null)
    }

    var stepsToReproduce by remember {
        mutableStateOf("")
    }

    var expectedResult by remember {
        mutableStateOf("")
    }

    var actualResult by remember {
        mutableStateOf("")
    }

    var showValidation by remember {
        mutableStateOf(false)
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = NavySurface,
        title = {
            Text(
                text = "Report New Defect",
                color = DefectWhite,
                fontWeight = FontWeight.Bold
            )
        },
        text = {

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(max = 520.dp)
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {

                OutlinedTextField(
                    value = title,
                    onValueChange = { title = it },
                    label = { Text("Defect Title *") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )

                OutlinedTextField(
                    value = description,
                    onValueChange = { description = it },
                    label = { Text("Description") },
                    modifier = Modifier.fillMaxWidth(),
                    minLines = 2
                )

                Text(
                    text = "Project *",
                    color = DefectWhite,
                    fontWeight = FontWeight.SemiBold
                )

                ProjectDropdown(
                    projects = projects,
                    selectedProject = selectedProject,
                    onProjectSelected = {
                        selectedProject = it
                    }
                )

                DropdownSelector(
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

                DropdownSelector(
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

                OutlinedTextField(
                    value = stepsToReproduce,
                    onValueChange = { stepsToReproduce = it },
                    label = { Text("Steps to Reproduce") },
                    modifier = Modifier.fillMaxWidth(),
                    minLines = 2
                )

                OutlinedTextField(
                    value = expectedResult,
                    onValueChange = { expectedResult = it },
                    label = { Text("Expected Result") },
                    modifier = Modifier.fillMaxWidth(),
                    minLines = 2
                )

                OutlinedTextField(
                    value = actualResult,
                    onValueChange = { actualResult = it },
                    label = { Text("Actual Result") },
                    modifier = Modifier.fillMaxWidth(),
                    minLines = 2
                )

                if (showValidation) {
                    Text(
                        text = when {
                            title.isBlank() ->
                                "Please enter a defect title."

                            selectedProject == null ->
                                "Please select a project."

                            else -> ""
                        },
                        color = DefectRed,
                        fontSize = 12.sp
                    )
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {

                    showValidation = true

                    if (
                        title.isNotBlank() &&
                        selectedProject != null
                    ) {

                        val defect = DefectEntity(
                            title = title.trim(),
                            description = description.trim(),
                            severity = severity,
                            priority = priority,
                            status = "Open",
                            projectId = selectedProject!!.id,
                            stepsToReproduce = stepsToReproduce.trim(),
                            expectedResult = expectedResult.trim(),
                            actualResult = actualResult.trim()
                        )

                        onSave(defect)
                    }
                },
                colors = ButtonDefaults.buttonColors(
                    containerColor = PurplePrimary
                )
            ) {
                Text("Save Defect")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text(
                    text = "Cancel",
                    color = DefectMuted
                )
            }
        }
    )
}

@Composable
private fun ProjectDropdown(
    projects: List<ProjectEntity>,
    selectedProject: ProjectEntity?,
    onProjectSelected: (ProjectEntity) -> Unit
) {

    var expanded by remember {
        mutableStateOf(false)
    }

    Box(
        modifier = Modifier.fillMaxWidth()
    ) {

        OutlinedTextField(
            value = selectedProject?.projectName ?: "",
            onValueChange = {},
            readOnly = true,
            label = { Text("Select Project") },
            placeholder = { Text("Choose a project") },
            modifier = Modifier
                .fillMaxWidth()
                .clickable {
                    expanded = true
                },
            trailingIcon = {
                Text(
                    text = "▼",
                    color = DefectMuted,
                    modifier = Modifier.clickable {
                        expanded = true
                    }
                )
            }
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
            },
            modifier = Modifier
                .fillMaxWidth(0.85f)
                .heightIn(max = 250.dp)
        ) {

            if (projects.isEmpty()) {
                DropdownMenuItem(
                    text = {
                        Text("No projects available")
                    },
                    onClick = {
                        expanded = false
                    }
                )
            } else {

                projects.forEach { project ->

                    DropdownMenuItem(
                        text = {
                            Text(project.projectName)
                        },
                        onClick = {
                            onProjectSelected(project)
                            expanded = false
                        }
                    )
                }
            }
        }
    }
}

@Composable
private fun DropdownSelector(
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
            label = { Text(label) },
            modifier = Modifier
                .fillMaxWidth()
                .clickable {
                    expanded = true
                },
            trailingIcon = {
                Text(
                    text = "▼",
                    color = DefectMuted,
                    modifier = Modifier.clickable {
                        expanded = true
                    }
                )
            }
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
