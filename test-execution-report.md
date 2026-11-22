# TestPilot – Test Execution Report

## 1. Document Information

| Field | Details |
|---|---|
| Project | TestPilot |
| Document | Test Execution Report |
| Application | TestPilot Android Application |
| Testing Type | Manual End-to-End Testing |
| Testing Approach | Functional and Integration Testing |
| Test Environment | Android application running on a physical Android device |
| Report Status | E2E Testing Completed |

## 2. Testing Objective

The objective of this testing session was to verify the main end-to-end workflows of TestPilot and ensure that the application's major modules work together correctly.

Testing covered project management, test suite management, test case management, test execution, defect reporting, dashboard analytics and data persistence.

## 3. Test Environment

| Component | Details |
|---|---|
| Platform | Android |
| Application | TestPilot |
| Database | Room Database |
| Testing Method | Manual Testing |
| Device | Samsung Galaxy A15 |
| Network Requirement | Local/offline database operations |

## 4. Test Data

The following sample data was used during the E2E testing session.

| Field | Test Data |
|---|---|
| Project Name | Online Shopping App |
| Application Name | ShopEasy |
| Version | 1.0 |
| Test Suite | Login Functionality |
| Test Case | Verify successful login |
| Priority | High |

### Test Execution Steps

| Step | Action | Expected Result | Recorded Result |
|---|---|---|---|
| 1 | Enter valid email and password | Credentials are entered successfully | Pass |
| 2 | Tap the Login button | User navigates to the home screen | Fail |

**Overall Execution Status: Failed**

The second step was deliberately recorded as Failed to verify TestPilot's test execution and defect reporting workflow. This was a simulated test scenario, not a verified defect in a real ShopEasy application.

## 5. End-to-End Test Execution Summary

| Test ID | Test Scenario | Execution Outcome |
|---|---|---|
| E2E-001 | Project Creation | Completed |
| E2E-002 | Test Suite Creation | Completed |
| E2E-003 | Test Case Creation | Completed |
| E2E-004 | Test Step Creation | Completed |
| E2E-005 | Test Execution | Completed |
| E2E-006 | Defect Reporting | Completed |
| E2E-007 | Dashboard and Analytics Verification | Completed |
| E2E-008 | Data Persistence Verification | Passed |

**Total E2E Scenarios: 8**

All eight planned scenarios were completed. The individual outcomes are documented according to the observations recorded during the testing session.

## 6. Defect Reporting Verification

A sample defect was created to verify the defect management workflow.

| Field | Value |
|---|---|
| Defect Title | Login button does not navigate to home screen |
| Description | After entering valid credentials and tapping Login, the application remains on the login screen |
| Severity | Major |
| Priority | High |
| Status | Open |

The defect was successfully created and its information was verified in the Defects section.

**Note:** This was a simulated defect scenario used to test TestPilot's defect management functionality.

## 7. Dashboard and Analytics Verification

The dashboard and analytics modules were checked after creating the sample project, test case, execution and defect.

The verification covered:

- Project statistics
- Test case statistics
- Execution status statistics
- Recent activity records
- Defect statistics
- Analytics charts
- PDF report generation and opening

The dashboard and analytics verification scenario was completed.

## 8. Data Persistence Verification

The application was completely closed and reopened.

The following information was checked:

- Project information
- Test suite information
- Test case information
- Test steps
- Test execution history
- Defect records

**Result: PASS**

Previously saved records remained available after reopening the application.

## 9. Overall Testing Conclusion

The planned eight-scenario end-to-end testing cycle was completed.

The testing session exercised the major TestPilot workflows, including project creation, test management, execution recording, defect reporting, analytics and data persistence.

The session demonstrated the application's main MVP workflows. Additional individual functional, negative and boundary test cases should be executed as part of broader regression testing.

**TestPilot – Manual Software Testing & QA Management Application.**
