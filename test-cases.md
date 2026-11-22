# TestPilot – Software Test Cases

## 1. Document Information

| Field | Details |
|---|---|
| Project | TestPilot |
| Document | Software Test Case Specification |
| Application Type | Android – Manual QA Management |
| Testing Type | Functional, Negative, Validation, Integration and Persistence Testing |
| Testing Approach | Manual Testing |
| Version | 1.0 |
| Document Status | Test Case Specification |

## 2. Introduction

This document contains the test cases designed to verify the functionality, reliability and usability of TestPilot.

The test cases cover the application's major modules, including project management, test suite management, test case management, test execution, defect management, dashboard analytics and PDF report generation.

The document includes positive, negative, validation and boundary test scenarios.

**Note:** These are documented test specifications. Individual execution results should be updated only after the corresponding tests have actually been performed.

## 3. Testing Scope

The following modules are included:

1. Project Management
2. Test Suite Management
3. Test Case Management
4. Test Step Management
5. Test Execution Management
6. Defect Management
7. Dashboard
8. Analytics and PDF Reporting
9. Data Persistence

## 4. Test Case Design

Each test case includes:

- Test Case ID
- Module
- Test Scenario
- Preconditions
- Test Steps
- Test Data
- Expected Result
- Test Type
- Priority
- Execution Status

### Execution Status Definitions

| Status | Description |
|---|---|
| Not Executed | Test has not yet been performed |
| Pass | Actual result matches expected result |
| Fail | Actual result differs from expected result |
| Blocked | Test cannot be completed due to a dependency or issue |

---

# 5. Project Management Test Cases

| Test ID | Test Scenario | Test Type | Priority | Expected Result |
|---|---|---|---|---|
| TC-PRO-001 | Create a project using valid details | Positive | High | Project is saved and displayed in the project list |
| TC-PRO-002 | Create multiple projects | Functional | Medium | Each project is stored and displayed independently |
| TC-PRO-003 | View project details | Functional | High | Correct project information is displayed |
| TC-PRO-004 | Update project information | Functional | High | Updated information is saved and displayed |
| TC-PRO-005 | Delete a project | Functional | Medium | Selected project is removed according to the application's deletion behaviour |
| TC-PRO-006 | Attempt to create a project with missing required information | Negative | High | Invalid submission is prevented or an appropriate validation message is displayed |
| TC-PRO-007 | Enter unusually long project information | Boundary | Medium | Input is handled without unexpected application errors |
| TC-PRO-008 | Verify project information after reopening the application | Persistence | High | Previously saved project information remains available |

# 6. Test Suite Management Test Cases

| Test ID | Test Scenario | Test Type | Priority | Expected Result |
|---|---|---|---|---|
| TC-SUI-001 | Create a test suite under an existing project | Positive | High | Test suite is successfully created |
| TC-SUI-002 | Create multiple test suites | Functional | Medium | Multiple suites are displayed correctly |
| TC-SUI-003 | View test suites belonging to a project | Functional | High | Only the relevant project's suites are displayed |
| TC-SUI-004 | Update test suite information | Functional | Medium | Updated information is saved |
| TC-SUI-005 | Delete a test suite | Functional | Medium | Selected suite is removed according to the application's deletion behaviour |
| TC-SUI-006 | Attempt to create a suite without selecting a project | Negative | High | Invalid submission is prevented or handled appropriately |
| TC-SUI-007 | Verify suite association after application restart | Persistence | High | Suite remains associated with the correct project |

# 7. Test Case Management Test Cases

| Test ID | Test Scenario | Test Type | Priority | Expected Result |
|---|---|---|---|---|
| TC-TC-001 | Create a test case with valid information | Positive | High | Test case is successfully saved |
| TC-TC-002 | Create multiple test cases in a suite | Functional | Medium | All test cases appear under the correct suite |
| TC-TC-003 | View test case details | Functional | High | Correct test case information is displayed |
| TC-TC-004 | Update test case information | Functional | Medium | Updated information is saved |
| TC-TC-005 | Delete a test case | Functional | Medium | Selected test case is removed according to the application's deletion behaviour |
| TC-TC-006 | Submit a test case with missing required information | Negative | High | Invalid submission is prevented or handled appropriately |
| TC-TC-007 | Verify test case priority and status | Functional | Medium | Selected priority and status are displayed correctly |
| TC-TC-008 | Verify test case data after application restart | Persistence | High | Test case information remains available |

# 8. Test Step Management Test Cases

| Test ID | Test Scenario | Test Type | Priority | Expected Result |
|---|---|---|---|---|
| TC-STEP-001 | Add a test step with an expected result | Positive | High | Test step is successfully saved |
| TC-STEP-002 | Add multiple steps to a test case | Functional | High | All steps appear in the correct test case |
| TC-STEP-003 | Update an existing test step | Functional | Medium | Updated step information is displayed |
| TC-STEP-004 | Delete a test step | Functional | Medium | Selected step is removed |
| TC-STEP-005 | Add a step with missing required information | Negative | Medium | Invalid input is prevented or handled appropriately |
| TC-STEP-006 | Verify the order of test steps | Functional | Medium | Steps are displayed in the intended order |
| TC-STEP-007 | Verify test steps after reopening the application | Persistence | High | Previously saved steps remain available |

# 9. Test Execution Management Test Cases

| Test ID | Test Scenario | Test Type | Priority | Expected Result |
|---|---|---|---|---|
| TC-EXE-001 | Execute a test case successfully | Positive | High | Execution is saved with the appropriate status |
| TC-EXE-002 | Mark a test step as Pass | Functional | High | Passed status is recorded |
| TC-EXE-003 | Mark a test step as Fail | Functional | High | Failed status is recorded |
| TC-EXE-004 | Mark a test step as Blocked | Functional | Medium | Blocked status is recorded |
| TC-EXE-005 | Leave a test step as Not Run | Functional | Medium | Not Run status is retained |
| TC-EXE-006 | Execute a test case containing failed steps | Functional | High | Overall execution status follows the application's execution rules |
| TC-EXE-007 | View previous executions | Functional | High | Execution history is displayed correctly |
| TC-EXE-008 | Execute the same test case multiple times | Functional | Medium | Separate execution records are maintained |
| TC-EXE-009 | Verify execution results after application restart | Persistence | High | Saved execution records remain available |
| TC-EXE-010 | Verify execution statistics after recording results | Integration | High | Dashboard statistics reflect the recorded executions |

# 10. Defect Management Test Cases

| Test ID | Test Scenario | Test Type | Priority | Expected Result |
|---|---|---|---|---|
| TC-DEF-001 | Create a defect with valid information | Positive | High | Defect is successfully saved |
| TC-DEF-002 | View the defect list | Functional | High | Saved defects are displayed correctly |
| TC-DEF-003 | View defect details | Functional | Medium | Correct defect information is displayed |
| TC-DEF-004 | Update defect information | Functional | Medium | Updated information is saved |
| TC-DEF-005 | Change defect status | Functional | High | Updated status is displayed correctly |
| TC-DEF-006 | Delete a defect | Functional | Medium | Selected defect is removed |
| TC-DEF-007 | Attempt to submit a defect with missing required information | Negative | High | Invalid submission is prevented or handled appropriately |
| TC-DEF-008 | Verify defect severity and priority | Functional | High | Selected severity and priority are displayed correctly |
| TC-DEF-009 | Verify defect information after application restart | Persistence | High | Saved defect information remains available |
| TC-DEF-010 | Verify defect statistics in analytics | Integration | High | Defect statistics reflect the stored defect records |

# 11. Dashboard Test Cases

| Test ID | Test Scenario | Test Type | Priority | Expected Result |
|---|---|---|---|---|
| TC-DASH-001 | Open the dashboard | Functional | High | Dashboard loads successfully |
| TC-DASH-002 | Verify total project count | Integration | High | Displayed count matches stored projects |
| TC-DASH-003 | Verify total test case count | Integration | High | Displayed count matches stored test cases |
| TC-DASH-004 | Verify passed execution count | Integration | High | Displayed count matches passed executions |
| TC-DASH-005 | Verify failed execution count | Integration | High | Displayed count matches failed executions |
| TC-DASH-006 | Verify blocked and in-progress execution counts | Integration | Medium | Counts match stored execution records |
| TC-DASH-007 | Verify test steps not run count | Integration | Medium | Count matches stored Not Run steps |
| TC-DASH-008 | Verify recent activity records | Functional | Medium | Latest activities are displayed in descending timestamp order |
| TC-DASH-009 | Open dashboard with an empty database | Boundary | Medium | Empty-state messages and zero statistics are displayed appropriately |
| TC-DASH-010 | Verify dashboard after application restart | Persistence | High | Dashboard reflects the persisted database records |

# 12. Analytics and PDF Reporting Test Cases

| Test ID | Test Scenario | Test Type | Priority | Expected Result |
|---|---|---|---|---|
| TC-ANA-001 | Open the analytics screen | Functional | High | Analytics screen loads successfully |
| TC-ANA-002 | View project testing statistics | Functional | High | Correct project statistics are displayed |
| TC-ANA-003 | Verify execution status distribution | Integration | High | Charts reflect the recorded execution data |
| TC-ANA-004 | Verify defect status distribution | Integration | High | Defect status chart reflects stored records |
| TC-ANA-005 | Verify defect severity distribution | Integration | High | Severity chart reflects stored records |
| TC-ANA-006 | Generate a project analytics PDF | Positive | High | PDF report is generated successfully |
| TC-ANA-007 | Open the generated PDF | Functional | High | PDF opens and its content is readable |
| TC-ANA-008 | Verify PDF statistics against application data | Integration | High | Reported values match the corresponding analytics data |
| TC-ANA-009 | Generate a report when limited data is available | Boundary | Medium | Report is generated or an appropriate empty-data state is handled |
| TC-ANA-010 | Verify report download behaviour | Functional | Medium | Report is saved to the selected destination |

# 13. Data Persistence Test Cases

| Test ID | Test Scenario | Test Type | Priority | Expected Result |
|---|---|---|---|---|
| TC-DATA-001 | Close and reopen the application | Persistence | High | Previously saved information remains available |
| TC-DATA-002 | Navigate between application modules | Integration | Medium | Data remains consistent across screens |
| TC-DATA-003 | Verify project and test suite relationships | Integration | High | Related records remain associated correctly |
| TC-DATA-004 | Verify test case and test step relationships | Integration | High | Steps remain associated with their correct test cases |
| TC-DATA-005 | Verify execution history after restarting the app | Persistence | High | Execution records remain available |
| TC-DATA-006 | Verify defect records after restarting the app | Persistence | High | Defect records remain available |

---

# 14. Conclusion

This test case specification provides a structured foundation for evaluating TestPilot's major functional modules.

It covers positive, negative, boundary, integration and persistence scenarios to support systematic manual testing.

The test cases can be used for future regression testing whenever application features or database behaviour are modified.

**TestPilot – Manual Software Testing & QA Management Application.**
