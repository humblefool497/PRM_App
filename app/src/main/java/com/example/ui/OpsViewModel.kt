package com.example.ui

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.export.ReportExportUtils
import com.example.data.local.AppDatabase
import com.example.data.local.entity.ActionItemEntity
import com.example.data.local.entity.ActionItemStatus
import com.example.data.local.entity.AssociateEntity
import com.example.data.local.entity.ComplianceEntity
import com.example.data.local.entity.ExpenseEntity
import com.example.data.local.entity.GovernanceCallEntity
import com.example.data.local.entity.MilestoneEntity
import com.example.data.local.entity.PTSStatus
import com.example.data.local.entity.SowAmendmentEntity
import com.example.data.local.entity.SowEntity
import com.example.data.local.entity.TimesheetEntity
import com.example.data.repository.OpsRepository
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

data class ActiveReportModalState(
    val title: String,
    val content: String,
    val subject: String,
    val isCsvAvailable: Boolean = false,
    val csvContent: String = ""
)

class OpsViewModel(application: Application) : AndroidViewModel(application) {

    private val repository: OpsRepository

    init {
        val db = AppDatabase.getDatabase(application, viewModelScope)
        repository = OpsRepository(db.opsDao())
    }

    // Date selection for PTS
    private val _selectedDate = MutableStateFlow("2026-10-28")
    val selectedDate: StateFlow<String> = _selectedDate.asStateFlow()

    fun setSelectedDate(date: String) {
        _selectedDate.value = date
    }

    // Active Report Preview Dialog State
    private val _activeReport = MutableStateFlow<ActiveReportModalState?>(null)
    val activeReport: StateFlow<ActiveReportModalState?> = _activeReport.asStateFlow()

    fun dismissReport() {
        _activeReport.value = null
    }

    // Associates
    val allAssociates: StateFlow<List<AssociateEntity>> = repository.allAssociates
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Timesheets react to selectedDate
    @OptIn(ExperimentalCoroutinesApi::class)
    val timesheets: StateFlow<List<TimesheetEntity>> = _selectedDate
        .flatMapLatest { date -> repository.getTimesheetsForDate(date) }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Compliance
    val allCompliance: StateFlow<List<ComplianceEntity>> = repository.allCompliance
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val activeDefaulters: StateFlow<List<ComplianceEntity>> = repository.activeDefaulters
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Governance
    val governanceCalls: StateFlow<List<GovernanceCallEntity>> = repository.allGovernanceCalls
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Action Items
    val actionItems: StateFlow<List<ActionItemEntity>> = repository.allActionItems
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Financials
    val sows: StateFlow<List<SowEntity>> = repository.allSows
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val amendments: StateFlow<List<SowAmendmentEntity>> = repository.allAmendments
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val milestones: StateFlow<List<MilestoneEntity>> = repository.allMilestones
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val expenses: StateFlow<List<ExpenseEntity>> = repository.allExpenses
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Dashboard aggregated metrics
    val dashboardMetrics = combine(
        timesheets,
        activeDefaulters,
        actionItems,
        milestones,
        expenses
    ) { ts, defaulters, actions, mls, exps ->
        val totalTs = ts.size
        val pendingTs = ts.count { it.status == PTSStatus.PENDING }
        val submittedTs = ts.count { it.status == PTSStatus.SUBMITTED }
        val assignedTs = ts.count { it.status == PTSStatus.CLIENT_ASSIGNED }
        val completionPct = if (totalTs > 0) ((submittedTs + assignedTs) * 100 / totalTs) else 0

        val openActionsCount = actions.count { it.status != ActionItemStatus.RESOLVED }
        val highPriorityCount = actions.count { it.priority == "High" && it.status != ActionItemStatus.RESOLVED }

        val q4Milestones = mls.filter { it.quarter.contains("Q4", ignoreCase = true) }
        val q4ProjectedTotal = q4Milestones.sumOf { it.amount }
        val q4EarningsAchieved = q4Milestones.filter { it.status == "Achieved" }.sumOf { it.amount }

        val totalExp = exps.sumOf { it.amount }
        val netOperatingMargin = q4ProjectedTotal - totalExp
        val marginPct = if (q4ProjectedTotal > 0) (netOperatingMargin / q4ProjectedTotal) * 100 else 0.0

        val q1NextQuarterForecast = mls.filter { it.quarter.contains("Q1", ignoreCase = true) }.sumOf { it.amount }

        DashboardMetrics(
            totalAssociates = totalTs,
            ptsCompletionPct = completionPct,
            ptsPendingCount = pendingTs,
            ptsSubmittedCount = submittedTs,
            ptsAssignedCount = assignedTs,
            activeDefaultersCount = defaulters.size,
            openActionsCount = openActionsCount,
            highPriorityActionsCount = highPriorityCount,
            q4ProjectedTotal = q4ProjectedTotal,
            q4EarningsAchieved = q4EarningsAchieved,
            totalExpenses = totalExp,
            netOperatingMargin = netOperatingMargin,
            marginPct = marginPct,
            q1Forecast = q1NextQuarterForecast
        )
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), DashboardMetrics())

    // User Operations: Module 1 PTS
    fun cycleTimesheetStatus(timesheet: TimesheetEntity) {
        viewModelScope.launch {
            val nextStatus = when (timesheet.status) {
                PTSStatus.PENDING -> PTSStatus.SUBMITTED
                PTSStatus.SUBMITTED -> PTSStatus.CLIENT_ASSIGNED
                PTSStatus.CLIENT_ASSIGNED -> PTSStatus.PENDING
                else -> PTSStatus.PENDING
            }
            val hours = if (nextStatus == PTSStatus.PENDING) 0.0 else 8.0
            val updated = timesheet.copy(
                status = nextStatus,
                hoursLogged = hours,
                updatedAt = System.currentTimeMillis()
            )
            repository.updateTimesheet(updated)
        }
    }

    fun submitAllPendingTimesheets() {
        viewModelScope.launch {
            timesheets.value.filter { it.status == PTSStatus.PENDING }.forEach { ts ->
                repository.updateTimesheet(
                    ts.copy(
                        status = PTSStatus.SUBMITTED,
                        hoursLogged = 8.0,
                        taskNotes = "Bulk verified submission by Ops Manager",
                        updatedAt = System.currentTimeMillis()
                    )
                )
            }
        }
    }

    fun generateDailyPtsReport() {
        val date = _selectedDate.value
        val list = timesheets.value
        val md = ReportExportUtils.generateDailyPtsMarkdown(date, list)
        val csv = ReportExportUtils.generateDailyPtsCsv(date, list)
        _activeReport.value = ActiveReportModalState(
            title = "Daily PTS Validation Report",
            content = md,
            subject = "OpsManager Daily PTS Report - $date",
            isCsvAvailable = true,
            csvContent = csv
        )
    }

    // User Operations: Module 1 Associates
    fun addAssociate(name: String, email: String, empId: String, role: String, project: String, client: String) {
        viewModelScope.launch {
            val assoc = AssociateEntity(
                empId = empId.ifBlank { "EMP-${(1000..9999).random()}" },
                name = name,
                email = email,
                designation = role,
                project = project,
                clientAccount = client,
                isActive = true
            )
            val newId = repository.insertAssociate(assoc)
            // also create timesheet entry for selected date
            repository.insertTimesheet(
                TimesheetEntity(
                    associateId = newId,
                    associateName = assoc.name,
                    empId = assoc.empId,
                    date = _selectedDate.value,
                    status = PTSStatus.PENDING,
                    hoursLogged = 0.0,
                    taskNotes = ""
                )
            )
        }
    }

    fun updateAssociate(associate: AssociateEntity) {
        viewModelScope.launch {
            repository.updateAssociate(associate)
        }
    }

    fun deleteAssociate(associate: AssociateEntity) {
        viewModelScope.launch {
            repository.deleteAssociate(associate)
        }
    }

    // User Operations: Module 2 Compliance
    fun toggleCompliance(compliance: ComplianceEntity) {
        viewModelScope.launch {
            repository.toggleCompliance(compliance)
        }
    }

    fun addComplianceRecord(
        associate: AssociateEntity,
        streamType: String,
        title: String,
        dueDate: String,
        detail: String
    ) {
        viewModelScope.launch {
            val record = ComplianceEntity(
                associateId = associate.id,
                associateName = associate.name,
                empId = associate.empId,
                streamType = streamType,
                title = title,
                dueDate = dueDate,
                detail = detail,
                isCompleted = false,
                notes = "Assigned by Ops Manager"
            )
            repository.insertCompliance(record)
        }
    }

    fun generateComplianceReport() {
        val records = allCompliance.value
        val md = ReportExportUtils.generateComplianceDefaulterMarkdown(records)
        _activeReport.value = ActiveReportModalState(
            title = "Compliance & Defaulter Audit",
            content = md,
            subject = "OpsManager Compliance & Defaulter Audit Report"
        )
    }

    // User Operations: Module 3 Governance & Action Items
    fun addGovernanceCall(title: String, clientStakeholder: String, callType: String, severity: String, minutes: String) {
        viewModelScope.launch {
            val todayStr = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())
            val call = GovernanceCallEntity(
                title = title,
                date = todayStr,
                clientStakeholder = clientStakeholder,
                callType = callType,
                severity = severity,
                meetingMinutes = minutes
            )
            repository.insertGovernanceCall(call)
        }
    }

    fun addActionItem(
        title: String,
        description: String,
        associate: AssociateEntity?,
        dueDate: String,
        priority: String,
        governanceCall: GovernanceCallEntity?
    ) {
        viewModelScope.launch {
            val item = ActionItemEntity(
                governanceCallId = governanceCall?.id,
                governanceTitle = governanceCall?.title,
                title = title,
                description = description,
                assignedAssociateId = associate?.id,
                assignedAssociateName = associate?.name ?: "Unassigned",
                dueDate = dueDate,
                priority = priority,
                status = ActionItemStatus.OPEN
            )
            repository.insertActionItem(item)
        }
    }

    fun cycleActionItemStatus(item: ActionItemEntity) {
        viewModelScope.launch {
            val nextStatus = when (item.status) {
                ActionItemStatus.OPEN -> ActionItemStatus.IN_PROGRESS
                ActionItemStatus.IN_PROGRESS -> ActionItemStatus.RESOLVED
                ActionItemStatus.RESOLVED -> ActionItemStatus.OPEN
                else -> ActionItemStatus.OPEN
            }
            repository.updateActionItem(item.copy(status = nextStatus))
        }
    }

    fun deleteActionItem(item: ActionItemEntity) {
        viewModelScope.launch {
            repository.deleteActionItem(item)
        }
    }

    fun generateGovernanceReport(filterAssignee: String? = null, filterStatus: String? = null) {
        val list = actionItems.value
        val md = ReportExportUtils.generateGovernanceActionSummaryMarkdown(list, filterAssignee, filterStatus)
        _activeReport.value = ActiveReportModalState(
            title = "Governance Action Items Summary",
            content = md,
            subject = "OpsManager Action Items & Escalation Summary"
        )
    }

    // User Operations: Module 4 Financials
    fun addSow(sowNumber: String, title: String, clientName: String, totalValue: Double, startDate: String, endDate: String) {
        viewModelScope.launch {
            val sow = SowEntity(
                sowNumber = sowNumber,
                title = title,
                clientName = clientName,
                totalValue = totalValue,
                startDate = startDate,
                endDate = endDate,
                status = "Active"
            )
            repository.insertSow(sow)
        }
    }

    fun updateSow(sow: SowEntity) {
        viewModelScope.launch {
            repository.updateSow(sow)
        }
    }

    fun deleteSow(sow: SowEntity) {
        viewModelScope.launch {
            repository.deleteSow(sow)
        }
    }

    fun addAmendment(sow: SowEntity, amendmentNumber: String, summary: String, impact: Double, extensionMonths: Int) {
        viewModelScope.launch {
            val todayStr = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())
            val amd = SowAmendmentEntity(
                sowId = sow.id,
                sowNumber = sow.sowNumber,
                amendmentNumber = amendmentNumber,
                signedDate = todayStr,
                summary = summary,
                financialImpact = impact,
                extensionMonths = extensionMonths
            )
            repository.insertAmendment(amd)
        }
    }

    fun updateAmendment(amendment: SowAmendmentEntity) {
        viewModelScope.launch {
            repository.updateAmendment(amendment)
        }
    }

    fun deleteAmendment(amendment: SowAmendmentEntity) {
        viewModelScope.launch {
            repository.deleteAmendment(amendment)
        }
    }

    fun addMilestone(sow: SowEntity, name: String, targetDate: String, quarter: String, amount: Double, billingTrigger: String) {
        viewModelScope.launch {
            val milestone = MilestoneEntity(
                sowId = sow.id,
                sowNumber = sow.sowNumber,
                name = name,
                targetDate = targetDate,
                quarter = quarter,
                amount = amount,
                status = "Pending",
                billingTrigger = billingTrigger
            )
            repository.insertMilestone(milestone)
        }
    }

    fun updateMilestone(milestone: MilestoneEntity) {
        viewModelScope.launch {
            repository.updateMilestone(milestone)
        }
    }

    fun deleteMilestone(milestone: MilestoneEntity) {
        viewModelScope.launch {
            repository.deleteMilestone(milestone)
        }
    }

    fun cycleMilestoneStatus(milestone: MilestoneEntity) {
        viewModelScope.launch {
            val nextStatus = when (milestone.status) {
                "Pending" -> "In Progress"
                "In Progress" -> "Achieved"
                "Achieved" -> "Pending"
                else -> "Pending"
            }
            repository.updateMilestone(milestone.copy(status = nextStatus))
        }
    }

    fun addExpense(sow: SowEntity, category: String, amount: Double, description: String, invoiceRef: String) {
        viewModelScope.launch {
            val todayStr = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())
            val exp = ExpenseEntity(
                sowId = sow.id,
                sowNumber = sow.sowNumber,
                category = category,
                amount = amount,
                date = todayStr,
                description = description,
                invoiceRef = invoiceRef
            )
            repository.insertExpense(exp)
        }
    }

    fun updateExpense(expense: ExpenseEntity) {
        viewModelScope.launch {
            repository.updateExpense(expense)
        }
    }

    fun deleteExpense(expense: ExpenseEntity) {
        viewModelScope.launch {
            repository.deleteExpense(expense)
        }
    }

    fun generateFinancialReport() {
        val md = ReportExportUtils.generateFinancialProjectionsMarkdown(
            sows.value,
            amendments.value,
            milestones.value,
            expenses.value
        )
        _activeReport.value = ActiveReportModalState(
            title = "Financial Projections & Portfolio Performance",
            content = md,
            subject = "OpsManager Quarterly Financial Projections & Portfolio Report"
        )
    }
}

data class DashboardMetrics(
    val totalAssociates: Int = 0,
    val ptsCompletionPct: Int = 0,
    val ptsPendingCount: Int = 0,
    val ptsSubmittedCount: Int = 0,
    val ptsAssignedCount: Int = 0,
    val activeDefaultersCount: Int = 0,
    val openActionsCount: Int = 0,
    val highPriorityActionsCount: Int = 0,
    val q4ProjectedTotal: Double = 0.0,
    val q4EarningsAchieved: Double = 0.0,
    val totalExpenses: Double = 0.0,
    val netOperatingMargin: Double = 0.0,
    val marginPct: Double = 0.0,
    val q1Forecast: Double = 0.0
)
