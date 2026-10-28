package com.example.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
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

@Dao
interface OpsDao {

    // Associates
    @Query("SELECT * FROM associates ORDER BY name ASC")
    fun getAllAssociates(): Flow<List<AssociateEntity>>

    @Query("SELECT * FROM associates WHERE isActive = 1 ORDER BY name ASC")
    fun getActiveAssociates(): Flow<List<AssociateEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAssociate(associate: AssociateEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAssociates(associates: List<AssociateEntity>)

    @Update
    suspend fun updateAssociate(associate: AssociateEntity)

    @Delete
    suspend fun deleteAssociate(associate: AssociateEntity)

    // Timesheets
    @Query("SELECT * FROM timesheets WHERE date = :date ORDER BY associateName ASC")
    fun getTimesheetsByDate(date: String): Flow<List<TimesheetEntity>>

    @Query("SELECT * FROM timesheets ORDER BY date DESC, associateName ASC")
    fun getAllTimesheets(): Flow<List<TimesheetEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTimesheet(timesheet: TimesheetEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTimesheets(timesheets: List<TimesheetEntity>)

    @Update
    suspend fun updateTimesheet(timesheet: TimesheetEntity)

    @Delete
    suspend fun deleteTimesheet(timesheet: TimesheetEntity)

    // Compliance
    @Query("SELECT * FROM compliance_records ORDER BY isCompleted ASC, dueDate ASC")
    fun getAllComplianceRecords(): Flow<List<ComplianceEntity>>

    @Query("SELECT * FROM compliance_records WHERE isCompleted = 0 ORDER BY dueDate ASC")
    fun getActiveDefaulters(): Flow<List<ComplianceEntity>>

    @Query("SELECT * FROM compliance_records WHERE streamType = :stream ORDER BY isCompleted ASC, dueDate ASC")
    fun getComplianceByStream(stream: String): Flow<List<ComplianceEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCompliance(compliance: ComplianceEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertComplianceList(list: List<ComplianceEntity>)

    @Update
    suspend fun updateCompliance(compliance: ComplianceEntity)

    @Delete
    suspend fun deleteCompliance(compliance: ComplianceEntity)

    // Governance
    @Query("SELECT * FROM governance_calls ORDER BY date DESC")
    fun getAllGovernanceCalls(): Flow<List<GovernanceCallEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertGovernanceCall(call: GovernanceCallEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertGovernanceCalls(calls: List<GovernanceCallEntity>)

    @Update
    suspend fun updateGovernanceCall(call: GovernanceCallEntity)

    @Delete
    suspend fun deleteGovernanceCall(call: GovernanceCallEntity)

    // Action Items
    @Query("SELECT * FROM action_items ORDER BY CASE status WHEN 'Open' THEN 1 WHEN 'In Progress' THEN 2 ELSE 3 END, dueDate ASC")
    fun getAllActionItems(): Flow<List<ActionItemEntity>>

    @Query("SELECT * FROM action_items WHERE status != 'Resolved' ORDER BY dueDate ASC")
    fun getOpenActionItems(): Flow<List<ActionItemEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertActionItem(item: ActionItemEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertActionItems(items: List<ActionItemEntity>)

    @Update
    suspend fun updateActionItem(item: ActionItemEntity)

    @Delete
    suspend fun deleteActionItem(item: ActionItemEntity)

    // SOWs
    @Query("SELECT * FROM sows ORDER BY startDate DESC")
    fun getAllSows(): Flow<List<SowEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSow(sow: SowEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSows(sows: List<SowEntity>)

    @Update
    suspend fun updateSow(sow: SowEntity)

    @Delete
    suspend fun deleteSow(sow: SowEntity)

    @Query("DELETE FROM sow_amendments WHERE sowId = :sowId")
    suspend fun deleteAmendmentsForSow(sowId: Long)

    @Query("DELETE FROM milestones WHERE sowId = :sowId")
    suspend fun deleteMilestonesForSow(sowId: Long)

    @Query("DELETE FROM expenses WHERE sowId = :sowId")
    suspend fun deleteExpensesForSow(sowId: Long)

    // Amendments
    @Query("SELECT * FROM sow_amendments ORDER BY signedDate DESC")
    fun getAllAmendments(): Flow<List<SowAmendmentEntity>>

    @Query("SELECT * FROM sow_amendments WHERE sowId = :sowId ORDER BY signedDate DESC")
    fun getAmendmentsForSow(sowId: Long): Flow<List<SowAmendmentEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAmendment(amendment: SowAmendmentEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAmendments(amendments: List<SowAmendmentEntity>)

    @Update
    suspend fun updateAmendment(amendment: SowAmendmentEntity)

    @Delete
    suspend fun deleteAmendment(amendment: SowAmendmentEntity)

    // Milestones
    @Query("SELECT * FROM milestones ORDER BY targetDate ASC")
    fun getAllMilestones(): Flow<List<MilestoneEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMilestone(milestone: MilestoneEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMilestones(milestones: List<MilestoneEntity>)

    @Update
    suspend fun updateMilestone(milestone: MilestoneEntity)

    @Delete
    suspend fun deleteMilestone(milestone: MilestoneEntity)

    // Expenses
    @Query("SELECT * FROM expenses ORDER BY date DESC")
    fun getAllExpenses(): Flow<List<ExpenseEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertExpense(expense: ExpenseEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertExpenses(expenses: List<ExpenseEntity>)

    @Update
    suspend fun updateExpense(expense: ExpenseEntity)

    @Delete
    suspend fun deleteExpense(expense: ExpenseEntity)

    // Associate cleanup
    @Query("DELETE FROM timesheets WHERE associateId = :associateId")
    suspend fun deleteTimesheetsForAssociate(associateId: Long)

    @Query("DELETE FROM compliance_records WHERE associateId = :associateId")
    suspend fun deleteComplianceForAssociate(associateId: Long)
}
