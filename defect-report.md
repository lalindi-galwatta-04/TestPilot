# TestPilot – Defect Report

## 1. Document Information

| Field | Details |
|---|---|
| Project | TestPilot |
| Document | Defect Report |
| Testing Type | Manual Testing |
| Application | TestPilot Android Application |
| Report Status | Sample Defect Record |

## 2. Introduction

This document records defects created and managed during the testing of TestPilot.

It provides a structured format for documenting defect details, reproduction steps, expected and actual results, severity, priority and status.

## 3. Defect Report

### DEF-001 – Login Button Does Not Navigate to Home Screen

| Field | Details |
|---|---|
| Defect ID | DEF-001 |
| Defect Title | Login button does not navigate to home screen |
| Related Project | Online Shopping App |
| Related Test Suite | Login Functionality |
| Related Test Case | Verify successful login |
| Severity | Major |
| Priority | High |
| Status | Open |
| Detection Method | Manual Test Execution |

### Description

After entering valid login credentials and tapping the Login button, the application remains on the login screen instead of navigating to the home screen.

### Preconditions

- The application is available.
- The login screen is accessible.
- Valid login credentials are available.

### Steps to Reproduce

1. Open the login screen.
2. Enter a valid email address.
3. Enter a valid password.
4. Tap the Login button.

### Expected Result

The user should successfully navigate to the application's home screen.

### Actual Result

The user remains on the login screen.

### Severity and Priority

**Severity: Major**

The simulated issue prevents successful completion of the login workflow.

**Priority: High**

The issue is considered important because login functionality is a core application feature.

### Related Test Execution

The defect was created following a deliberately failed test step during the TestPilot execution workflow.

**Important:** This is a simulated defect used to verify TestPilot's defect management functionality. It is not a confirmed defect in an independently tested ShopEasy application.

## 4. Defect Status Definitions

| Status | Description |
|---|---|
| Open | Defect has been reported and is awaiting action |
| In Progress | Defect is currently being investigated or fixed |
| Resolved | A correction has been implemented |
| Closed | Defect has been verified and closed |
| Reopened | Previously resolved defect has occurred again |

These definitions describe common defect lifecycle stages; available statuses may depend on the application's implementation.

## 5. Defect Severity Definitions

| Severity | Description |
|---|---|
| Critical | Causes a major system failure or makes essential functionality unusable |
| Major | Significantly affects important functionality |
| Medium | Affects functionality but has a workaround |
| Minor | Causes a relatively small functional or cosmetic issue |

## 6. Defect Priority Definitions

| Priority | Description |
|---|---|
| High | Requires urgent attention |
| Medium | Should be addressed within a reasonable timeframe |
| Low | Can be addressed when resources are available |

## 7. Conclusion

The defect reporting workflow was exercised during TestPilot's end-to-end testing session.

A sample defect was successfully created and its information was verified in the Defects section.

This document provides a structured reference for recording and managing defects in future testing activities.

**TestPilot – Manual Software Testing & QA Management Application.**
