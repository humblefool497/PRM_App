package com.example.data.export

import android.content.Context
import android.content.Intent
import com.example.data.local.entity.ActionItemEntity
import com.example.data.local.entity.ComplianceEntity
import com.example.data.local.entity.ExpenseEntity
import com.example.data.local.entity.MilestoneEntity
import com.example.data.local.entity.PTSStatus
import com.example.data.local.entity.SowAmendmentEntity
import com.example.data.local.entity.SowEntity
import com.example.data.local.entity.TimesheetEntity
import java.text.NumberFormat
import java.util.Locale

object ReportExportUtils {

    private val currencyFormatter = NumberFormat.getCurrencyInstance(Locale.US).apply {
        maximumFractionDigits = 0
    }

    /**
     * Module 1: Daily PTS Report
     * Clean summary of associate statuses, highlight-flagging missing entries ("Pending Submission").
     */
    fun generateDailyPtsMarkdown(date: String, timesheets: List<TimesheetEntity>): String {
        val total = timesheets.size
        val pending = timesheets.count { it.status == PTSStatus.PENDING }
        val submitted = timesheets.count { it.status == PTSStatus.SUBMITTED }
        val assigned = timesheets.count { it.status == PTSStatus.CLIENT_ASSIGNED }
        val completionRate = if (total > 0) ((submitted + assigned) * 100 / total) else 0

        val sb = StringBuilder()
        sb.appendLine("# DAILY PROJECT TIMESHEET (PTS) REPORT")
        sb.appendLine("Date: $date")
        sb.appendLine("Generated At: ${System.currentTimeMillis()}")
        sb.appendLine("--------------------------------")
        sb.appendLine("EXECUTIVE SUMMARY:")
        sb.appendLine("- Total Roster: $total Associates")
        sb.appendLine("- Submitted & Awaiting Review: $submitted (${if (total > 0) submitted * 100 / total else 0}%)")
        sb.appendLine("- Assigned by Client Manager: $assigned (${if (total > 0) assigned * 100 / total else 0}%)")
        sb.appendLine("- [ALERT] Pending Submissions: $pending (${if (total > 0) pending * 100 / total else 0}%)")
        sb.appendLine("- Effective Completion Rate: $completionRate%")
        sb.appendLine("--------------------------------")
        sb.appendLine("ASSOCIATE STATUS BREAKDOWN:")
        sb.appendLine()

        timesheets.forEachIndexed { index, ts ->
            val flag = when (ts.status) {
                PTSStatus.PENDING -> "[!! DEFAULTER / MISSING !!]"
                PTSStatus.SUBMITTED -> "[✓ SUBMITTED]"
                PTSStatus.CLIENT_ASSIGNED -> "[🔒 CLIENT LOCKED]"
                else -> "[UNKNOWN]"
            }
            sb.appendLine("${index + 1}. ${ts.associateName} (${ts.empId})")
            sb.appendLine("   Status: ${ts.status} $flag")
            sb.appendLine("   Logged Hours: ${ts.hoursLogged} hrs")
            if (ts.taskNotes.isNotBlank()) {
                sb.appendLine("   Task Notes: ${ts.taskNotes}")
            }
            sb.appendLine()
        }

        sb.appendLine("--------------------------------")
        sb.appendLine("End of PTS Report - OpsManager Enterprise")
        return sb.toString()
    }

    fun generateDailyPtsCsv(date: String, timesheets: List<TimesheetEntity>): String {
        val sb = StringBuilder()
        sb.appendLine("Employee ID,Associate Name,Date,Status,Hours Logged,Missing Flag,Notes")
        timesheets.forEach { ts ->
            val isMissing = if (ts.status == PTSStatus.PENDING) "YES - DEFAULTER" else "NO"
            val escapedNotes = "\"${ts.taskNotes.replace("\"", "\"\"")}\""
            sb.appendLine("${ts.empId},\"${ts.associateName}\",${ts.date},\"${ts.status}\",${ts.hoursLogged},$isMissing,$escapedNotes")
        }
        return sb.toString()
    }

    /**
     * Module 2: Compliance & Defaulter Audit Report
     */
    fun generateComplianceDefaulterMarkdown(records: List<ComplianceEntity>): String {
        val nonCompliant = records.filter { !it.isCompleted }
        val compliant = records.filter { it.isCompleted }

        val sb = StringBuilder()
        sb.appendLine("# COMPLIANCE & DEFAULTER AUDIT REPORT")
        sb.appendLine("Total Tracked Items: ${records.size}")
        sb.appendLine("Active Non-Compliant Defaulters: ${nonCompliant.size}")
        sb.appendLine("Compliant Submissions: ${compliant.size}")
        sb.appendLine("--------------------------------")
        sb.appendLine("HIGH-VISIBILITY DEFAULTER MATRIX:")
        sb.appendLine()

        if (nonCompliant.isEmpty()) {
            sb.appendLine("All tracked associates are currently 100% compliant!")
        } else {
            nonCompliant.forEachIndexed { i, record ->
                sb.appendLine("${i + 1}. [CRIMSON FLAG - OVERDUE]")
                sb.appendLine("   Associate: ${record.associateName} (${record.empId})")
                sb.appendLine("   Stream: ${record.streamType}")
                sb.appendLine("   Requirement: ${record.title}")
                sb.appendLine("   Due Date: ${record.dueDate}")
                sb.appendLine("   Details: ${record.detail}")
                if (record.notes.isNotBlank()) {
                    sb.appendLine("   Notes: ${record.notes}")
                }
                sb.appendLine()
            }
        }

        sb.appendLine("--------------------------------")
        sb.appendLine("COMPLIANT ITEMS RECORD:")
        compliant.forEachIndexed { i, record ->
            sb.appendLine("${i + 1}. [COMPLIANT] ${record.associateName} - ${record.title} (Verified: ${record.completedDate ?: "Yes"})")
        }
        return sb.toString()
    }

    /**
     * Module 3: Governance & Escalations Summary Report
     */
    fun generateGovernanceActionSummaryMarkdown(
        actions: List<ActionItemEntity>,
        filterAssignee: String? = null,
        filterStatus: String? = null
    ): String {
        val filtered = actions.filter {
            (filterAssignee == null || it.assignedAssociateName.equals(filterAssignee, ignoreCase = true)) &&
            (filterStatus == null || it.status.equals(filterStatus, ignoreCase = true))
        }

        val sb = StringBuilder()
        sb.appendLine("# GOVERNANCE ACTION ITEMS & REMINDERS SUMMARY")
        sb.appendLine("Filter: Assignee = ${filterAssignee ?: "ALL"}, Status = ${filterStatus ?: "ALL"}")
        sb.appendLine("Total Matching Action Items: ${filtered.size}")
        sb.appendLine("--------------------------------")

        filtered.forEachIndexed { i, item ->
            val priorityFlag = if (item.priority == "High") "[HIGH PRIORITY]" else "[${item.priority.uppercase()}]"
            sb.appendLine("${i + 1}. $priorityFlag ${item.title}")
            sb.appendLine("   Status: ${item.status}")
            sb.appendLine("   Assignee: ${item.assignedAssociateName}")
            sb.appendLine("   Due Date: ${item.dueDate}")
            if (!item.governanceTitle.isNullOrBlank()) {
                sb.appendLine("   Linked Governance: ${item.governanceTitle}")
            }
            sb.appendLine("   Description: ${item.description}")
            sb.appendLine()
        }
        return sb.toString()
    }

    /**
     * Module 4: Project Financials & Projections Report
     */
    fun generateFinancialProjectionsMarkdown(
        sows: List<SowEntity>,
        amendments: List<SowAmendmentEntity>,
        milestones: List<MilestoneEntity>,
        expenses: List<ExpenseEntity>
    ): String {
        val totalSowValue = sows.sumOf { it.totalValue }
        val totalAmendmentsImpact = amendments.sumOf { it.financialImpact }
        val grossPortfolioValue = totalSowValue + totalAmendmentsImpact

        val q4Milestones = milestones.filter { it.quarter.contains("Q4", ignoreCase = true) }
        val q4EarningsAchieved = q4Milestones.filter { it.status == "Achieved" }.sumOf { it.amount }
        val q4ProjectedTotal = q4Milestones.sumOf { it.amount }

        val totalExpenses = expenses.sumOf { it.amount }
        val netProjectedMargin = q4ProjectedTotal - totalExpenses
        val marginPercent = if (q4ProjectedTotal > 0) (netProjectedMargin / q4ProjectedTotal) * 100 else 0.0

        val q1NextQuarterForecast = milestones.filter { it.quarter.contains("Q1", ignoreCase = true) }.sumOf { it.amount }

        val sb = StringBuilder()
        sb.appendLine("# PROJECT FINANCIALS & QUARTERLY PROJECTIONS")
        sb.appendLine("Portfolio Gross Contract Value: ${currencyFormatter.format(grossPortfolioValue)}")
        sb.appendLine("Base SOWs: ${currencyFormatter.format(totalSowValue)} | Signed Amendments: ${currencyFormatter.format(totalAmendmentsImpact)}")
        sb.appendLine("--------------------------------")
        sb.appendLine("CURRENT QUARTER ANALYTICS (Q4 2026):")
        sb.appendLine("- Milestone Projected Revenue: ${currencyFormatter.format(q4ProjectedTotal)}")
        sb.appendLine("- Milestone Earnings Achieved: ${currencyFormatter.format(q4EarningsAchieved)} (${if (q4ProjectedTotal > 0) (q4EarningsAchieved * 100 / q4ProjectedTotal).toInt() else 0}%)")
        sb.appendLine("- Operational Expenses to Date: ${currencyFormatter.format(totalExpenses)}")
        sb.appendLine("- Net Projected Operating Margin: ${currencyFormatter.format(netProjectedMargin)} (${String.format(Locale.US, "%.1f", marginPercent)}%)")
        sb.appendLine("- Next Quarter (Q1 2027) Forecast: ${currencyFormatter.format(q1NextQuarterForecast)}")
        sb.appendLine("--------------------------------")
        sb.appendLine("ACTIVE SOWs:")
        sows.forEach { sow ->
            val sowAmds = amendments.filter { it.sowId == sow.id }
            val amdTotal = sowAmds.sumOf { it.financialImpact }
            sb.appendLine("• ${sow.sowNumber} - ${sow.title} (${sow.clientName})")
            sb.appendLine("  Base Value: ${currencyFormatter.format(sow.totalValue)} + Amendments: ${currencyFormatter.format(amdTotal)} = ${currencyFormatter.format(sow.totalValue + amdTotal)}")
            sb.appendLine("  Validity: ${sow.startDate} to ${sow.endDate} [Status: ${sow.status}]")
        }
        sb.appendLine("--------------------------------")
        sb.appendLine("UPCOMING MILESTONES & BILLING TRIGGERS:")
        milestones.forEach { m ->
            sb.appendLine("• [${m.status.uppercase()}] ${m.name} (${currencyFormatter.format(m.amount)})")
            sb.appendLine("  Target Date: ${m.targetDate} (${m.quarter})")
            sb.appendLine("  Billing Trigger: ${m.billingTrigger}")
        }
        return sb.toString()
    }

    /**
     * Share text directly via Android System Share Intent
     */
    fun shareText(context: Context, subject: String, content: String) {
        val intent = Intent(Intent.ACTION_SEND).apply {
            type = "text/plain"
            putExtra(Intent.EXTRA_SUBJECT, subject)
            putExtra(Intent.EXTRA_TEXT, content)
        }
        val chooser = Intent.createChooser(intent, "Share $subject via")
        chooser.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        context.startActivity(chooser)
    }
}
