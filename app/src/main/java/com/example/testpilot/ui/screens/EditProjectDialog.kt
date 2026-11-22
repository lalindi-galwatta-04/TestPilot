
package com.example.testpilot.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.testpilot.data.local.ProjectEntity
import com.example.testpilot.domain.validation.ProjectValidator
import com.example.testpilot.ui.theme.*
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.TimeZone

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditProjectDialog(
    project: ProjectEntity,
    onDismiss: () -> Unit,
    onSave: (ProjectEntity) -> Unit
) {

    var projectName by remember(project.id) {
        mutableStateOf(project.projectName)
    }

    var applicationName by remember(project.id) {
        mutableStateOf(project.applicationName)
    }

    var description by remember(project.id) {
        mutableStateOf(project.description)
    }

    var version by remember(project.id) {
        mutableStateOf(project.version)
    }

    var status by remember(project.id) {
        mutableStateOf(project.status)
    }

    var startDate by remember(project.id) {
        mutableLongStateOf(project.startDate)
    }

    var endDate by remember(project.id) {
        mutableStateOf(project.endDate)
    }

    var showStartDatePicker by remember {
        mutableStateOf(false)
    }

    var showEndDatePicker by remember {
        mutableStateOf(false)
    }

    var errorMessage by remember {
        mutableStateOf<String?>(null)
    }

    val statuses = listOf("Active", "Completed", "On Hold")

    fun formatDate(timestamp: Long): String {
        return SimpleDateFormat(
            "dd MMM yyyy",
            Locale.getDefault()
        ).apply {
            timeZone = TimeZone.getTimeZone("UTC")
        }.format(Date(timestamp))
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = NavySurface,

        title = {
            Text(
                "Edit Project",
                color = TextPrimary
            )
        },

        text = {

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(max = 550.dp)
                    .verticalScroll(rememberScrollState()),

                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {

                OutlinedTextField(
                    value = projectName,
                    onValueChange = {
                        projectName = it
                        errorMessage = null
                    },
                    label = { Text("Project Name") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )

                OutlinedTextField(
                    value = applicationName,
                    onValueChange = {
                        applicationName = it
                        errorMessage = null
                    },
                    label = { Text("Application Name") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )

                OutlinedTextField(
                    value = description,
                    onValueChange = {
                        description = it
                    },
                    label = { Text("Description") },
                    modifier = Modifier.fillMaxWidth(),
                    minLines = 2
                )

                OutlinedTextField(
                    value = version,
                    onValueChange = {
                        version = it
                        errorMessage = null
                    },
                    label = { Text("Application Version") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )

                HorizontalDivider()

                Text(
                    "Project Dates",
                    color = TextPrimary,
                    style = MaterialTheme.typography.titleMedium
                )

                // Start Date

                Text(
                    "Start Date",
                    color = TextSecondary
                )

                OutlinedButton(
                    onClick = {
                        showStartDatePicker = true
                    },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(formatDate(startDate))
                }

                // End Date

                Text(
                    "End Date (Optional)",
                    color = TextSecondary
                )

                OutlinedButton(
                    onClick = {
                        showEndDatePicker = true
                    },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        endDate?.let { formatDate(it) }
                            ?: "Select End Date"
                    )
                }

                if (endDate != null) {

                    TextButton(
                        onClick = {
                            endDate = null
                        }
                    ) {
                        Text(
                            "Clear End Date",
                            color = FailureRed
                        )
                    }
                }

                HorizontalDivider()

                Text(
                    "Project Status",
                    color = TextPrimary,
                    style = MaterialTheme.typography.titleMedium
                )

                statuses.forEach { option ->

                    Row(
                        verticalAlignment =
                            androidx.compose.ui.Alignment.CenterVertically
                    ) {

                        RadioButton(
                            selected = status == option,
                            onClick = {
                                status = option
                                errorMessage = null
                            }
                        )

                        Text(
                            text = option,
                            color = TextPrimary
                        )
                    }
                }

                errorMessage?.let {

                    Text(
                        text = it,
                        color = MaterialTheme.colorScheme.error
                    )
                }
            }
        },

        confirmButton = {

            Button(
                onClick = {

                    // Centralised project validation

                    val validationError = ProjectValidator.validate(
                        projectName = projectName,
                        applicationName = applicationName,
                        version = version,
                        startDate = startDate,
                        endDate = endDate
                    )

                    if (validationError != null) {

                        errorMessage = validationError

                    } else {

                        val finalEndDate = when {
                            status == "Completed" ->
                                endDate ?: System.currentTimeMillis()

                            else -> endDate
                        }

                        onSave(
                            project.copy(
                                projectName = projectName.trim(),
                                applicationName = applicationName.trim(),
                                description = description.trim(),
                                version = version.trim(),
                                status = status,
                                startDate = startDate,
                                endDate = finalEndDate,
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

            TextButton(
                onClick = onDismiss
            ) {
                Text("Cancel")
            }
        }
    )

    // START DATE PICKER

    if (showStartDatePicker) {

        val datePickerState = rememberDatePickerState(
            initialSelectedDateMillis = startDate
        )

        DatePickerDialog(
            onDismissRequest = {
                showStartDatePicker = false
            },

            confirmButton = {

                TextButton(
                    onClick = {

                        datePickerState.selectedDateMillis?.let {

                            startDate = it

                            if (endDate != null && endDate!! < it) {
                                endDate = null
                            }
                        }

                        showStartDatePicker = false
                    }
                ) {
                    Text("Confirm")
                }
            },

            dismissButton = {

                TextButton(
                    onClick = {
                        showStartDatePicker = false
                    }
                ) {
                    Text("Cancel")
                }
            }
        ) {
            DatePicker(state = datePickerState)
        }
    }

    // END DATE PICKER

    if (showEndDatePicker) {

        val datePickerState = rememberDatePickerState(
            initialSelectedDateMillis = endDate ?: startDate,

            selectableDates = object : SelectableDates {

                override fun isSelectableDate(
                    utcTimeMillis: Long
                ): Boolean {
                    return utcTimeMillis >= startDate
                }

                override fun isSelectableYear(
                    year: Int
                ): Boolean {
                    return true
                }
            }
        )

        DatePickerDialog(
            onDismissRequest = {
                showEndDatePicker = false
            },

            confirmButton = {

                TextButton(
                    onClick = {

                        datePickerState.selectedDateMillis?.let {
                            endDate = it
                        }

                        showEndDatePicker = false
                    }
                ) {
                    Text("Confirm")
                }
            },

            dismissButton = {

                TextButton(
                    onClick = {
                        showEndDatePicker = false
                    }
                ) {
                    Text("Cancel")
                }
            }
        ) {
            DatePicker(state = datePickerState)
        }
    }
}
