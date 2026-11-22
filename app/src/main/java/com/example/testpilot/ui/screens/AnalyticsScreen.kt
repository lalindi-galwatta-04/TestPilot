
package com.example.testpilot.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Analytics
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.Folder
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.testpilot.data.local.ProjectEntity
import com.example.testpilot.ui.theme.NavyBackground
import com.example.testpilot.ui.theme.NavySurface
import com.example.testpilot.ui.theme.PurplePrimary
import com.example.testpilot.ui.theme.PurpleSecondary
import com.example.testpilot.ui.theme.TextPrimary
import com.example.testpilot.ui.theme.TextSecondary

@Composable
fun AnalyticsScreen(
    projects: List<ProjectEntity>,
    onOpenProject: (Int) -> Unit
) {

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(NavyBackground)
            .padding(horizontal = 18.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {

        item {
            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "QA Analytics",
                color = TextPrimary,
                fontSize = 27.sp,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(6.dp))

            Text(
                text = "Explore the quality and testing performance of your projects.",
                color = TextSecondary,
                fontSize = 14.sp
            )

            Spacer(modifier = Modifier.height(18.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "Your Projects",
                    color = TextPrimary,
                    fontSize = 19.sp,
                    fontWeight = FontWeight.SemiBold
                )

                Text(
                    text = "${projects.size} Projects",
                    color = PurpleSecondary,
                    fontSize = 13.sp
                )
            }
        }

        if (projects.isEmpty()) {

            item {
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
                            .padding(28.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {

                        Icon(
                            imageVector = Icons.Default.Analytics,
                            contentDescription = null,
                            tint = PurpleSecondary,
                            modifier = Modifier
                                .padding(bottom = 14.dp)
                        )

                        Text(
                            text = "No Projects Available",
                            color = TextPrimary,
                            fontSize = 17.sp,
                            fontWeight = FontWeight.Bold
                        )

                        Spacer(modifier = Modifier.height(6.dp))

                        Text(
                            text = "Create a project first to view its analytics.",
                            color = TextSecondary,
                            fontSize = 13.sp
                        )
                    }
                }
            }

        } else {

            items(
                items = projects,
                key = { it.id }
            ) { project ->

                AnalyticsProjectCard(
                    project = project,
                    onClick = {
                        onOpenProject(project.id)
                    }
                )
            }
        }

        item {
            Spacer(modifier = Modifier.height(20.dp))
        }
    }
}

@Composable
private fun AnalyticsProjectCard(
    project: ProjectEntity,
    onClick: () -> Unit
) {

    val statusColor = when (project.status) {
        "Completed" -> Color(0xFF4CAF50)
        "On Hold" -> Color(0xFFFFB74D)
        else -> Color(0xFF64B5F6)
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
            modifier = Modifier.padding(18.dp)
        ) {

            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {

                Icon(
                    imageVector = Icons.Default.Folder,
                    contentDescription = null,
                    tint = PurpleSecondary
                )

                Column(
                    modifier = Modifier
                        .weight(1f)
                        .padding(start = 12.dp)
                ) {

                    Text(
                        text = project.projectName,
                        color = TextPrimary,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold
                    )

                    Spacer(modifier = Modifier.height(4.dp))

                    Text(
                        text = project.applicationName,
                        color = TextSecondary,
                        fontSize = 13.sp
                    )
                }

                Icon(
                    imageVector = Icons.Default.ChevronRight,
                    contentDescription = "View project analytics",
                    tint = PurplePrimary
                )
            }

            Spacer(modifier = Modifier.height(14.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {

                Text(
                    text = "Version ${project.version}",
                    color = PurpleSecondary,
                    fontSize = 12.sp
                )

                Text(
                    text = project.status,
                    color = statusColor,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.SemiBold
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = "Tap to view detailed analytics",
                color = TextSecondary,
                fontSize = 12.sp
            )
        }
    }
}
