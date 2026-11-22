
package com.example.testpilot.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Folder
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.testpilot.data.local.ProjectEntity
import com.example.testpilot.data.local.TestPilotDatabase
import com.example.testpilot.data.repository.ProjectRepository
import com.example.testpilot.ui.theme.*
import com.example.testpilot.viewmodel.ProjectViewModel
import com.example.testpilot.viewmodel.ProjectViewModelFactory

@Composable
fun ProjectsScreen()  {

    val context = LocalContext.current.applicationContext

    val repository = remember(context) {
        ProjectRepository(
            TestPilotDatabase
                .getDatabase(context)
                .projectDao()
        )
    }

    val factory = remember(repository) {
        ProjectViewModelFactory(repository)
    }

    val projectViewModel: ProjectViewModel = viewModel(
        factory = factory
    )

    val uiState by projectViewModel.uiState.collectAsState()

    var showAddDialog by remember {
        mutableStateOf(false)
    }

    var showEditDialog by remember {
        mutableStateOf(false)
    }

    var selectedProject by remember {
        mutableStateOf<ProjectEntity?>(null)
    }

    var projectToDelete by remember {
        mutableStateOf<ProjectEntity?>(null)
    }

    val statuses = listOf("All", "Active", "Completed", "On Hold")

    // Show project details when a project is selected.

    selectedProject?.let { project ->

        ProjectDetailsScreen(
            project = project,
            onBack = {
                selectedProject = null
            },
            onEdit = {
                showEditDialog = true
            }
        )

        if (showEditDialog) {

            EditProjectDialog(
                project = project,
                onDismiss = {
                    showEditDialog = false
                },
                onSave = { updatedProject ->

                    projectViewModel.updateProject(updatedProject)

                    selectedProject = updatedProject
                    showEditDialog = false
                }
            )
        }

        return
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(NavyBackground)
            .padding(20.dp)
    ) {

        Text(
            text = "My Projects",
            color = TextPrimary,
            fontSize = 27.sp,
            fontWeight = FontWeight.Bold
        )

        Spacer(modifier = Modifier.height(6.dp))

        Text(
            text = "Manage your software testing projects.",
            color = TextSecondary,
            fontSize = 14.sp
        )

        Spacer(modifier = Modifier.height(22.dp))

        OutlinedTextField(
            value = uiState.searchQuery,
            onValueChange = projectViewModel::updateSearchQuery,
            modifier = Modifier.fillMaxWidth(),
            placeholder = {
                Text("Search projects...")
            },
            leadingIcon = {
                Icon(
                    Icons.Default.Search,
                    contentDescription = "Search"
                )
            },
            singleLine = true,
            shape = RoundedCornerShape(14.dp)
        )

        Spacer(modifier = Modifier.height(16.dp))

        Row(
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            statuses.forEach { status ->

                FilterChip(
                    selected = uiState.selectedStatus == status,
                    onClick = {
                        projectViewModel.updateSelectedStatus(status)
                    },
                    label = {
                        Text(status)
                    },
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = PurplePrimary,
                        selectedLabelColor = TextPrimary
                    )
                )
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        Text(
            text = "${uiState.projects.size} Projects",
            color = TextPrimary,
            fontWeight = FontWeight.SemiBold,
            fontSize = 17.sp
        )

        Spacer(modifier = Modifier.height(12.dp))

        if (uiState.projects.isEmpty()) {

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {

                Icon(
                    imageVector = Icons.Default.Folder,
                    contentDescription = null,
                    tint = PurpleSecondary,
                    modifier = Modifier.size(55.dp)
                )

                Spacer(modifier = Modifier.height(14.dp))

                Text(
                    text = if (uiState.searchQuery.isNotEmpty()) {
                        "No matching projects found"
                    } else {
                        "No projects yet"
                    },
                    color = TextPrimary,
                    fontWeight = FontWeight.Bold
                )

                Spacer(modifier = Modifier.height(6.dp))

                Text(
                    text = "Create your first testing project.",
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
                    items = uiState.projects,
                    key = { it.id }
                ) { project ->

                    ProjectCard(
                        project = project,
                        onClick = {
                            selectedProject = project
                        },
                        onDelete = {
                            projectToDelete = project
                        }
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

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
                Icons.Default.Add,
                contentDescription = null
            )

            Spacer(modifier = Modifier.width(8.dp))

            Text("Add New Project")
        }
    }

    if (showAddDialog) {

        AddProjectDialog(
            onDismiss = {
                showAddDialog = false
            },
            onSave = { name, description, app, version, start, end ->

                projectViewModel.addProject(
                    projectName = name,
                    description = description,
                    applicationName = app,
                    version = version,
                    startDate = start,
                    endDate = end
                )

                showAddDialog = false
            }
        )
    }

    projectToDelete?.let { project ->

        AlertDialog(
            onDismissRequest = {
                projectToDelete = null
            },
            containerColor = NavySurface,
            title = {
                Text("Delete Project?", color = TextPrimary)
            },
            text = {
                Text(
                    "Are you sure you want to delete ${project.projectName}?",
                    color = TextSecondary
                )
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        projectViewModel.deleteProject(project)
                        projectToDelete = null
                    }
                ) {
                    Text("Delete", color = FailureRed)
                }
            },
            dismissButton = {
                TextButton(
                    onClick = {
                        projectToDelete = null
                    }
                ) {
                    Text("Cancel")
                }
            }
        )
    }
}

@Composable
private fun ProjectCard(
    project: ProjectEntity,
    onClick: () -> Unit,
    onDelete: () -> Unit
) {

    val statusColor = when (project.status) {
        "Completed" -> SuccessGreen
        "On Hold" -> WarningOrange
        else -> SuccessGreen
    }

    Card(
        onClick = onClick,
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(
            containerColor = NavySurface
        )
    ) {

        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {

            Icon(
                imageVector = Icons.Default.Folder,
                contentDescription = null,
                tint = PurpleSecondary,
                modifier = Modifier.size(32.dp)
            )

            Spacer(modifier = Modifier.width(14.dp))

            Column(
                modifier = Modifier.weight(1f)
            ) {

                Text(
                    text = project.projectName,
                    color = TextPrimary,
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp
                )

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = project.applicationName,
                    color = TextSecondary,
                    fontSize = 13.sp
                )

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = "Version ${project.version}",
                    color = PurpleSecondary,
                    fontSize = 12.sp
                )

                Spacer(modifier = Modifier.height(6.dp))

                Text(
                    text = project.status,
                    color = statusColor,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.SemiBold
                )
            }

            IconButton(onClick = onDelete) {
                Icon(
                    imageVector = Icons.Default.Delete,
                    contentDescription = "Delete project",
                    tint = FailureRed
                )
            }
        }
    }
}
