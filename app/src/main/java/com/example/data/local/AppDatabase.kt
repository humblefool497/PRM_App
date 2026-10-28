package com.example.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import com.example.data.local.dao.OpsDao
import com.example.data.local.entity.ActionItemEntity
import com.example.data.local.entity.ActionItemStatus
import com.example.data.local.entity.AssociateEntity
import com.example.data.local.entity.ComplianceEntity
import com.example.data.local.entity.ComplianceStream
import com.example.data.local.entity.ExpenseCategory
import com.example.data.local.entity.ExpenseEntity
import com.example.data.local.entity.GovernanceCallEntity
import com.example.data.local.entity.MilestoneEntity
import com.example.data.local.entity.PTSStatus
import com.example.data.local.entity.SowAmendmentEntity
import com.example.data.local.entity.SowEntity
import com.example.data.local.entity.TimesheetEntity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Database(
    entities = [
        AssociateEntity::class,
        TimesheetEntity::class,
        ComplianceEntity::class,
        GovernanceCallEntity::class,
        ActionItemEntity::class,
        SowEntity::class,
        SowAmendmentEntity::class,
        MilestoneEntity::class,
        ExpenseEntity::class
    ],
    version = 1,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {

    abstract fun opsDao(): OpsDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context, coroutineScope: CoroutineScope): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "ops_manager.db"
                )
                    .addCallback(DatabaseCallback(coroutineScope))
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }

    private class DatabaseCallback(
        private val scope: CoroutineScope
    ) : RoomDatabase.Callback() {
        override fun onCreate(db: SupportSQLiteDatabase) {
            super.onCreate(db)
            INSTANCE?.let { database ->
                scope.launch(Dispatchers.IO) {
                    populateInitialData(database.opsDao())
                }
            }
        }

        private suspend fun populateInitialData(dao: OpsDao) {
            // Associates
            val associates = listOf(
                AssociateEntity(1, "EMP-1042", "Sarah Chen", "sarah.chen@enterprise.com", "Senior Android Architect", "Titan Mobile Banking", "Global Retail Bank"),
                AssociateEntity(2, "EMP-1088", "Marcus Vance", "marcus.vance@enterprise.com", "Principal Backend Lead", "Titan Mobile Banking", "Global Retail Bank"),
                AssociateEntity(3, "EMP-1105", "Priya Sharma", "priya.sharma@enterprise.com", "Lead Cloud DevOps", "Core Cloud Platform", "FinTech International"),
                AssociateEntity(4, "EMP-1142", "Alex Rivera", "alex.rivera@enterprise.com", "Senior Full-Stack Dev", "Titan Mobile Banking", "Global Retail Bank"),
                AssociateEntity(5, "EMP-1190", "Elena Rostova", "elena.rostova@enterprise.com", "QA Automation Specialist", "Titan Mobile Banking", "Global Retail Bank"),
                AssociateEntity(6, "EMP-1215", "David Kim", "david.kim@enterprise.com", "Senior Data Engineer", "Real-time Fraud Engine", "Global Retail Bank"),
                AssociateEntity(7, "EMP-1240", "Jordan Taylor", "jordan.taylor@enterprise.com", "Staff UX Designer", "Titan Mobile Banking", "Global Retail Bank"),
                AssociateEntity(8, "EMP-1288", "Aisha Morales", "aisha.morales@enterprise.com", "AppSec Solutions Engineer", "Titan Mobile Banking", "Global Retail Bank")
            )
            dao.insertAssociates(associates)

            // Today's PTS Timesheets (2026-10-28)
            val today = "2026-10-28"
            val timesheets = listOf(
                TimesheetEntity(1, 1, "Sarah Chen", "EMP-1042", today, PTSStatus.SUBMITTED, 8.0, "Completed Biometric Authentication flow & Compose testing"),
                TimesheetEntity(2, 2, "Marcus Vance", "EMP-1088", today, PTSStatus.CLIENT_ASSIGNED, 8.0, "Client Manager pre-approved high priority gateway sync"),
                TimesheetEntity(3, 3, "Priya Sharma", "EMP-1105", today, PTSStatus.SUBMITTED, 8.0, "AWS EKS cluster upgrade and pipeline automation"),
                TimesheetEntity(4, 4, "Alex Rivera", "EMP-1142", today, PTSStatus.PENDING, 0.0, ""),
                TimesheetEntity(5, 5, "Elena Rostova", "EMP-1190", today, PTSStatus.SUBMITTED, 8.0, "Regression test suite run on Sprint 24 build"),
                TimesheetEntity(6, 6, "David Kim", "EMP-1215", today, PTSStatus.PENDING, 0.0, ""),
                TimesheetEntity(7, 7, "Jordan Taylor", "EMP-1240", today, PTSStatus.CLIENT_ASSIGNED, 8.0, "Design sprint client workshop locked hours"),
                TimesheetEntity(8, 8, "Aisha Morales", "EMP-1288", today, PTSStatus.PENDING, 0.0, "")
            )
            dao.insertTimesheets(timesheets)

            // Defaulters & Compliance across 3 streams
            val complianceList = listOf(
                ComplianceEntity(1, 4, "Alex Rivera", "EMP-1142", ComplianceStream.INTERNAL_TRAINING, "Mandatory Cyber Resilience & Phishing Defense 2026", "2026-10-20", "Annual Mandate", false, null, "Overdue by 8 days"),
                ComplianceEntity(2, 6, "David Kim", "EMP-1215", ComplianceStream.INTERNAL_TRAINING, "Global Anti-Money Laundering (AML) Compliance", "2026-10-25", "Banking Regulatory", false, null, "Overdue by 3 days"),
                ComplianceEntity(3, 8, "Aisha Morales", "EMP-1288", ComplianceStream.INTERNAL_TRAINING, "Data Privacy & GDPR Standards 2026", "2026-10-22", "Security Cert", false, null, "Overdue by 6 days"),
                ComplianceEntity(4, 1, "Sarah Chen", "EMP-1042", ComplianceStream.INTERNAL_TRAINING, "Secure Code Architecture & OWASP Top 10", "2026-11-15", "Engineering Standards", true, "2026-10-14", "Certified"),

                ComplianceEntity(5, 2, "Marcus Vance", "EMP-1088", ComplianceStream.CORPORATE_DECLARATION, "Code of Business Conduct & Ethics", "2026-10-15", "Q4 Mandatory", false, null, "Overdue by 13 days"),
                ComplianceEntity(6, 5, "Elena Rostova", "EMP-1190", ComplianceStream.CORPORATE_DECLARATION, "IT Equipment & Data Security Attestation", "2026-10-24", "Quarterly Declaration", false, null, "Overdue by 4 days"),
                ComplianceEntity(7, 3, "Priya Sharma", "EMP-1105", ComplianceStream.CORPORATE_DECLARATION, "Annual Conflict of Interest Declaration", "2026-11-30", "Corporate Compliance", true, "2026-10-10", "Acknowledged"),

                ComplianceEntity(8, 4, "Alex Rivera", "EMP-1142", ComplianceStream.CLIENT_PORTAL, "Client VPN & Access Token Verification", "2026-10-26", "Client Account: Global Retail Bank", false, null, "Pending client verification"),
                ComplianceEntity(9, 6, "David Kim", "EMP-1215", ComplianceStream.CLIENT_PORTAL, "Third-Party Vendor Confidentiality Acknowledgment", "2026-10-18", "Client Account: Global Retail Bank", false, null, "Sign-off pending"),
                ComplianceEntity(10, 3, "Priya Sharma", "EMP-1105", ComplianceStream.CLIENT_PORTAL, "Production Access Governance Form", "2026-11-10", "Client Account: FinTech Intl", true, "2026-10-12", "Verified by Client Lead")
            )
            dao.insertComplianceList(complianceList)

            // Governance Calls & Complaints
            val governanceCalls = listOf(
                GovernanceCallEntity(
                    id = 1,
                    title = "Sprint 24 Executive SteerCo & Escalation",
                    date = "2026-10-26",
                    clientStakeholder = "David Ross (VP Technology, Global Retail Bank)",
                    callType = "Steering Committee",
                    severity = "High",
                    meetingMinutes = "Reviewed Sprint 24 deliverables and latency spikes. Client escalated transaction timeout issues on gateway during peak morning load. Demanded Root Cause Analysis within 72 hours and accelerated mobile UAT certification."
                ),
                GovernanceCallEntity(
                    id = 2,
                    title = "Enterprise Security Audit Post-Mortem",
                    date = "2026-10-22",
                    clientStakeholder = "Jennifer Beale (Chief Information Security Officer)",
                    callType = "Client Escalation",
                    severity = "Critical",
                    meetingMinutes = "Detailed audit on dependency CVE reports. Agreed on immediate code audit, rotation of API gateway client certs, and tighter token lifetimes."
                ),
                GovernanceCallEntity(
                    id = 3,
                    title = "Quarterly Financial & Scope Review",
                    date = "2026-10-14",
                    clientStakeholder = "Michael Vance (Director Procurement)",
                    callType = "Monthly Review",
                    severity = "Medium",
                    meetingMinutes = "Finalized signed Amendment AMD-02 for Scope Expansion on Fraud Analytics pipeline ($95,000 addition). Discussed initial terms for Q1 2027 extension."
                )
            )
            dao.insertGovernanceCalls(governanceCalls)

            // Action Items
            val actionItems = listOf(
                ActionItemEntity(
                    id = 1,
                    governanceCallId = 1,
                    governanceTitle = "Sprint 24 Executive SteerCo & Escalation",
                    title = "Deliver Root Cause Analysis (RCA) on Transaction Gateway timeout",
                    description = "Analyze database connection pooling and API gateway thread starvation under 10k RPS load.",
                    assignedAssociateId = 2,
                    assignedAssociateName = "Marcus Vance",
                    dueDate = "2026-10-29",
                    priority = "High",
                    status = ActionItemStatus.OPEN
                ),
                ActionItemEntity(
                    id = 2,
                    governanceCallId = 2,
                    governanceTitle = "Enterprise Security Audit Post-Mortem",
                    title = "Submit Security Remediation Audit Report for CISO office",
                    description = "Validate all transitive dependencies, remediate CVEs, and produce compliance matrix.",
                    assignedAssociateId = 8,
                    assignedAssociateName = "Aisha Morales",
                    dueDate = "2026-10-30",
                    priority = "High",
                    status = ActionItemStatus.IN_PROGRESS
                ),
                ActionItemEntity(
                    id = 3,
                    governanceCallId = 1,
                    governanceTitle = "Sprint 24 Executive SteerCo & Escalation",
                    title = "Accelerate UAT Sign-off for Sprint 24 Mobile Release",
                    description = "Coordinate with client testing team to unblock biometric verification test scripts.",
                    assignedAssociateId = 1,
                    assignedAssociateName = "Sarah Chen",
                    dueDate = "2026-11-02",
                    priority = "Medium",
                    status = ActionItemStatus.IN_PROGRESS
                ),
                ActionItemEntity(
                    id = 4,
                    governanceCallId = 2,
                    governanceTitle = "Enterprise Security Audit Post-Mortem",
                    title = "Rotate API Gateway Client Certificates across staging and prod",
                    description = "Generate new 4096-bit mTLS certificates and schedule zero-downtime rollover.",
                    assignedAssociateId = 3,
                    assignedAssociateName = "Priya Sharma",
                    dueDate = "2026-10-31",
                    priority = "High",
                    status = ActionItemStatus.OPEN
                ),
                ActionItemEntity(
                    id = 5,
                    governanceCallId = null,
                    governanceTitle = "Operations Internal Audit",
                    title = "Conduct Defaulter compliance sweep for all overdue trainings",
                    description = "Send notifications and track non-compliant associate submissions before month-end audit.",
                    assignedAssociateId = 5,
                    assignedAssociateName = "Elena Rostova",
                    dueDate = "2026-10-28",
                    priority = "Medium",
                    status = ActionItemStatus.OPEN
                ),
                ActionItemEntity(
                    id = 6,
                    governanceCallId = 1,
                    governanceTitle = "Sprint 24 Executive SteerCo & Escalation",
                    title = "Archive completed Sprint 23 test automation logs",
                    description = "Package and upload regression pass certificates to client compliance repository.",
                    assignedAssociateId = 5,
                    assignedAssociateName = "Elena Rostova",
                    dueDate = "2026-10-25",
                    priority = "Low",
                    status = ActionItemStatus.RESOLVED
                )
            )
            dao.insertActionItems(actionItems)

            // SOWs
            val sows = listOf(
                SowEntity(1, "SOW-2026-BNK-101", "Titan Mobile Banking Re-architecture", "Global Retail Bank", 680000.0, "USD", "2026-01-01", "2026-12-31", "Active"),
                SowEntity(2, "SOW-2026-CLD-204", "Core Cloud Modernization & DevOps Platform", "FinTech International", 450000.0, "USD", "2026-04-01", "2027-03-31", "Active"),
                SowEntity(3, "SOW-2025-SEC-012", "Enterprise Cyber Resilience & Auth Framework", "Global Retail Bank", 320000.0, "USD", "2025-10-01", "2026-09-30", "Extended")
            )
            dao.insertSows(sows)

            // SOW Amendments
            val amendments = listOf(
                SowAmendmentEntity(1, 1, "SOW-2026-BNK-101", "AMD-01", "2026-06-15", "Scope Expansion for Biometric TouchID & FaceID Native SDK Integration", 75000.0, 2),
                SowAmendmentEntity(2, 1, "SOW-2026-BNK-101", "AMD-02", "2026-09-20", "Real-time Anti-Fraud Pipeline AI Hookup & Stream Ingestion", 95000.0, 3),
                SowAmendmentEntity(3, 2, "SOW-2026-CLD-204", "AMD-01", "2026-08-10", "Multi-region Disaster Recovery Cluster Provisioning & Terraform Automation", 60000.0, 0)
            )
            dao.insertAmendments(amendments)

            // Milestones
            val milestones = listOf(
                MilestoneEntity(1, 1, "SOW-2026-BNK-101", "Sprint 20-22 Feature Delivery", "2026-10-05", "Q4 2026", 140000.0, "Achieved", "Sprint Demo Acceptance"),
                MilestoneEntity(2, 1, "SOW-2026-BNK-101", "UAT Sign-off & Mobile Security Audit", "2026-11-15", "Q4 2026", 165000.0, "In Progress", "Client UAT Certification"),
                MilestoneEntity(3, 1, "SOW-2026-BNK-101", "Production Cutover & Pilot Rollout", "2026-12-20", "Q4 2026", 180000.0, "Pending", "Go-Live Sign-off"),
                MilestoneEntity(4, 2, "SOW-2026-CLD-204", "Cloud Infrastructure IaC Final Deployment", "2026-11-30", "Q4 2026", 110000.0, "In Progress", "Terraform IaC Acceptance"),
                MilestoneEntity(5, 2, "SOW-2026-CLD-204", "Next-Gen Microservices Migration Phase 1", "2027-01-25", "Q1 2027", 125000.0, "Pending", "Service Benchmark Approval"),
                MilestoneEntity(6, 1, "SOW-2026-BNK-101", "Q1 2027 Mobile Banking Feature Batch", "2027-02-28", "Q1 2027", 150000.0, "Pending", "Milestone Deliverable Sign-off")
            )
            dao.insertMilestones(milestones)

            // Operational Expenses
            val expenses = listOf(
                ExpenseEntity(1, 1, "SOW-2026-BNK-101", ExpenseCategory.INFRASTRUCTURE, 18500.0, "2026-10-02", "Dedicated Cloud Performance Testing Instances", "INV-AWS-8891"),
                ExpenseEntity(2, 1, "SOW-2026-BNK-101", ExpenseCategory.SOFTWARE_LICENSES, 12400.0, "2026-10-10", "Enterprise SonarQube & Fortify Static Analysis Seat Licenses", "INV-LIC-4421"),
                ExpenseEntity(3, 2, "SOW-2026-CLD-204", ExpenseCategory.TRAVEL, 6850.0, "2026-10-18", "Client On-site Architecture Workshop & SteerCo Travel", "INV-TRV-1029"),
                ExpenseEntity(4, 1, "SOW-2026-BNK-101", ExpenseCategory.MISCELLANEOUS, 3200.0, "2026-10-24", "Hardware Security Keys (YubiKeys) for Offshore Team", "INV-MSC-7741"),
                ExpenseEntity(5, 2, "SOW-2026-CLD-204", ExpenseCategory.INFRASTRUCTURE, 14200.0, "2026-09-15", "Kubernetes Dev/Stage clusters hosting", "INV-AWS-8219")
            )
            dao.insertExpenses(expenses)
        }
    }
}
