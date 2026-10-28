package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Assignment
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material.icons.filled.Group
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.PendingActions
import androidx.compose.material.icons.filled.PersonAdd
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.ScrollableTabRow
import androidx.compose.material3.Surface
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.data.local.entity.AssociateEntity
import com.example.data.local.entity.ComplianceEntity
import com.example.data.local.entity.ComplianceStream
import com.example.data.local.entity.PTSStatus
import com.example.data.local.entity.TimesheetEntity
import com.example.ui.OpsViewModel
import com.example.ui.components.DefaulterBadge
import com.example.ui.components.ExportReportButton
import com.example.ui.components.PtsStatusBadge
import com.example.ui.theme.AlertAmber
import com.example.ui.theme.AlertCrimson
import com.example.ui.theme.AlertCrimsonBg
import com.example.ui.theme.AlertGreen
import com.example.ui.theme.CharcoalBorder
import com.example.ui.theme.CharcoalCard
import com.example.ui.theme.SlateLight
import com.example.ui.theme.SlateMuted
import com.example.ui.theme.TealAccent
import com.example.ui.theme.TealContainer
import com.example.ui.theme.TealLight

@Composable
fun TeamComplianceScreen(
    viewModel: OpsViewModel
) {
    var selectedSubTab by remember { mutableIntStateOf(0) }
    val tabs = listOf("PTS Timesheet", "Defaulter Matrix", "Associate Roster")

    var showAddAssociateDialog by remember { mutableStateOf(false) }
    var showAddComplianceDialog by remember { mutableStateOf(false) }

    Box(modifier = Modifier.fillMaxSize().background(Color(0xFF0B0F17))) {
        Column(modifier = Modifier.fillMaxSize()) {
            // Header
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 10.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f).padding(end = 8.dp)) {
                    Text(
                        text = "Team & Compliance",
                        color = Color.White,
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Black
                    )
                    Text(
                        text = "PTS Tracking & Defaulter Audit",
                        color = SlateLight,
                        fontSize = 11.sp,
                        maxLines = 1
                    )
                }

                if (selectedSubTab == 0) {
                    ExportReportButton(
                        label = "PTS Report",
                        onClick = { viewModel.generateDailyPtsReport() },
                        testTag = "generate_pts_report_top_button"
                    )
                } else if (selectedSubTab == 1) {
                    ExportReportButton(
                        label = "Audit Export",
                        accentColor = AlertCrimson,
                        containerColor = AlertCrimsonBg,
                        onClick = { viewModel.generateComplianceReport() },
                        testTag = "generate_compliance_report_top_button"
                    )
                }
            }

            // Sub Tab Row
            ScrollableTabRow(
                selectedTabIndex = selectedSubTab,
                containerColor = Color(0xFF131B24),
                contentColor = TealLight,
                edgePadding = 16.dp,
                indicator = { tabPositions ->
                    TabRowDefaults.SecondaryIndicator(
                        Modifier.tabIndicatorOffset(tabPositions[selectedSubTab]),
                        color = TealLight
                    )
                }
            ) {
                tabs.forEachIndexed { index, title ->
                    Tab(
                        selected = selectedSubTab == index,
                        onClick = { selectedSubTab = index },
                        text = {
                            Text(
                                text = title,
                                fontSize = 13.sp,
                                fontWeight = if (selectedSubTab == index) FontWeight.Bold else FontWeight.Normal,
                                color = if (selectedSubTab == index) TealLight else SlateLight
                            )
                        }
                    )
                }
            }

            // Content per SubTab
            when (selectedSubTab) {
                0 -> PtsTrackingSubView(viewModel)
                1 -> DefaulterMatrixSubView(viewModel)
                2 -> AssociateRosterSubView(viewModel)
            }
        }

        // Floating Action Button
        if (selectedSubTab == 2) {
            FloatingActionButton(
                onClick = { showAddAssociateDialog = true },
                containerColor = TealLight,
                contentColor = Color(0xFF0F172A),
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .padding(16.dp)
                    .testTag("add_associate_fab")
            ) {
                Icon(Icons.Default.PersonAdd, contentDescription = "Add Associate")
            }
        } else if (selectedSubTab == 1) {
            FloatingActionButton(
                onClick = { showAddComplianceDialog = true },
                containerColor = AlertCrimson,
                contentColor = Color.White,
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .padding(16.dp)
                    .testTag("add_compliance_fab")
            ) {
                Icon(Icons.Default.Add, contentDescription = "Add Compliance Item")
            }
        }
    }

    if (showAddAssociateDialog) {
        AddAssociateDialog(
            onDismiss = { showAddAssociateDialog = false },
            onAdd = { name, email, empId, role, project, client ->
                viewModel.addAssociate(name, email, empId, role, project, client)
                showAddAssociateDialog = false
            }
        )
    }

    if (showAddComplianceDialog) {
        val associates by viewModel.allAssociates.collectAsStateWithLifecycle()
        AddComplianceDialog(
            associates = associates,
            onDismiss = { showAddComplianceDialog = false },
            onAdd = { assoc, stream, title, dueDate, detail ->
                viewModel.addComplianceRecord(assoc, stream, title, dueDate, detail)
                showAddComplianceDialog = false
            }
        )
    }
}

/**
 * Module 1: PTS Timesheet Tracking & Validation SubView
 */
@Composable
private fun PtsTrackingSubView(viewModel: OpsViewModel) {
    val timesheets by viewModel.timesheets.collectAsStateWithLifecycle()
    val selectedDate by viewModel.selectedDate.collectAsStateWithLifecycle()
    var statusFilter by remember { mutableStateOf("All") }

    val total = timesheets.size
    val pending = timesheets.count { it.status == PTSStatus.PENDING }
    val submitted = timesheets.count { it.status == PTSStatus.SUBMITTED }
    val assigned = timesheets.count { it.status == PTSStatus.CLIENT_ASSIGNED }
    val completionPct = if (total > 0) ((submitted + assigned) * 100 / total) else 0

    val filteredList = when (statusFilter) {
        "Pending" -> timesheets.filter { it.status == PTSStatus.PENDING }
        "Submitted" -> timesheets.filter { it.status == PTSStatus.SUBMITTED }
        "Client Locked" -> timesheets.filter { it.status == PTSStatus.CLIENT_ASSIGNED }
        else -> timesheets
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        item {
            Spacer(modifier = Modifier.height(10.dp))
            // Aggregated Summary Bar
            Card(
                colors = CardDefaults.cardColors(containerColor = CharcoalCard),
                shape = RoundedCornerShape(12.dp),
                border = androidx.compose.foundation.BorderStroke(1.dp, CharcoalBorder)
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "DATE: $selectedDate",
                            color = TealAccent,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "Validation Engine Active",
                            color = SlateLight,
                            fontSize = 11.sp
                        )
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        PtsMetricItem("Total Team", "$total", SlateLight)
                        PtsMetricItem("Submitted", "$submitted", TealAccent)
                        PtsMetricItem("Client Locked", "$assigned", Color(0xFF38BDF8))
                        PtsMetricItem("Missing / Defaulters", "$pending", if (pending > 0) AlertCrimson else AlertGreen)
                    }
                }
            }
        }

        // Filter chips row
        item {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                listOf("All", "Pending", "Submitted", "Client Locked").forEach { filter ->
                    val isSelected = statusFilter == filter
                    Surface(
                        modifier = Modifier
                            .clip(RoundedCornerShape(8.dp))
                            .clickable { statusFilter = filter },
                        color = if (isSelected) {
                            if (filter == "Pending") AlertCrimsonBg else TealContainer
                        } else Color(0xFF1E293B),
                        shape = RoundedCornerShape(8.dp),
                        border = androidx.compose.foundation.BorderStroke(
                            1.dp,
                            if (isSelected) {
                                if (filter == "Pending") AlertCrimson else TealAccent
                            } else CharcoalBorder
                        )
                    ) {
                        Text(
                            text = when (filter) {
                                "Pending" -> "Missing ($pending)"
                                "Submitted" -> "Submitted ($submitted)"
                                "Client Locked" -> "Locked ($assigned)"
                                else -> "All ($total)"
                            },
                            color = if (isSelected) {
                                if (filter == "Pending") AlertCrimson else TealAccent
                            } else SlateLight,
                            fontSize = 11.sp,
                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                            maxLines = 1,
                            softWrap = false,
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
                        )
                    }
                }
            }
        }

        items(filteredList) { ts ->
            val isMissing = ts.status == PTSStatus.PENDING
            Card(
                colors = CardDefaults.cardColors(
                    containerColor = if (isMissing) AlertCrimsonBg.copy(alpha = 0.6f) else CharcoalCard
                ),
                shape = RoundedCornerShape(12.dp),
                border = androidx.compose.foundation.BorderStroke(
                    1.dp,
                    if (isMissing) AlertCrimson else CharcoalBorder
                )
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    text = ts.associateName,
                                    color = Color.White,
                                    fontSize = 15.sp,
                                    fontWeight = FontWeight.Bold
                                )
                                if (isMissing) {
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Surface(
                                        color = AlertCrimson,
                                        shape = RoundedCornerShape(4.dp)
                                    ) {
                                        Text(
                                            text = "MISSING",
                                            color = Color.White,
                                            fontSize = 9.sp,
                                            fontWeight = FontWeight.Black,
                                            modifier = Modifier.padding(horizontal = 4.dp, vertical = 1.dp)
                                        )
                                    }
                                }
                            }
                            Text(
                                text = "Emp ID: ${ts.empId} • Hours Logged: ${ts.hoursLogged}h",
                                color = if (isMissing) Color(0xFFFECDD3) else SlateLight,
                                fontSize = 12.sp
                            )
                        }

                        PtsStatusBadge(
                            status = ts.status,
                            onClick = { viewModel.cycleTimesheetStatus(ts) }
                        )
                    }

                    if (ts.taskNotes.isNotBlank()) {
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = "Notes: ${ts.taskNotes}",
                            color = SlateMuted,
                            fontSize = 11.sp
                        )
                    }

                    Spacer(modifier = Modifier.height(8.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.End
                    ) {
                        Text(
                            text = "Tap badge to cycle status",
                            color = SlateMuted,
                            fontSize = 10.sp
                        )
                    }
                }
            }
        }

        item {
            Spacer(modifier = Modifier.height(80.dp))
        }
    }
}

@Composable
private fun PtsMetricItem(label: String, value: String, color: Color) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(value, color = color, fontSize = 18.sp, fontWeight = FontWeight.ExtraBold)
        Text(label, color = SlateMuted, fontSize = 10.sp)
    }
}

/**
 * Module 2: Compliance & Defaulter Tracker SubView
 */
@Composable
private fun DefaulterMatrixSubView(viewModel: OpsViewModel) {
    val complianceRecords by viewModel.allCompliance.collectAsStateWithLifecycle()
    var selectedStreamFilter by remember { mutableStateOf("All") }

    val streams = listOf("All", "Trainings", "Corporate", "Client Portal", "Overdue Only")

    val filtered = complianceRecords.filter { record ->
        when (selectedStreamFilter) {
            "Trainings" -> record.streamType == ComplianceStream.INTERNAL_TRAINING
            "Corporate" -> record.streamType == ComplianceStream.CORPORATE_DECLARATION
            "Client Portal" -> record.streamType == ComplianceStream.CLIENT_PORTAL
            "Overdue Only" -> !record.isCompleted
            else -> true
        }
    }

    val overdueCount = complianceRecords.count { !it.isCompleted }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        item {
            Spacer(modifier = Modifier.height(10.dp))
            // Alert Card
            Card(
                colors = CardDefaults.cardColors(
                    containerColor = if (overdueCount > 0) AlertCrimsonBg else CharcoalCard
                ),
                shape = RoundedCornerShape(12.dp),
                border = androidx.compose.foundation.BorderStroke(
                    1.dp,
                    if (overdueCount > 0) AlertCrimson else AlertGreen
                )
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(14.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(36.dp)
                            .clip(CircleShape)
                            .background(if (overdueCount > 0) AlertCrimson else AlertGreen),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = if (overdueCount > 0) Icons.Default.Warning else Icons.Default.Check,
                            contentDescription = null,
                            tint = Color.White,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(12.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = if (overdueCount > 0) "$overdueCount Non-Compliant Defaulters Flagged" else "All Streams 100% Compliant",
                            color = Color.White,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "Tracking Internal Trainings, Corporate Declarations & Client Portals",
                            color = SlateLight,
                            fontSize = 11.sp
                        )
                    }
                }
            }
        }

        // Stream Filters
        item {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                streams.forEach { stream ->
                    val isSelected = selectedStreamFilter == stream
                    Surface(
                        modifier = Modifier
                            .clip(RoundedCornerShape(8.dp))
                            .clickable { selectedStreamFilter = stream },
                        color = if (isSelected) {
                            if (stream == "Overdue Only") AlertCrimsonBg else TealContainer
                        } else Color(0xFF1E293B),
                        shape = RoundedCornerShape(8.dp),
                        border = androidx.compose.foundation.BorderStroke(
                            1.dp,
                            if (isSelected) {
                                if (stream == "Overdue Only") AlertCrimson else TealAccent
                            } else CharcoalBorder
                        )
                    ) {
                        Text(
                            text = stream,
                            color = if (isSelected) {
                                if (stream == "Overdue Only") AlertCrimson else TealAccent
                            } else SlateLight,
                            fontSize = 11.sp,
                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                            maxLines = 1,
                            softWrap = false,
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
                        )
                    }
                }
            }
        }

        items(filtered) { record ->
            val isOverdue = !record.isCompleted
            Card(
                colors = CardDefaults.cardColors(
                    containerColor = if (isOverdue) AlertCrimsonBg.copy(alpha = 0.45f) else CharcoalCard
                ),
                shape = RoundedCornerShape(12.dp),
                border = androidx.compose.foundation.BorderStroke(
                    1.dp,
                    if (isOverdue) AlertCrimson else CharcoalBorder
                )
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(14.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            DefaulterBadge(isCompleted = record.isCompleted)
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = record.streamType,
                                color = SlateLight,
                                fontSize = 11.sp
                            )
                        }
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = record.title,
                            color = Color.White,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "Associate: ${record.associateName} (${record.empId})",
                            color = if (isOverdue) Color(0xFFFECDD3) else SlateLight,
                            fontSize = 12.sp
                        )
                        Text(
                            text = "Due Date: ${record.dueDate} • ${record.detail}",
                            color = SlateMuted,
                            fontSize = 11.sp
                        )
                        if (record.isCompleted && record.completedDate != null) {
                            Text(
                                text = "Verified on: ${record.completedDate}",
                                color = AlertGreen,
                                fontSize = 10.sp,
                                fontWeight = FontWeight.SemiBold
                            )
                        }
                    }

                    // Quick Toggle Checkbox
                    Checkbox(
                        checked = record.isCompleted,
                        onCheckedChange = { viewModel.toggleCompliance(record) },
                        colors = CheckboxDefaults.colors(
                            checkedColor = AlertGreen,
                            uncheckedColor = AlertCrimson
                        )
                    )
                }
            }
        }

        item {
            Spacer(modifier = Modifier.height(80.dp))
        }
    }
}

/**
 * Module 1: Associate Database Roster SubView
 */
@Composable
private fun AssociateRosterSubView(viewModel: OpsViewModel) {
    val associates by viewModel.allAssociates.collectAsStateWithLifecycle()
    var associateToEdit by remember { mutableStateOf<AssociateEntity?>(null) }
    var associateToDelete by remember { mutableStateOf<AssociateEntity?>(null) }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        item {
            Spacer(modifier = Modifier.height(10.dp))
            Text(
                text = "Direct-Report Associates (${associates.size})",
                color = Color.White,
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = "Manage associate assignments, project allocations, and client accounts",
                color = SlateLight,
                fontSize = 12.sp
            )
        }

        items(associates) { assoc ->
            Card(
                colors = CardDefaults.cardColors(containerColor = CharcoalCard),
                shape = RoundedCornerShape(12.dp),
                border = androidx.compose.foundation.BorderStroke(1.dp, CharcoalBorder)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(14.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f).padding(end = 8.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = assoc.name,
                                color = Color.White,
                                fontSize = 15.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Surface(
                                color = TealContainer,
                                shape = RoundedCornerShape(4.dp)
                            ) {
                                Text(
                                    text = assoc.empId,
                                    color = TealAccent,
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold,
                                    modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp)
                                )
                            }
                        }
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = assoc.designation,
                            color = SlateLight,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Medium
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "Project: ${assoc.project}",
                            color = Color(0xFF38BDF8),
                            fontSize = 11.sp
                        )
                        Text(
                            text = "Client: ${assoc.clientAccount} • ${assoc.email}",
                            color = SlateMuted,
                            fontSize = 11.sp
                        )
                    }

                    Row(verticalAlignment = Alignment.CenterVertically) {
                        IconButton(
                            onClick = { associateToEdit = assoc },
                            modifier = Modifier.size(36.dp).testTag("edit_associate_${assoc.id}")
                        ) {
                            Icon(
                                Icons.Default.Edit,
                                contentDescription = "Edit Associate",
                                tint = TealAccent,
                                modifier = Modifier.size(18.dp)
                            )
                        }
                        IconButton(
                            onClick = { associateToDelete = assoc },
                            modifier = Modifier.size(36.dp).testTag("delete_associate_${assoc.id}")
                        ) {
                            Icon(
                                Icons.Default.Delete,
                                contentDescription = "Delete Associate",
                                tint = AlertCrimson,
                                modifier = Modifier.size(18.dp)
                            )
                        }
                    }
                }
            }
        }

        item {
            Spacer(modifier = Modifier.height(80.dp))
        }
    }

    associateToEdit?.let { assoc ->
        EditAssociateDialog(
            associate = assoc,
            onDismiss = { associateToEdit = null },
            onSave = { updated ->
                viewModel.updateAssociate(updated)
                associateToEdit = null
            }
        )
    }

    associateToDelete?.let { assoc ->
        DeleteConfirmDialog(
            title = "Delete Associate",
            message = "Are you sure you want to remove ${assoc.name} (${assoc.empId})? Their timesheet entries and compliance assignments will also be deleted.",
            onDismiss = { associateToDelete = null },
            onConfirm = {
                viewModel.deleteAssociate(assoc)
                associateToDelete = null
            }
        )
    }
}

@Composable
private fun EditAssociateDialog(
    associate: AssociateEntity,
    onDismiss: () -> Unit,
    onSave: (AssociateEntity) -> Unit
) {
    var name by remember { mutableStateOf(associate.name) }
    var email by remember { mutableStateOf(associate.email) }
    var empId by remember { mutableStateOf(associate.empId) }
    var role by remember { mutableStateOf(associate.designation) }
    var project by remember { mutableStateOf(associate.project) }
    var client by remember { mutableStateOf(associate.clientAccount) }

    Dialog(onDismissRequest = onDismiss) {
        Card(
            colors = CardDefaults.cardColors(containerColor = Color(0xFF0F172A)),
            shape = RoundedCornerShape(16.dp),
            border = androidx.compose.foundation.BorderStroke(1.dp, TealLight.copy(alpha = 0.4f)),
            modifier = Modifier.padding(16.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Text("Edit Associate Details", color = Color.White, fontSize = 18.sp, fontWeight = FontWeight.Bold)

                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text("Associate Name") },
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = Color.White,
                        unfocusedTextColor = Color.White,
                        focusedBorderColor = TealLight,
                        unfocusedBorderColor = CharcoalBorder
                    ),
                    modifier = Modifier.fillMaxWidth().testTag("edit_associate_name_input")
                )

                OutlinedTextField(
                    value = empId,
                    onValueChange = { empId = it },
                    label = { Text("Employee ID") },
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = Color.White,
                        unfocusedTextColor = Color.White,
                        focusedBorderColor = TealLight,
                        unfocusedBorderColor = CharcoalBorder
                    ),
                    modifier = Modifier.fillMaxWidth()
                )

                OutlinedTextField(
                    value = email,
                    onValueChange = { email = it },
                    label = { Text("Corporate Email") },
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = Color.White,
                        unfocusedTextColor = Color.White,
                        focusedBorderColor = TealLight,
                        unfocusedBorderColor = CharcoalBorder
                    ),
                    modifier = Modifier.fillMaxWidth()
                )

                OutlinedTextField(
                    value = role,
                    onValueChange = { role = it },
                    label = { Text("Designation / Role") },
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = Color.White,
                        unfocusedTextColor = Color.White,
                        focusedBorderColor = TealLight,
                        unfocusedBorderColor = CharcoalBorder
                    ),
                    modifier = Modifier.fillMaxWidth()
                )

                OutlinedTextField(
                    value = project,
                    onValueChange = { project = it },
                    label = { Text("Project Allocation") },
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = Color.White,
                        unfocusedTextColor = Color.White,
                        focusedBorderColor = TealLight,
                        unfocusedBorderColor = CharcoalBorder
                    ),
                    modifier = Modifier.fillMaxWidth()
                )

                OutlinedTextField(
                    value = client,
                    onValueChange = { client = it },
                    label = { Text("Client Account") },
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = Color.White,
                        unfocusedTextColor = Color.White,
                        focusedBorderColor = TealLight,
                        unfocusedBorderColor = CharcoalBorder
                    ),
                    modifier = Modifier.fillMaxWidth()
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    OutlinedButton(
                        onClick = onDismiss,
                        shape = RoundedCornerShape(8.dp),
                        contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp)
                    ) {
                        Text("Cancel", color = SlateLight, maxLines = 1, softWrap = false)
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    Button(
                        onClick = {
                            if (name.isNotBlank() && email.isNotBlank()) {
                                onSave(
                                    associate.copy(
                                        name = name,
                                        empId = empId,
                                        email = email,
                                        designation = role,
                                        project = project,
                                        clientAccount = client
                                    )
                                )
                            }
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = TealLight, contentColor = Color(0xFF0F172A)),
                        shape = RoundedCornerShape(8.dp),
                        contentPadding = PaddingValues(horizontal = 14.dp, vertical = 6.dp),
                        modifier = Modifier.testTag("save_edit_associate_button")
                    ) {
                        Text("Save Changes", fontWeight = FontWeight.Bold, maxLines = 1, softWrap = false)
                    }
                }
            }
        }
    }
}

@Composable
private fun DeleteConfirmDialog(
    title: String,
    message: String,
    onDismiss: () -> Unit,
    onConfirm: () -> Unit
) {
    Dialog(onDismissRequest = onDismiss) {
        Card(
            colors = CardDefaults.cardColors(containerColor = Color(0xFF0F172A)),
            shape = RoundedCornerShape(16.dp),
            border = androidx.compose.foundation.BorderStroke(1.dp, AlertCrimson.copy(alpha = 0.5f)),
            modifier = Modifier.padding(16.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.Delete, contentDescription = null, tint = AlertCrimson, modifier = Modifier.size(24.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(title, color = Color.White, fontSize = 18.sp, fontWeight = FontWeight.Bold)
                }

                Text(message, color = SlateLight, fontSize = 13.sp, lineHeight = 18.sp)

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    OutlinedButton(
                        onClick = onDismiss,
                        shape = RoundedCornerShape(8.dp),
                        contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp)
                    ) {
                        Text("Cancel", color = SlateLight, maxLines = 1, softWrap = false)
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    Button(
                        onClick = onConfirm,
                        colors = ButtonDefaults.buttonColors(containerColor = AlertCrimson, contentColor = Color.White),
                        shape = RoundedCornerShape(8.dp),
                        contentPadding = PaddingValues(horizontal = 14.dp, vertical = 6.dp),
                        modifier = Modifier.testTag("confirm_delete_button")
                    ) {
                        Text("Delete", fontWeight = FontWeight.Bold, maxLines = 1, softWrap = false)
                    }
                }
            }
        }
    }
}

@Composable
private fun AddAssociateDialog(
    onDismiss: () -> Unit,
    onAdd: (name: String, email: String, empId: String, role: String, project: String, client: String) -> Unit
) {
    var name by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var empId by remember { mutableStateOf("") }
    var role by remember { mutableStateOf("") }
    var project by remember { mutableStateOf("Titan Mobile Banking") }
    var client by remember { mutableStateOf("Global Retail Bank") }

    Dialog(onDismissRequest = onDismiss) {
        Card(
            colors = CardDefaults.cardColors(containerColor = Color(0xFF0F172A)),
            shape = RoundedCornerShape(16.dp),
            border = androidx.compose.foundation.BorderStroke(1.dp, TealLight.copy(alpha = 0.4f)),
            modifier = Modifier.padding(16.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Text("Add New Direct-Report", color = Color.White, fontSize = 18.sp, fontWeight = FontWeight.Bold)

                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text("Associate Name") },
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = Color.White,
                        unfocusedTextColor = Color.White,
                        focusedBorderColor = TealLight,
                        unfocusedBorderColor = CharcoalBorder
                    ),
                    modifier = Modifier.fillMaxWidth().testTag("associate_name_input")
                )

                OutlinedTextField(
                    value = empId,
                    onValueChange = { empId = it },
                    label = { Text("Employee ID (e.g. EMP-1300)") },
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = Color.White,
                        unfocusedTextColor = Color.White,
                        focusedBorderColor = TealLight,
                        unfocusedBorderColor = CharcoalBorder
                    ),
                    modifier = Modifier.fillMaxWidth()
                )

                OutlinedTextField(
                    value = email,
                    onValueChange = { email = it },
                    label = { Text("Corporate Email") },
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = Color.White,
                        unfocusedTextColor = Color.White,
                        focusedBorderColor = TealLight,
                        unfocusedBorderColor = CharcoalBorder
                    ),
                    modifier = Modifier.fillMaxWidth()
                )

                OutlinedTextField(
                    value = role,
                    onValueChange = { role = it },
                    label = { Text("Designation / Role") },
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = Color.White,
                        unfocusedTextColor = Color.White,
                        focusedBorderColor = TealLight,
                        unfocusedBorderColor = CharcoalBorder
                    ),
                    modifier = Modifier.fillMaxWidth()
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    OutlinedButton(
                        onClick = onDismiss,
                        shape = RoundedCornerShape(8.dp),
                        contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp)
                    ) {
                        Text("Cancel", color = SlateLight, maxLines = 1, softWrap = false)
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    Button(
                        onClick = {
                            if (name.isNotBlank()) {
                                onAdd(name, email, empId, role, project, client)
                            }
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = TealLight, contentColor = Color(0xFF0F172A)),
                        shape = RoundedCornerShape(8.dp),
                        contentPadding = PaddingValues(horizontal = 14.dp, vertical = 6.dp),
                        modifier = Modifier.testTag("submit_associate_button")
                    ) {
                        Text("Save Associate", fontWeight = FontWeight.Bold, maxLines = 1, softWrap = false)
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun AddComplianceDialog(
    associates: List<AssociateEntity>,
    onDismiss: () -> Unit,
    onAdd: (assoc: AssociateEntity, stream: String, title: String, dueDate: String, detail: String) -> Unit
) {
    var selectedAssoc by remember { mutableStateOf(associates.firstOrNull()) }
    var selectedStream by remember { mutableStateOf(ComplianceStream.INTERNAL_TRAINING) }
    var title by remember { mutableStateOf("") }
    var dueDate by remember { mutableStateOf("2026-11-15") }
    var detail by remember { mutableStateOf("Q4 Mandate") }

    var assocExpanded by remember { mutableStateOf(false) }
    var streamExpanded by remember { mutableStateOf(false) }

    val streams = listOf(
        ComplianceStream.INTERNAL_TRAINING,
        ComplianceStream.CORPORATE_DECLARATION,
        ComplianceStream.CLIENT_PORTAL
    )

    Dialog(onDismissRequest = onDismiss) {
        Card(
            colors = CardDefaults.cardColors(containerColor = Color(0xFF0F172A)),
            shape = RoundedCornerShape(16.dp),
            border = androidx.compose.foundation.BorderStroke(1.dp, AlertCrimson.copy(alpha = 0.4f)),
            modifier = Modifier.padding(16.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Text("Add Compliance Requirement", color = Color.White, fontSize = 18.sp, fontWeight = FontWeight.Bold)

                // Associate picker
                ExposedDropdownMenuBox(
                    expanded = assocExpanded,
                    onExpandedChange = { assocExpanded = !assocExpanded }
                ) {
                    OutlinedTextField(
                        value = selectedAssoc?.name ?: "Select Associate",
                        onValueChange = {},
                        readOnly = true,
                        label = { Text("Assign To Associate") },
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = assocExpanded) },
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedTextColor = Color.White,
                            unfocusedTextColor = Color.White,
                            focusedBorderColor = TealLight,
                            unfocusedBorderColor = CharcoalBorder
                        ),
                        modifier = Modifier.fillMaxWidth().menuAnchor()
                    )
                    ExposedDropdownMenu(
                        expanded = assocExpanded,
                        onDismissRequest = { assocExpanded = false }
                    ) {
                        associates.forEach { assoc ->
                            DropdownMenuItem(
                                text = { Text("${assoc.name} (${assoc.empId})") },
                                onClick = {
                                    selectedAssoc = assoc
                                    assocExpanded = false
                                }
                            )
                        }
                    }
                }

                // Stream picker
                ExposedDropdownMenuBox(
                    expanded = streamExpanded,
                    onExpandedChange = { streamExpanded = !streamExpanded }
                ) {
                    OutlinedTextField(
                        value = selectedStream,
                        onValueChange = {},
                        readOnly = true,
                        label = { Text("Compliance Stream") },
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = streamExpanded) },
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedTextColor = Color.White,
                            unfocusedTextColor = Color.White,
                            focusedBorderColor = TealLight,
                            unfocusedBorderColor = CharcoalBorder
                        ),
                        modifier = Modifier.fillMaxWidth().menuAnchor()
                    )
                    ExposedDropdownMenu(
                        expanded = streamExpanded,
                        onDismissRequest = { streamExpanded = false }
                    ) {
                        streams.forEach { stream ->
                            DropdownMenuItem(
                                text = { Text(stream) },
                                onClick = {
                                    selectedStream = stream
                                    streamExpanded = false
                                }
                            )
                        }
                    }
                }

                OutlinedTextField(
                    value = title,
                    onValueChange = { title = it },
                    label = { Text("Requirement / Course / Type") },
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = Color.White,
                        unfocusedTextColor = Color.White,
                        focusedBorderColor = TealLight,
                        unfocusedBorderColor = CharcoalBorder
                    ),
                    modifier = Modifier.fillMaxWidth()
                )

                OutlinedTextField(
                    value = dueDate,
                    onValueChange = { dueDate = it },
                    label = { Text("Due Date (YYYY-MM-DD)") },
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = Color.White,
                        unfocusedTextColor = Color.White,
                        focusedBorderColor = TealLight,
                        unfocusedBorderColor = CharcoalBorder
                    ),
                    modifier = Modifier.fillMaxWidth()
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End
                ) {
                    OutlinedButton(
                        onClick = onDismiss,
                        shape = RoundedCornerShape(8.dp),
                        contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp)
                    ) {
                        Text("Cancel", color = SlateLight, maxLines = 1, softWrap = false)
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    Button(
                        onClick = {
                            selectedAssoc?.let { assoc ->
                                if (title.isNotBlank()) {
                                    onAdd(assoc, selectedStream, title, dueDate, detail)
                                }
                            }
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = AlertCrimson, contentColor = Color.White),
                        shape = RoundedCornerShape(8.dp),
                        contentPadding = PaddingValues(horizontal = 14.dp, vertical = 6.dp)
                    ) {
                        Text("Add Requirement", fontWeight = FontWeight.Bold, maxLines = 1, softWrap = false)
                    }
                }
            }
        }
    }
}
