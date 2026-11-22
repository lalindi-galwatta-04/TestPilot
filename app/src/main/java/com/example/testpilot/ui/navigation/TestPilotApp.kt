
package com.example.testpilot.ui.navigation

import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Analytics
import androidx.compose.material.icons.filled.BugReport
import androidx.compose.material.icons.filled.Dashboard
import androidx.compose.material.icons.filled.FactCheck
import androidx.compose.material.icons.filled.Folder
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument

import com.example.testpilot.data.local.TestPilotDatabase
import com.example.testpilot.ui.screens.AnalyticsScreen
import com.example.testpilot.ui.screens.DashboardScreen
import com.example.testpilot.ui.screens.DefectDetailsScreen
import com.example.testpilot.ui.screens.DefectsScreen
import com.example.testpilot.ui.screens.ProjectAnalyticsScreen
import com.example.testpilot.ui.screens.ProjectsScreen
import com.example.testpilot.ui.screens.TestCasesScreen
import com.example.testpilot.ui.screens.TestExecutionScreen
import com.example.testpilot.ui.screens.TestProjectsScreen
import com.example.testpilot.ui.screens.TestStepsScreen
import com.example.testpilot.ui.screens.TestSuitesScreen

import com.example.testpilot.ui.screens.PlaceholderScreen

import com.example.testpilot.ui.theme.NavyBackground
import com.example.testpilot.ui.theme.NavySurface
import com.example.testpilot.ui.theme.PurplePrimary
import com.example.testpilot.ui.theme.TextSecondary

private const val TEST_STEPS_ROUTE = "test_steps/{testCaseId}"

private fun createTestStepsRoute(testCaseId: Int): String {
    return "test_steps/$testCaseId"
}

private const val TEST_EXECUTION_ROUTE =
    "test_execution/{testCaseId}"

private fun createTestExecutionRoute(testCaseId: Int): String {
    return "test_execution/$testCaseId"
}

private data class BottomNavItem(
    val screen: Screen,
    val icon: ImageVector
)

@Composable
fun TestPilotApp() {

    val navController = rememberNavController()

    val items = listOf(
        BottomNavItem(Screen.Dashboard, Icons.Default.Dashboard),
        BottomNavItem(Screen.Projects, Icons.Default.Folder),
        BottomNavItem(Screen.TestCases, Icons.Default.FactCheck),
        BottomNavItem(Screen.Defects, Icons.Default.BugReport),
        BottomNavItem(Screen.Analytics, Icons.Default.Analytics)
    )

    val backStackEntry by
    navController.currentBackStackEntryAsState()

    val currentRoute =
        backStackEntry?.destination?.route

    Scaffold(
        containerColor = NavyBackground,

        bottomBar = {
            NavigationBar(
                containerColor = NavySurface
            ) {

                items.forEach { item ->

                    val selected =
                        currentRoute == item.screen.route ||
                                (
                                        item.screen == Screen.Analytics &&
                                                currentRoute?.startsWith(
                                                    "project_analytics/"
                                                ) == true
                                        ) ||
                                (
                                        item.screen == Screen.TestCases &&
                                                (
                                                        currentRoute?.startsWith(
                                                            "test_suites/"
                                                        ) == true ||
                                                                currentRoute?.startsWith(
                                                                    "suite_test_cases/"
                                                                ) == true ||
                                                                currentRoute?.startsWith(
                                                                    "test_steps/"
                                                                ) == true ||
                                                                currentRoute?.startsWith(
                                                                    "test_execution/"
                                                                ) == true
                                                        )
                                        )

                    NavigationBarItem(
                        selected = selected,

                        onClick = {
                            navController.navigate(
                                item.screen.route
                            ) {
                                popUpTo(
                                    navController.graph
                                        .findStartDestination().id
                                ) {
                                    saveState = true
                                }

                                launchSingleTop = true
                                restoreState = true
                            }
                        },

                        icon = {
                            Icon(
                                imageVector = item.icon,
                                contentDescription = item.screen.title
                            )
                        },

                        label = {
                            Text(item.screen.title)
                        },

                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = PurplePrimary,
                            selectedTextColor = PurplePrimary,
                            unselectedIconColor = TextSecondary,
                            unselectedTextColor = TextSecondary,
                            indicatorColor = PurplePrimary.copy(alpha = 0.15f)
                        )
                    )
                }
            }
        }
    ) { innerPadding ->

        NavHost(
            navController = navController,
            startDestination = Screen.Dashboard.route,
            modifier = Modifier.padding(innerPadding)
        ) {

            // =========================================
            // DASHBOARD
            // =========================================

            composable(Screen.Dashboard.route) {

                DashboardScreen(
                    onNavigate = { route ->
                        navController.navigate(route)
                    }
                )
            }

            // =========================================
            // PROJECTS
            // =========================================

            composable(Screen.Projects.route) {

                ProjectsScreen()
            }

            // =========================================
            // TESTING WORKSPACE - PROJECT SELECTION
            // =========================================

            composable(Screen.TestCases.route) {

                TestProjectsScreen(
                    onOpenProject = { projectId ->

                        navController.navigate(
                            Screen.TestSuites.createRoute(projectId)
                        )
                    }
                )
            }

            // =========================================
            // TEST SUITES
            // =========================================

            composable(
                route = Screen.TestSuites.route,
                arguments = listOf(
                    navArgument("projectId") {
                        type = NavType.IntType
                    }
                )
            ) { backStackEntry ->

                val projectId =
                    backStackEntry.arguments?.getInt("projectId") ?: 0

                TestSuitesScreen(
                    projectId = projectId,

                    onBack = {
                        navController.popBackStack()
                    },

                    onOpenSuite = { suiteId ->

                        navController.navigate(
                            Screen.TestCasesInSuite.createRoute(suiteId)
                        )
                    }
                )
            }

            // =========================================
            // TEST CASES INSIDE A SUITE
            // =========================================

            composable(
                route = Screen.TestCasesInSuite.route,
                arguments = listOf(
                    navArgument("suiteId") {
                        type = NavType.IntType
                    }
                )
            ) { backStackEntry ->

                val suiteId =
                    backStackEntry.arguments?.getInt("suiteId") ?: 0

                TestCasesScreen(
                    suiteId = suiteId,

                    onBack = {
                        navController.popBackStack()
                    },

                    onOpenTestCaseSteps = { testCaseId ->

                        navController.navigate(
                            createTestStepsRoute(testCaseId)
                        )
                    }
                )
            }

            // =========================================
            // TEST STEPS
            // =========================================

            composable(
                route = TEST_STEPS_ROUTE,
                arguments = listOf(
                    navArgument("testCaseId") {
                        type = NavType.IntType
                    }
                )
            ) { backStackEntry ->

                val testCaseId =
                    backStackEntry.arguments?.getInt("testCaseId") ?: 0

                TestStepsScreen(
                    testCaseId = testCaseId,

                    onBack = {
                        navController.popBackStack()
                    },

                    onStartExecution = {

                        navController.navigate(
                            createTestExecutionRoute(testCaseId)
                        )
                    }
                )
            }

            // =========================================
            // TEST EXECUTION
            // =========================================

            composable(
                route = TEST_EXECUTION_ROUTE,
                arguments = listOf(
                    navArgument("testCaseId") {
                        type = NavType.IntType
                    }
                )
            ) { backStackEntry ->

                val testCaseId =
                    backStackEntry.arguments?.getInt("testCaseId") ?: 0

                TestExecutionScreen(
                    testCaseId = testCaseId,

                    onBack = {
                        navController.popBackStack()
                    }
                )
            }

            // =========================================
            // DEFECTS
            // =========================================

            composable(Screen.Defects.route) {

                DefectsScreen(
                    onOpenDefect = { defectId ->

                        navController.navigate(
                            Screen.DefectDetails.createRoute(defectId)
                        )
                    }
                )
            }

            // =========================================
            // DEFECT DETAILS
            // =========================================

            composable(
                route = Screen.DefectDetails.route,
                arguments = listOf(
                    navArgument("defectId") {
                        type = NavType.IntType
                    }
                )
            ) { backStackEntry ->

                val defectId =
                    backStackEntry.arguments?.getInt("defectId") ?: 0

                DefectDetailsScreen(
                    defectId = defectId,

                    onBack = {
                        navController.popBackStack()
                    }
                )
            }

            // =========================================
            // ANALYTICS - PROJECT SELECTION
            // =========================================

            composable(Screen.Analytics.route) {

                val context = LocalContext.current

                val database = remember(context) {
                    TestPilotDatabase.getDatabase(context)
                }

                val projects by database
                    .projectDao()
                    .getAllProjects()
                    .collectAsState(initial = emptyList())

                AnalyticsScreen(
                    projects = projects,

                    onOpenProject = { projectId ->

                        navController.navigate(
                            Screen.ProjectAnalytics.createRoute(projectId)
                        )
                    }
                )
            }

            // =========================================
            // INDIVIDUAL PROJECT ANALYTICS
            // =========================================

            composable(
                route = Screen.ProjectAnalytics.route,
                arguments = listOf(
                    navArgument("projectId") {
                        type = NavType.IntType
                    }
                )
            ) { backStackEntry ->

                val projectId =
                    backStackEntry.arguments?.getInt("projectId") ?: 0

                ProjectAnalyticsScreen(
                    projectId = projectId,

                    onBack = {
                        navController.popBackStack()
                    }
                )
            }
        }
    }
}
