
package com.example.testpilot.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Folder
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.text.font.FontWeight
import com.example.testpilot.data.local.ProjectEntity
import com.example.testpilot.ui.theme.*
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun ProjectDetailsScreen(
    project: ProjectEntity,
    onBack: () -> Unit,
    onEdit: () -> Unit
) {

    val dateFormat = SimpleDateFormat(
        "dd MMM yyyy",
        Locale.getDefault()
    )

    fun formatDate(timestamp: Long): String {
        return dateFormat.format(Date(timestamp))
    }

    val statusColor = when (project.status) {
        "Completed" -> SuccessGreen
        "On Hold" -> WarningOrange
        else -> PurpleSecondary
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(NavyBackground)
            .verticalScroll(rememberScrollState())
            .padding(20.dp)
    ) {

        // HEADER

        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {

            IconButton(onClick = onBack) {
                Icon(
                    Icons.Default.ArrowBack,
                    contentDescription = "Back",
                    tint = TextPrimary
                )
            }

            Text(
                text = "Project Details",
                color = TextPrimary,
                fontSize = 22.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.weight(1f)
            )

            IconButton(onClick = onEdit) {
                Icon(
                    Icons.Default.Edit,
                    contentDescription = "Edit project",
                    tint = PurpleSecondary
                )
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // PROJECT INFORMATION CARD

        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(
                containerColor = NavySurface
            )
        ) {

            Column(
                modifier = Modifier.padding(20.dp)
            ) {

                Icon(
                    Icons.Default.Folder,
                    contentDescription = null,
                    tint = PurpleSecondary,
                    modifier = Modifier.size(42.dp)
                )

                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = project.projectName,
                    color = TextPrimary,
                    fontSize = 23.sp,
                    fontWeight = FontWeight.Bold
                )

                Spacer(modifier = Modifier.height(6.dp))

                Text(
                    text = project.applicationName,
                    color = TextSecondary,
                    fontSize = 16.sp
                )

                Spacer(modifier = Modifier.height(16.dp))

                Surface(
                    color = statusColor.copy(alpha = 0.15f),
                    shape = RoundedCornerShape(20.dp)
                ) {
                    Text(
                        text = project.status,
                        color = statusColor,
                        modifier = Modifier.padding(
                            horizontal = 14.dp,
                            vertical = 7.dp
                        ),
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(22.dp))

        // PROJECT INFORMATION

        Text(
            text = "Project Information",
            color = TextPrimary,
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold
        )

        Spacer(modifier = Modifier.height(14.dp))

        InfoRow("Application Version", project.version)

        InfoRow(
            "Start Date",
            formatDate(project.startDate)
        )

        InfoRow(
            "End Date",
            project.endDate?.let {
                formatDate(it)
            } ?: "Not set"
        )

        InfoRow(
            "Created",
            formatDate(project.createdAt)
        )

        InfoRow(
            "Last Updated",
            formatDate(project.updatedAt)
        )

        Spacer(modifier = Modifier.height(20.dp))

        // DESCRIPTION

        Text(
            text = "Description",
            color = TextPrimary,
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold
        )

        Spacer(modifier = Modifier.height(10.dp))

        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(
                containerColor = NavySurface
            )
        ) {
            Text(
                text = project.description.ifBlank {
                    "No description provided."
                },
                color = TextSecondary,
                modifier = Modifier.padding(16.dp),
                fontSize = 14.sp
            )
        }

        Spacer(modifier = Modifier.height(24.dp))

        // EDIT PROJECT

        OutlinedButton(
            onClick = onEdit,
            modifier = Modifier
                .fillMaxWidth()
                .height(54.dp),
            shape = RoundedCornerShape(14.dp),
            colors = ButtonDefaults.outlinedButtonColors(
                contentColor = PurpleSecondary
            )
        ) {
            Icon(
                Icons.Default.Edit,
                contentDescription = null
            )

            Spacer(modifier = Modifier.width(8.dp))

            Text("Edit Project")
        }

        Spacer(modifier = Modifier.height(12.dp))
    }
}

@Composable
private fun InfoRow(
    label: String,
    value: String
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 9.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = label,
            color = TextSecondary,
            fontSize = 13.sp
        )

        Text(
            text = value,
            color = TextPrimary,
            fontSize = 13.sp,
            fontWeight = FontWeight.Medium
        )
    }
}
