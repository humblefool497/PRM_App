package com.example.data.repository

import com.example.data.local.dao.OpsDao
import com.example.data.local.entity.ActionItemEntity
import com.example.data.local.entity.AssociateEntity
import com.example.data.local.entity.ComplianceEntity
import com.example.data.local.entity.ExpenseEntity
import com.example.data.local.entity.GovernanceCallEntity
import com.example.data.local.entity.MilestoneEntity
import com.example.data.local.entity.SowAmendmentEntity
import com.example.data.local.entity.SowEntity
import com.example.data.local.entity.TimesheetEntity
import kotlinx.coroutines.flow.Flow
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class OpsRepository(private val dao: OpsDao) {

    val allAssociates: Flow<List<AssociateEntity>> = dao.getAllAssociates()
    val activeAssociates: Flow<List<AssociateEntity>> = dao.getActiveAssociates()

    suspend fun insertAssociate(associate: AssociateEntity): Long = dao.insertAssociate(associate)
    suspend fun updateAssociate(associate: AssociateEntity) = dao.updateAssociate(associate)
    suspend fun deleteAssociate(associate: AssociateEntity) = dao.deleteAssociate(associate)

    fun getTimesheetsForDate(date: String): Flow<List<TimesheetEntity>> = dao.getTimesheetsByDate(date)
    val allTimesheets: Flow<List<TimesheetEntity>> = dao.getAllTimesheets()

    suspend fun insertTimesheet(timesheet: TimesheetEntity): Long = dao.insertTimesheet(timesheet)
    suspend fun updateTimesheet(timesheet: TimesheetEntity) = dao.updateTimesheet(timesheet)
    suspend fun deleteTimesheet(timesheet: TimesheetEntity) = dao.deleteTimesheet(timesheet)

    val allCompliance: Flow<List<ComplianceEntity>> = dao.getAllComplianceRecords()
    val activeDefaulters: Flow<List<ComplianceEntity>> = dao.getActiveDefaulters()
    fun getComplianceByStream(stream: String): Flow<List<ComplianceEntity>> = dao.getComplianceByStream(stream)

    suspend fun insertCompliance(compliance: ComplianceEntity): Long = dao.insertCompliance(compliance)
    suspend fun updateCompliance(compliance: ComplianceEntity) = dao.updateCompliance(compliance)
    suspend fun toggleCompliance(compliance: ComplianceEntity) {
        val updated = if (compliance.isCompleted) {
            compliance.copy(isCompleted = false, completedDate = null)
        } else {
            val todayStr = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())
            compliance.copy(isCompleted = true, completedDate = todayStr)
        }
        dao.updateCompliance(updated)
    }
    suspend fun deleteCompliance(compliance: ComplianceEntity) = dao.deleteCompliance(compliance)

    val allGovernanceCalls: Flow<List<GovernanceCallEntity>> = dao.getAllGovernanceCalls()
    suspend fun insertGovernanceCall(call: GovernanceCallEntity): Long = dao.insertGovernanceCall(call)
    suspend fun deleteGovernanceCall(call: GovernanceCallEntity) = dao.deleteGovernanceCall(call)

    val allActionItems: Flow<List<ActionItemEntity>> = dao.getAllActionItems()
    val openActionItems: Flow<List<ActionItemEntity>> = dao.getOpenActionItems()

    suspend fun insertActionItem(item: ActionItemEntity): Long = dao.insertActionItem(item)
    suspend fun updateActionItem(item: ActionItemEntity) = dao.updateActionItem(item)
    suspend fun deleteActionItem(item: ActionItemEntity) = dao.deleteActionItem(item)

    val allSows: Flow<List<SowEntity>> = dao.getAllSows()
    suspend fun insertSow(sow: SowEntity): Long = dao.insertSow(sow)
    suspend fun updateSow(sow: SowEntity) = dao.updateSow(sow)
    suspend fun deleteSow(sow: SowEntity) = dao.deleteSow(sow)

    val allAmendments: Flow<List<SowAmendmentEntity>> = dao.getAllAmendments()
    fun getAmendmentsForSow(sowId: Long): Flow<List<SowAmendmentEntity>> = dao.getAmendmentsForSow(sowId)
    suspend fun insertAmendment(amendment: SowAmendmentEntity): Long = dao.insertAmendment(amendment)
    suspend fun deleteAmendment(amendment: SowAmendmentEntity) = dao.deleteAmendment(amendment)

    val allMilestones: Flow<List<MilestoneEntity>> = dao.getAllMilestones()
    suspend fun insertMilestone(milestone: MilestoneEntity): Long = dao.insertMilestone(milestone)
    suspend fun updateMilestone(milestone: MilestoneEntity) = dao.updateMilestone(milestone)
    suspend fun deleteMilestone(milestone: MilestoneEntity) = dao.deleteMilestone(milestone)

    val allExpenses: Flow<List<ExpenseEntity>> = dao.getAllExpenses()
    suspend fun insertExpense(expense: ExpenseEntity): Long = dao.insertExpense(expense)
    suspend fun deleteExpense(expense: ExpenseEntity) = dao.deleteExpense(expense)
}
