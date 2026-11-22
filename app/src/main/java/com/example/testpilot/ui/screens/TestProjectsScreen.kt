
package com.example.testpilot.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Folder
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.platform.LocalContext
import com.example.testpilot.data.local.ProjectEntity
import com.example.testpilot.data.local.TestPilotDatabase
import com.example.testpilot.ui.theme.NavyBackground
import com.example.testpilot.ui.theme.NavySurface
import com.example.testpilot.ui.theme.PurplePrimary
import com.example.testpilot.ui.theme.PurpleSecondary
import com.example.testpilot.ui.theme.TextPrimary
import com.example.testpilot.ui.theme.TextSecondary

private val TestWorkspaceMuted = Color(0xFF9CA3AF)
private val TestWorkspaceGreen = Color(0xFF66BB6A)
private val TestWorkspaceOrange = Color(0xFFFFB74D)

@Composable
fun TestProjectsScreen(
    onOpenProject: (Int) -> Unit
) {
    val context = LocalContext.current.applicationContext

    val database = remember(context) {
        TestPilotDatabase.getDatabase(context)
    }

    val projects by database.projectDao()
        .getAllProjects()
        .collectAsState(initial = emptyList())

    var searchQuery by remember {
        mutableStateOf("")
    }

    val filteredProjects = remember(projects, searchQuery) {
        projects.filter { project ->
            project.projectName.contains(
                searchQuery.trim(),
                ignoreCase = true
            ) || project.applicationName.contains(
                searchQuery.trim(),
                ignoreCase = true
            )
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(NavyBackground)
            .padding(horizontal = 20.dp)
            .padding(top = 20.dp, bottom = 16.dp)
    ) {
        Text(
            text = "Testing Workspace",
            color = TextPrimary,
            fontSize = 26.sp,
            fontWeight = FontWeight.Bold
        )

        Spacer(modifier = Modifier.height(6.dp))

        Text(
            text = "Select a project to manage its test suites and test cases.",
            color = TextSecondary,
            fontSize = 14.sp
        )

        Spacer(modifier = Modifier.height(22.dp))

        OutlinedTextField(
            value = searchQuery,
            onValueChange = { searchQuery = it },
            modifier = Modifier.fillMaxWidth(),
            placeholder = {
                Text(
                    text = "Search projects...",
                    color = TestWorkspaceMuted
                )
            },
            leadingIcon = {
                Icon(
                    imageVector = Icons.Default.Search,
                    contentDescription = "Search projects",
                    tint = TestWorkspaceMuted
                )
            },
            singleLine = true,
            shape = RoundedCornerShape(14.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedTextColor = TextPrimary,
                unfocusedTextColor = TextPrimary,
                focusedBorderColor = PurplePrimary,
                unfocusedBorderColor = TestWorkspaceMuted,
                cursorColor = PurplePrimary,
                focusedContainerColor = NavySurface,
                unfocusedContainerColor = NavySurface
            )
        )

        Spacer(modifier = Modifier.height(20.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Your Projects",
                color = TextPrimary,
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold
            )

            Text(
                text = "${filteredProjects.size} found",
                color = TextSecondary,
                fontSize = 12.sp
            )
        }

        Spacer(modifier = Modifier.height(14.dp))

        if (projects.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Folder,
                        contentDescription = null,
                        tint = PurpleSecondary,
                        modifier = Modifier.size(52.dp)
                    )

                    Text(
                        text = "No projects available",
                        color = TextPrimary,
                        fontWeight = FontWeight.Bold
                    )

                    Text(
                        text = "Create a project in the Projects tab first.",
                        color = TextSecondary,
                        fontSize = 13.sp
                    )
                }
            }
        } else if (filteredProjects.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "No matching projects found.",
                    color = TextSecondary
                )
            }
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(
                    items = filteredProjects,
                    key = { it.id }
                ) { project ->

                    TestWorkspaceProjectCard(
                        project = project,
                        onClick = {
                            onOpenProject(project.id)
                        }
                    )
                }
            }
        }
    }
}

@Composable
private fun TestWorkspaceProjectCard(
    project: ProjectEntity,
    onClick: () -> Unit
) {
    val statusColor = when (project.status) {
        "Completed" -> TestWorkspaceGreen
        "On Hold" -> TestWorkspaceOrange
        else -> TestWorkspaceGreen
    }

    Card(
        onClick = onClick,
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(
            containerColor = NavySurface
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(17.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Default.Folder,
                    contentDescription = null,
                    tint = PurpleSecondary,
                    modifier = Modifier.size(30.dp)
                )

                Spacer(modifier = Modifier.size(12.dp))

                Column(
                    modifier = Modifier.weight(1f)
                ) {
                    Text(
                        text = project.projectName,
                        color = TextPrimary,
                        fontSize = 17.sp,
                        fontWeight = FontWeight.Bold
                    )

                    Text(
                        text = project.applicationName,
                        color = TextSecondary,
                        fontSize = 13.sp
                    )
                }

                Icon(
                    imageVector = Icons.Default.ArrowForward,
                    contentDescription = "Open testing workspace",
                    tint = PurpleSecondary
                )
            }

            Text(
                text = "Version: ${project.version}",
                color = TestWorkspaceMuted,
                fontSize = 12.sp
            )

            Text(
                text = "Status: ${project.status}",
                color = statusColor,
                fontSize = 12.sp,
                fontWeight = FontWeight.SemiBold
            )

            Spacer(modifier = Modifier.height(2.dp))

            Text(
                text = "Open Testing Workspace →",
                color = PurpleSecondary,
                fontSize = 13.sp,
                fontWeight = FontWeight.Bold
            )
        }
    }
}
