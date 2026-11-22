
package com.example.testpilot.ui.navigation

sealed class Screen(
    val route: String,
    val title: String
) {

    data object Dashboard :
        Screen("dashboard", "Dashboard")

    data object Projects :
        Screen("projects", "Projects")

    data object TestCases :
        Screen("test_cases", "Tests")

    data object TestSuites :
        Screen("test_suites/{projectId}", "Test Suites") {

        fun createRoute(projectId: Int): String {
            return "test_suites/$projectId"
        }
    }

    data object TestCasesInSuite :
        Screen("suite_test_cases/{suiteId}", "Test Cases") {

        fun createRoute(suiteId: Int): String {
            return "suite_test_cases/$suiteId"
        }
    }

    data object Defects :
        Screen("defects", "Defects")

    data object DefectDetails :
        Screen("defect_details/{defectId}", "Defect Details") {

        fun createRoute(defectId: Int): String {
            return "defect_details/$defectId"
        }
    }

    data object Analytics :
        Screen("analytics", "Analytics")

    data object ProjectAnalytics :
        Screen("project_analytics/{projectId}", "Project Analytics") {

        fun createRoute(projectId: Int): String {
            return "project_analytics/$projectId"
        }
    }
}
