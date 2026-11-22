
package com.example.testpilot.domain.validation

object ProjectValidator {

    fun validate(
        projectName: String,
        applicationName: String,
        version: String,
        startDate: Long,
        endDate: Long?
    ): String? {

        if (projectName.isBlank()) {
            return "Project name is required."
        }

        if (projectName.trim().length < 3) {
            return "Project name must contain at least 3 characters."
        }

        if (applicationName.isBlank()) {
            return "Application name is required."
        }

        if (version.isBlank()) {
            return "Application version is required."
        }

        if (endDate != null && endDate < startDate) {
            return "End date cannot be before start date."
        }

        return null
    }
}
