
package com.example.testpilot.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.graphics.Color
import com.example.testpilot.ui.theme.*
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale
import java.util.TimeZone
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.DisplayMode
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.material3.SelectableDates
import com.example.testpilot.domain.validation.ProjectValidator

private fun todayUtcMillis(): Long {
    return Calendar.getInstance(TimeZone.getTimeZone("UTC")).apply {
        set(Calendar.HOUR_OF_DAY, 0)
        set(Calendar.MINUTE, 0)
        set(Calendar.SECOND, 0)
        set(Calendar.MILLISECOND, 0)
    }.timeInMillis
}

private fun formatProjectDate(timestamp: Long): String {
    return SimpleDateFormat(
        "dd MMM yyyy",
        Locale.getDefault()
    ).apply {
        timeZone = TimeZone.getTimeZone("UTC")
    }.format(timestamp)
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddProjectDialog(
    onDismiss: () -> Unit,
    onSave: (
        String,
        String,
        String,
        String,
        Long,
        Long?
    ) -> Unit
) {

    var projectName by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var applicationName by remember { mutableStateOf("") }
    var version by remember { mutableStateOf("1.0.0") }

    var startDate by remember { mutableLongStateOf(todayUtcMillis()) }
    var endDate by remember { mutableStateOf<Long?>(null) }

    var showStartDatePicker by remember { mutableStateOf(false) }
    var showEndDatePicker by remember { mutableStateOf(false) }

    var errorMessage by remember { mutableStateOf<String?>(null) }

    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = NavySurface,
        title = {
            Text("Create New Project", color = TextPrimary)
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
                    onValueChange = { description = it },
                    label = { Text("Description (Optional)") },
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

                // Start date

                Text(
                    "Project Start Date",
                    color = TextPrimary,
                    style = MaterialTheme.typography.labelLarge
                )

                OutlinedButton(
                    onClick = { showStartDatePicker = true },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(formatProjectDate(startDate))
                }

                // End date

                Text(
                    "Project End Date (Optional)",
                    color = TextPrimary,
                    style = MaterialTheme.typography.labelLarge
                )

                OutlinedButton(
                    onClick = { showEndDatePicker = true },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        endDate?.let { formatProjectDate(it) }
                            ?: "Select end date"
                    )
                }

                if (endDate != null) {
                    TextButton(
                        onClick = {
                            endDate = null
                            errorMessage = null
                        }
                    ) {
                        Text("Clear end date")
                    }
                }

                errorMessage?.let {
                    Text(
                        text = it,
                        color = MaterialTheme.colorScheme.error,
                        style = MaterialTheme.typography.bodySmall
                    )
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {

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
                        onSave(
                            projectName.trim(),
                            description.trim(),
                            applicationName.trim(),
                            version.trim(),
                            startDate,
                            endDate
                        )
                    }
                },
                colors = ButtonDefaults.buttonColors(
                    containerColor = PurplePrimary
                )
            ) {
                Text("Save Project")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel", color = TextSecondary)
            }
        }
    )

    // Start date picker

    if (showStartDatePicker) {

        val datePickerState = rememberDatePickerState(
            initialSelectedDateMillis = startDate,
            initialDisplayMode = DisplayMode.Picker
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

    // End date picker

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
