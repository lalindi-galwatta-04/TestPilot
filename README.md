# TestPilot – Manual Software Testing & QA Management

**TestPilot** is an Android application designed to simplify manual software testing and quality assurance management. It provides a structured environment for managing testing projects, organising test cases, recording test executions, tracking defects and analysing testing results.

Built with **Kotlin, Jetpack Compose and Room Database**, TestPilot follows the MVVM architecture and uses an offline-first approach to manage testing information locally.

## Features

### 1. Project Management
- Create and manage software testing projects.
- Maintain project descriptions and application information.
- Organise testing activities by project.

### 2. Test Suite Management
- Create test suites under individual projects.
- Organise related test cases into logical groups.

### 3. Test Case Management
- Create and manage test cases.
- Define descriptions, preconditions and priorities.
- Organise test scenarios by test suite.

### 4. Test Step Management
- Add individual steps to test cases.
- Define expected results for each step.
- Track the execution status of individual steps.

### 5. Test Execution Management
- Execute test cases and record results.
- Track execution statuses:
  - Passed
  - Failed
  - Blocked
  - In Progress
- Maintain test execution history.

### 6. Defect Management
- Report and manage software defects.
- Record defect details, severity and priority.
- Track defect statuses.

### 7. Dashboard & Analytics
- View an overview of testing projects and test cases.
- Monitor test execution statistics.
- Track passed, failed, blocked and in-progress executions.
- Monitor test steps awaiting execution.
- View recent project and test execution activities.

### 8. PDF Analytics Reports
- Generate downloadable project analytics reports.
- Visualise testing statistics through charts.
- Review defect status and severity distributions.
- Export analytics for documentation and reporting.

## Technology Stack

| Technology | Purpose |
|---|---|
| Kotlin | Primary programming language |
| Jetpack Compose | Modern Android UI development |
| Material 3 | UI components and styling |
| Room Database | Local database management |
| Kotlin Coroutines & Flow | Asynchronous operations and reactive data |
| MVVM | Application architecture |
| Android Studio | Development environment |
| Git & GitHub | Version control |

## Architecture

TestPilot follows the **Model–View–ViewModel (MVVM)** architectural pattern, with a repository layer to separate data access from application logic.

The main architectural components include:

- **UI Layer:** Jetpack Compose screens and reusable UI components.
- **ViewModel Layer:** Manages UI-related data and application state.
- **Repository Layer:** Coordinates data operations.
- **Data Layer:** Room entities, DAOs and database management.

### Architecture Overview

```text
             UI Layer
       Jetpack Compose Screens
                |
                v
          ViewModel Layer
       State Management & Logic
                |
                v
          Repository Layer
          Data Operations
                |
                v
           Data Layer
        Room Database
        Entities & DAOs
```

## Database

TestPilot uses **Room Database** for local, offline-first data persistence.

The database manages the following core data:

- Projects
- Test Suites
- Test Cases
- Test Steps
- Test Executions
- Test Execution Steps
- Defects

Database migrations are implemented to support database schema updates while preserving existing records.

## Testing

A manual end-to-end testing cycle was performed to verify the application's main workflows.

The testing scenarios covered:

1. Project creation
2. Test suite creation
3. Test case creation
4. Test step creation
5. Test execution
6. Defect reporting
7. Dashboard and analytics verification
8. Data persistence

The end-to-end testing cycle also included verification of analytics PDF generation.

## Getting Started

### Prerequisites

- Android Studio
- Android SDK
- JDK compatible with the project's Gradle configuration
- Android emulator or physical Android device

### Installation

1. Clone the repository:

```bash
git clone https://github.com/lalindi-galwatta-04/TestPilot.git
```

2. Open the cloned project in Android Studio.

3. Allow Gradle to sync the project and download the required dependencies.

4. Select an Android emulator or connect a physical Android device.

5. Run the application using Android Studio.

## Project Information

| Property | Details |
|---|---|
| Project Name | TestPilot |
| Platform | Android |
| Application Type | Manual QA & Testing Management |
| Architecture | MVVM |
| Database | Room |
| Development Language | Kotlin |
| UI Framework | Jetpack Compose |
| Project Stage | MVP |

## Future Enhancements

Potential future improvements include:

- Complete activity history with advanced filtering.
- Enhanced analytics and reporting.
- Additional test execution insights.
- Improved defect tracking and filtering.
- Further UI and usability enhancements.

## Author

**Lalindi Galwatta**

Developed as an Android development and software testing portfolio project.

## License

No open-source license has been assigned to this project yet. All rights are reserved by the author unless a license is added to this repository.

## Testing Documentation

The testing documentation is available in the
[`testing/`](testing/) directory.

- [Software Test Cases](testing/test-cases.md)
- [Test Execution Report](testing/test-execution-report.md)
- [Defect Report](testing/defect-report.md)
- [Test Summary Report](testing/test-summary.md)
