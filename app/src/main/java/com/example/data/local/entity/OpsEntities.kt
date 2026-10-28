package com.example.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "associates")
data class AssociateEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val empId: String,
    val name: String,
    val email: String,
    val designation: String,
    val project: String,
    val clientAccount: String,
    val isActive: Boolean = true
)

object PTSStatus {
    const val PENDING = "Pending Submission"
    const val SUBMITTED = "Submitted - Awaiting Review"
    const val CLIENT_ASSIGNED = "Assigned by Client Manager"
}

@Entity(tableName = "timesheets")
data class TimesheetEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val associateId: Long,
    val associateName: String,
    val empId: String,
    val date: String, // YYYY-MM-DD
    val status: String = PTSStatus.PENDING, // "Pending Submission", "Submitted - Awaiting Review", "Assigned by Client Manager"
    val hoursLogged: Double = 8.0,
    val taskNotes: String = "",
    val updatedAt: Long = System.currentTimeMillis()
)

object ComplianceStream {
    const val INTERNAL_TRAINING = "Mandatory Internal Training"
    const val CORPORATE_DECLARATION = "Corporate Portal Declaration"
    const val CLIENT_PORTAL = "Client Portal Declaration"
}

@Entity(tableName = "compliance_records")
data class ComplianceEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val associateId: Long,
    val associateName: String,
    val empId: String,
    val streamType: String, // ComplianceStream constants
    val title: String, // Course name, Declaration Type, or Client Account
    val dueDate: String, // YYYY-MM-DD
    val detail: String, // e.g. "Q4 Mandatory", "Client Account: FirstBank", "Cybersecurity v2"
    val isCompleted: Boolean = false,
    val completedDate: String? = null,
    val notes: String = ""
)

@Entity(tableName = "governance_calls")
data class GovernanceCallEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val title: String,
    val date: String, // YYYY-MM-DD
    val clientStakeholder: String,
    val callType: String, // "Governance Call", "Client Escalation", "Bi-Weekly SteerCo", "Monthly Review"
    val severity: String, // "Critical", "High", "Medium", "Low"
    val meetingMinutes: String,
    val createdAt: Long = System.currentTimeMillis()
)

object ActionItemStatus {
    const val OPEN = "Open"
    const val IN_PROGRESS = "In Progress"
    const val RESOLVED = "Resolved"
}

@Entity(tableName = "action_items")
data class ActionItemEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val governanceCallId: Long? = null,
    val governanceTitle: String? = null,
    val title: String,
    val description: String,
    val assignedAssociateId: Long? = null,
    val assignedAssociateName: String = "Unassigned",
    val dueDate: String, // YYYY-MM-DD
    val priority: String = "Medium", // "High", "Medium", "Low"
    val status: String = ActionItemStatus.OPEN, // "Open", "In Progress", "Resolved"
    val createdAt: Long = System.currentTimeMillis()
)

@Entity(tableName = "sows")
data class SowEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val sowNumber: String,
    val title: String,
    val clientName: String,
    val totalValue: Double,
    val currency: String = "USD",
    val startDate: String,
    val endDate: String,
    val status: String = "Active" // "Active", "Extended", "Pending Signature", "Closed"
)

@Entity(tableName = "sow_amendments")
data class SowAmendmentEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val sowId: Long,
    val sowNumber: String,
    val amendmentNumber: String, // e.g., "AMD-01"
    val signedDate: String,
    val summary: String,
    val financialImpact: Double, // e.g., +45000.0
    val extensionMonths: Int = 0
)

@Entity(tableName = "milestones")
data class MilestoneEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val sowId: Long,
    val sowNumber: String,
    val name: String,
    val targetDate: String,
    val quarter: String, // e.g. "Q4 2026", "Q1 2027"
    val amount: Double,
    val status: String = "Pending", // "Pending", "In Progress", "Achieved"
    val billingTrigger: String // e.g. "UAT Sign-off", "Production Go-Live"
)

object ExpenseCategory {
    const val INFRASTRUCTURE = "Infrastructure"
    const val SOFTWARE_LICENSES = "Software Licenses"
    const val TRAVEL = "Travel"
    const val MISCELLANEOUS = "Miscellaneous"
}

@Entity(tableName = "expenses")
data class ExpenseEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val sowId: Long,
    val sowNumber: String,
    val category: String, // ExpenseCategory constants
    val amount: Double,
    val date: String,
    val description: String,
    val invoiceRef: String = ""
)
