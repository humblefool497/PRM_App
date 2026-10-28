# OpsManager — Manager's Operations Dashboard

[![Android CI](https://github.com/aistudio/ops-manager/actions/workflows/android.yml/badge.svg)](https://github.com/aistudio/ops-manager/actions/workflows/android.yml)
![Platform](https://img.shields.io/badge/Platform-Android%2014%2B%20(API%2024--36)-3DDC84?logo=android&logoColor=white)
![Kotlin](https://img.shields.io/badge/Kotlin-2.2.10-7F52FF?logo=kotlin&logoColor=white)
![Compose](https://img.shields.io/badge/UI-Jetpack%20Compose%20M3-4285F4?logo=jetpackcompose&logoColor=white)
![Database](https://img.shields.io/badge/Storage-Room%202.7-F58220?logo=sqlite&logoColor=white)
![Build](https://img.shields.io/badge/Gradle-9.3.1-02303A?logo=gradle&logoColor=white)

An enterprise-grade, offline-first Android application designed for engineering, delivery, and portfolio managers. **OpsManager** consolidates daily project operations, timesheet validation, compliance matrices, client governance escalations, and multi-million dollar Statement of Work (SOW) financial projections into a glanceable, responsive executive workspace.

---

## Key Highlights & Enterprise Architecture

- **Clean Architecture & MVVM:** Strict separation across UI (`Jetpack Compose`), State Management (`ViewModel` with `StateFlow` and `combine`), Domain/Repository (`OpsRepository`), and Data Layer (`Room Database`).
- **Reactive Data Streams:** Unidirectional data flow (UDF) powered by Kotlin Coroutines and reactive `Flow` queries.
- **Enterprise Design System:** Built with Material 3 (M3) utilizing a deep charcoal (`#0B0F17`, `#141C27`), corporate slate (`#94A3B8`), and teal accent (`#14B8A6`, `#2DD4BF`) palette. Visual status indicators highlight critical escalations and overdue non-compliance in high-visibility crimson (`#EF4444`).
- **Full CRUD Capabilities:** Complete Create, Read, Update, and Delete capabilities for Team Associates, SOWs, Amendments, Milestones, and Operational Expenses, backed by relational cascade integrity.
- **Automated Reporting & Export Engine:** Instant one-click generation of Markdown executive summaries and CSV datasets with native Android Intent sharing.

---

## Core Modules

### 1. Project Timesheet Tracking (PTS) & Validation
- **Associate Roster:** Track direct-report engineering and delivery talent across client accounts and project assignments.
- **Three-State Validation Engine:**
  - `Pending Submission`: Associate has not logged or submitted daily hours.
  - `Submitted - Awaiting Review`: Submitted by associate, ready for manager sign-off.
  - `Assigned by Client Manager`: Pre-approved or locked directly by client side leads.
- **Bulk Operations & Date Navigation:** Date picker support with a one-tap action to bulk-verify and submit pending entries.
- **PTS Export:** Generates clean, copyable daily timesheet reports with prominent flags for missing/defaulter submissions (`ReportExportUtils`).

### 2. Compliance & Defaulter Tracker
- **Three-Stream Defaulter Matrix:**
  1. *Mandatory Internal Trainings* (Course Name, Due Date, Certification Status).
  2. *Corporate Portal Declarations* (Quarterly Ethics & IT Security Attestations).
  3. *Client Portal Declarations* (Client Account Access & Verification Gates).
- **Visual Alerting:** Highlights non-compliant overdue records with crimson card borders and badges.
- **Fast Status Toggling:** 1-tap checkbox reconciliation for manager audits.

### 3. Escalations, Governance & Action Items
- **Governance Log:** Centralized repository for client escalations, Steering Committee calls, and periodic stakeholder reviews with meeting minutes.
- **Smart Action Items:** Tracks remediation tasks by status (`Open`, `In Progress`, `Resolved`) and priority (`High`, `Medium`, `Low`).
- **Associate Delegation:** Link and delegate action items directly to direct reports from the team database.
- **Governance Audit Summaries:** Filterable summary reports by assignee and status for client distribution.

### 4. Project Financials & Portfolio Management
- **Statement of Work (SOW) Lifecycle:** Track contract numbers, base values, start/end dates, and contract health (`Active`, `Extended`, `Pending Signature`, `Closed`).
- **Contract Amendments:** Log signed scope expansions and amendments with financial impact amounts and contract month extensions.
- **Milestones Matrix:** Manage deliverable milestones, target dates, quarter assignments (`Q4 2026`, `Q1 2027`), billing trigger gates (e.g., *Client UAT Sign-off*, *Go-Live Acceptance*), and 1-tap status cycling.
- **Operational Expense Log:** Record project expenses categorized across 4 enterprise streams: *Cloud Infrastructure*, *Software Licenses*, *Corporate Travel*, and *Miscellaneous*.
- **Quarterly Projections Dashboard:** Analytical financial dashboard comparing:
  - Total quarterly earnings achieved vs. projected milestone revenue.
  - Total operational expenditures vs. net operating margins and margin percentages.
  - Next-quarter pipeline forecasts based on active SOW structures.

---

## Full CRUD Operations Summary

| Entity | Create (Add) | Read (View/Filter) | Update (Edit) | Delete |
|---|---|---|---|---|
| **Team Associates** | FAB / Dialog with role, project & client | Tabular Roster with EMP-ID & allocations | Full dialog (name, role, project, client) | Confirmed delete + timesheet/compliance cleanup |
| **SOWs** | FAB / Header "+ SOW" dialog | Active portfolio cards with amendment sums | Edit SOW dialog (value, client, dates, status) | Cascading delete (removes linked milestones, expenses) |
| **Amendments** | "+ Amendment" button on SOW | Expandable accordion per SOW | Edit dialog (scope, financial impact, months) | Confirmed delete with contract recalculation |
| **Milestones** | FAB "+ Milestone" dialog | Grouped by quarter with billing triggers | Edit dialog (amount, date, quarter, trigger) | Confirmed delete with margin recalculation |
| **Expenses** | FAB "+ Expense" dialog | Categorized list with invoice references | Edit dialog (category, amount, invoice ref) | Confirmed delete with margin recalculation |
| **Action Items** | FAB "+ Action Item" dialog | Filterable list with priority badges | Status cycle + delegation | Confirmed delete |

---

## Technology Stack

| Layer | Technologies |
|---|---|
| **Language** | Kotlin 2.2.10 |
| **UI Framework** | Jetpack Compose (BOM 2024.09.00) + Material Design 3 (M3) |
| **Architecture** | MVVM, Clean Architecture, StateFlow, Coroutines |
| **Database** | Room Database 2.7.0 (with SQLite & KSP code generation) |
| **Networking & Serialization**| Moshi 1.15.2, Retrofit 2.12.0, OkHttp 4.10.0 |
| **Build Tooling** | Gradle 9.3.1, Android Gradle Plugin (AGP) 9.1.1, Java 21 |
| **Secrets & Keys** | Secrets Gradle Plugin 2.0.1 (via `.env` / `.env.example`) |
| **Testing** | JUnit 4.13.2, Robolectric 4.16.1, Compose UI Test Manifest |
| **CI/CD** | GitHub Actions (`.github/workflows/android.yml`) |

---

## Directory Structure

```
├── .github/
│   └── workflows/
│       └── android.yml                # Automated CI pipeline (build, test, lint, APK upload)
├── app/
│   ├── build.gradle.kts               # App module build configuration
│   ├── proguard-rules.pro             # ProGuard optimization rules
│   └── src/
│       ├── main/
│       │   ├── AndroidManifest.xml    # App manifest & launcher configuration
│       │   ├── java/com/example/
│       │   │   ├── MainActivity.kt    # Root Compose activity with Navigation Scaffold
│       │   │   ├── data/
│       │   │   │   ├── export/
│       │   │   │   │   └── ReportExportUtils.kt   # Markdown & CSV reporting engines
│       │   │   │   ├── local/
│       │   │   │   │   ├── AppDatabase.kt         # Room DB builder & initial data seeding
│       │   │   │   │   ├── dao/
│       │   │   │   │   │   └── OpsDao.kt          # Room DAOs with reactive Flows & cascades
│       │   │   │   │   └── entity/
│       │   │   │   │       └── OpsEntities.kt     # Room entities (Associate, SOW, Milestone...)
│       │   │   │   └── repository/
│       │   │   │       └── OpsRepository.kt       # Repository abstraction for DAO operations
│       │   │   └── ui/
│       │   │       ├── OpsViewModel.kt            # Core ViewModel, aggregated dashboard metrics
│       │   │       ├── components/
│       │   │       │   └── CommonUi.kt            # Reusable M3 badges, cards & report modal
│       │   │       ├── navigation/
│       │   │       │   └── NavigationItem.kt      # Bottom navigation routes & icons
│       │   │       ├── screens/
│       │   │       │   ├── DashboardScreen.kt     # Executive overview & KPIs
│       │   │       │   ├── TeamComplianceScreen.kt# PTS validation & Defaulter matrix (CRUD)
│       │   │       │   ├── GovernanceScreen.kt    # Escalations & Action item delegation
│       │   │       │   └── FinancialsScreen.kt    # SOWs, Milestones, Expenses & Projections (CRUD)
│       │   │       └── theme/
│       │   │           ├── Color.kt               # Charcoal, slate & teal design tokens
│       │   │           ├── Theme.kt               # Dynamic MaterialTheme wrapper
│       │   │           └── Type.kt                # Typography scales
│       │   └── res/                               # Vector assets, launcher icons, strings
│       └── test/java/com/example/
│           ├── ExampleUnitTest.kt                 # JVM Unit tests
│           └── ExampleRobolectricTest.kt          # Robolectric tests for reports & string contexts
├── gradle/
│   ├── libs.versions.toml             # Version Catalog dependencies
│   └── wrapper/
│       └── gradle-wrapper.properties  # Gradle 9.3.1 distribution
├── build.gradle.kts                   # Root build script
├── gradlew                            # Gradle wrapper executable script (POSIX 755)
├── gradlew.bat                        # Gradle wrapper for Windows
├── metadata.json                      # AI Studio platform identification
└── README.md                          # Project documentation
```

---

## Getting Started & Build Commands

### Prerequisites
- **JDK 21** (Eclipse Temurin recommended)
- **Android SDK** (API Level 36 / Android 16 preview compatible, Min SDK 24)

### Building Locally

```bash
# Clone the repository
git clone <repository-url>
cd ops-manager

# Verify executable permissions on gradlew
chmod +x gradlew

# Run Unit & Robolectric Tests
./gradlew testDebugUnitTest

# Run Code Quality & Lint Analysis
./gradlew lintDebug

# Compile Debug APK
./gradlew assembleDebug
```
*The compiled APK will be located at: `app/build/outputs/apk/debug/app-debug.apk`.*

---

## Continuous Integration (GitHub Actions)

The repository includes a ready-to-use GitHub Actions workflow (`.github/workflows/android.yml`):
- **Triggers:** On every push and pull request to `main` and `master`, plus manual `workflow_dispatch`.
- **Automated Steps:**
  1. Sets up JDK 21 and caches Gradle dependencies.
  2. Auto-initializes `.env` and decodes `debug.keystore` from base64 representation.
  3. Executes `./gradlew lintDebug` and uploads HTML/XML lint reports.
  4. Runs Robolectric and JUnit test suites (`./gradlew testDebugUnitTest`).
  5. Assembles debug APK (`./gradlew assembleDebug`) and stores the artifact for 14 days.

---

## License & Enterprise Compliance

Developed for internal enterprise management and operations tracking. Built with clean zero-trust principles and local-first data privacy.
