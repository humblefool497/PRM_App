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
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.Assignment
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.ExpandLess
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material.icons.filled.Gavel
import androidx.compose.material.icons.filled.NotificationsActive
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.PriorityHigh
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
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
import com.example.data.local.entity.ActionItemEntity
import com.example.data.local.entity.ActionItemStatus
import com.example.data.local.entity.AssociateEntity
import com.example.data.local.entity.GovernanceCallEntity
import com.example.ui.OpsViewModel
import com.example.ui.components.ActionStatusBadge
import com.example.ui.components.ExportReportButton
import com.example.ui.components.PriorityBadge
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
fun GovernanceScreen(viewModel: OpsViewModel) {
    var selectedSubTab by remember { mutableIntStateOf(0) }
    val tabs = listOf("Action Items & Reminders", "Governance & Escalations")

    var showAddActionItemDialog by remember { mutableStateOf(false) }
    var showAddGovernanceCallDialog by remember { mutableStateOf(false) }

    val associates by viewModel.allAssociates.collectAsStateWithLifecycle()
    val governanceCalls by viewModel.governanceCalls.collectAsStateWithLifecycle()

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
                        text = "Governance & Actions",
                        color = Color.White,
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Black
                    )
                    Text(
                        text = "SteerCo Logs & Delegated Actions",
                        color = SlateLight,
                        fontSize = 11.sp,
                        maxLines = 1
                    )
                }

                ExportReportButton(
                    label = "Export",
                    onClick = { viewModel.generateGovernanceReport() },
                    testTag = "generate_governance_report_top_button"
                )
            }

            // SubTabs
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

            when (selectedSubTab) {
                0 -> ActionItemsSubView(viewModel)
                1 -> GovernanceLogsSubView(viewModel)
            }
        }

        // FAB
        FloatingActionButton(
            onClick = {
                if (selectedSubTab == 0) showAddActionItemDialog = true else showAddGovernanceCallDialog = true
            },
            containerColor = TealLight,
            contentColor = Color(0xFF0F172A),
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(16.dp)
                .testTag("governance_add_fab")
        ) {
            Icon(Icons.Default.Add, contentDescription = "Add Item")
        }
    }

    if (showAddActionItemDialog) {
        AddActionItemDialog(
            associates = associates,
            governanceCalls = governanceCalls,
            onDismiss = { showAddActionItemDialog = false },
            onAdd = { title, desc, assoc, dueDate, priority, call ->
                viewModel.addActionItem(title, desc, assoc, dueDate, priority, call)
                showAddActionItemDialog = false
            }
        )
    }

    if (showAddGovernanceCallDialog) {
        AddGovernanceCallDialog(
            onDismiss = { showAddGovernanceCallDialog = false },
            onAdd = { title, stakeholder, callType, severity, minutes ->
                viewModel.addGovernanceCall(title, stakeholder, callType, severity, minutes)
                showAddGovernanceCallDialog = false
            }
        )
    }
}

/**
 * Action Items & Smart Reminders SubView
 */
@Composable
private fun ActionItemsSubView(viewModel: OpsViewModel) {
    val actionItems by viewModel.actionItems.collectAsStateWithLifecycle()
    val associates by viewModel.allAssociates.collectAsStateWithLifecycle()

    var statusFilter by remember { mutableStateOf("All") }
    var selectedAssignee by remember { mutableStateOf<String?>(null) }

    val filtered = actionItems.filter { item ->
        val matchesStatus = when (statusFilter) {
            "Open" -> item.status == ActionItemStatus.OPEN
            "In Progress" -> item.status == ActionItemStatus.IN_PROGRESS
            "Resolved" -> item.status == ActionItemStatus.RESOLVED
            else -> true
        }
        val matchesAssignee = selectedAssignee == null || item.assignedAssociateName.equals(selectedAssignee, ignoreCase = true)
        matchesStatus && matchesAssignee
    }

    val openCount = actionItems.count { it.status == ActionItemStatus.OPEN }
    val inProgressCount = actionItems.count { it.status == ActionItemStatus.IN_PROGRESS }
    val resolvedCount = actionItems.count { it.status == ActionItemStatus.RESOLVED }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        item {
            Spacer(modifier = Modifier.height(10.dp))
            // Quick Status Pill Filter
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                listOf("All", "Open", "In Progress", "Resolved").forEach { status ->
                    val isSelected = statusFilter == status
                    val count = when (status) {
                        "Open" -> openCount
                        "In Progress" -> inProgressCount
                        "Resolved" -> resolvedCount
                        else -> actionItems.size
                    }
                    Surface(
                        modifier = Modifier
                            .clip(RoundedCornerShape(8.dp))
                            .clickable { statusFilter = status },
                        color = if (isSelected) TealContainer else Color(0xFF1E293B),
                        shape = RoundedCornerShape(8.dp),
                        border = androidx.compose.foundation.BorderStroke(
                            1.dp,
                            if (isSelected) TealAccent else CharcoalBorder
                        )
                    ) {
                        Text(
                            text = "$status ($count)",
                            color = if (isSelected) TealAccent else SlateLight,
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

        items(filtered) { item ->
            Card(
                colors = CardDefaults.cardColors(containerColor = CharcoalCard),
                shape = RoundedCornerShape(12.dp),
                border = androidx.compose.foundation.BorderStroke(
                    1.dp,
                    if (item.priority == "High" && item.status != ActionItemStatus.RESOLVED) AlertCrimson.copy(alpha = 0.5f) else CharcoalBorder
                )
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            PriorityBadge(item.priority)
                            Spacer(modifier = Modifier.width(6.dp))
                            ActionStatusBadge(item.status)
                        }

                        IconButton(
                            onClick = { viewModel.cycleActionItemStatus(item) },
                            modifier = Modifier.size(32.dp)
                        ) {
                            Icon(
                                imageVector = if (item.status == ActionItemStatus.RESOLVED) Icons.Default.Check else Icons.Default.NotificationsActive,
                                contentDescription = "Cycle Status",
                                tint = if (item.status == ActionItemStatus.RESOLVED) AlertGreen else TealAccent,
                                modifier = Modifier.size(18.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(6.dp))

                    Text(
                        text = item.title,
                        color = Color.White,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold
                    )

                    if (item.description.isNotBlank()) {
                        Text(
                            text = item.description,
                            color = SlateLight,
                            fontSize = 12.sp,
                            lineHeight = 16.sp,
                            modifier = Modifier.padding(top = 2.dp)
                        )
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.Person, contentDescription = null, tint = TealAccent, modifier = Modifier.size(14.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = "Assignee: ${item.assignedAssociateName}",
                                color = Color(0xFF38BDF8),
                                fontSize = 11.sp,
                                fontWeight = FontWeight.SemiBold
                            )
                        }

                        Text(
                            text = "Due: ${item.dueDate}",
                            color = SlateMuted,
                            fontSize = 11.sp
                        )
                    }

                    if (!item.governanceTitle.isNullOrBlank()) {
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "Linked Call: ${item.governanceTitle}",
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

/**
 * Governance Logs SubView
 */
@Composable
private fun GovernanceLogsSubView(viewModel: OpsViewModel) {
    val governanceCalls by viewModel.governanceCalls.collectAsStateWithLifecycle()

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        item {
            Spacer(modifier = Modifier.height(10.dp))
            Text(
                text = "Client Governance & SteerCo Calls (${governanceCalls.size})",
                color = Color.White,
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = "Log client complaints, escalation bridges, and executive meeting minutes",
                color = SlateLight,
                fontSize = 12.sp
            )
        }

        items(governanceCalls) { call ->
            var isExpanded by remember { mutableStateOf(false) }

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
                        Surface(
                            color = if (call.severity == "Critical") AlertCrimsonBg else TealContainer,
                            shape = RoundedCornerShape(6.dp)
                        ) {
                            Text(
                                text = "${call.callType} • ${call.severity}",
                                color = if (call.severity == "Critical") AlertCrimson else TealAccent,
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                            )
                        }
                        Text(
                            text = call.date,
                            color = SlateLight,
                            fontSize = 11.sp
                        )
                    }

                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = call.title,
                        color = Color.White,
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "Stakeholder: ${call.clientStakeholder}",
                        color = Color(0xFF38BDF8),
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Medium
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { isExpanded = !isExpanded },
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = if (isExpanded) "Hide Minutes" else "View Meeting Minutes",
                            color = TealAccent,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Icon(
                            imageVector = if (isExpanded) Icons.Default.ExpandLess else Icons.Default.ExpandMore,
                            contentDescription = null,
                            tint = TealAccent,
                            modifier = Modifier.size(18.dp)
                        )
                    }

                    AnimatedVisibility(visible = isExpanded) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(top = 8.dp)
                                .background(Color(0xFF090D14), RoundedCornerShape(8.dp))
                                .padding(12.dp)
                        ) {
                            Text(
                                text = "Minutes & Escalation Log:",
                                color = SlateLight,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = call.meetingMinutes,
                                color = Color(0xFFE2E8F0),
                                fontSize = 12.sp,
                                lineHeight = 17.sp
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
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun AddActionItemDialog(
    associates: List<AssociateEntity>,
    governanceCalls: List<GovernanceCallEntity>,
    onDismiss: () -> Unit,
    onAdd: (title: String, desc: String, assoc: AssociateEntity?, dueDate: String, priority: String, call: GovernanceCallEntity?) -> Unit
) {
    var title by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var selectedAssoc by remember { mutableStateOf(associates.firstOrNull()) }
    var selectedCall by remember { mutableStateOf<GovernanceCallEntity?>(null) }
    var dueDate by remember { mutableStateOf("2026-11-05") }
    var priority by remember { mutableStateOf("High") }

    var assocExpanded by remember { mutableStateOf(false) }
    var priorityExpanded by remember { mutableStateOf(false) }
    var callExpanded by remember { mutableStateOf(false) }

    val priorities = listOf("High", "Medium", "Low")

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
                Text("Create Delegated Action Item", color = Color.White, fontSize = 18.sp, fontWeight = FontWeight.Bold)

                OutlinedTextField(
                    value = title,
                    onValueChange = { title = it },
                    label = { Text("Action Item Title") },
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = Color.White,
                        unfocusedTextColor = Color.White,
                        focusedBorderColor = TealLight,
                        unfocusedBorderColor = CharcoalBorder
                    ),
                    modifier = Modifier.fillMaxWidth().testTag("action_title_input")
                )

                OutlinedTextField(
                    value = description,
                    onValueChange = { description = it },
                    label = { Text("Action Description / Steps") },
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = Color.White,
                        unfocusedTextColor = Color.White,
                        focusedBorderColor = TealLight,
                        unfocusedBorderColor = CharcoalBorder
                    ),
                    modifier = Modifier.fillMaxWidth()
                )

                // Assignee picker
                ExposedDropdownMenuBox(
                    expanded = assocExpanded,
                    onExpandedChange = { assocExpanded = !assocExpanded }
                ) {
                    OutlinedTextField(
                        value = selectedAssoc?.name ?: "Select Assignee",
                        onValueChange = {},
                        readOnly = true,
                        label = { Text("Assignee (Associate)") },
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
                                text = { Text("${assoc.name} (${assoc.designation})") },
                                onClick = {
                                    selectedAssoc = assoc
                                    assocExpanded = false
                                }
                            )
                        }
                    }
                }

                // Priority picker
                ExposedDropdownMenuBox(
                    expanded = priorityExpanded,
                    onExpandedChange = { priorityExpanded = !priorityExpanded }
                ) {
                    OutlinedTextField(
                        value = priority,
                        onValueChange = {},
                        readOnly = true,
                        label = { Text("Priority Level") },
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = priorityExpanded) },
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedTextColor = Color.White,
                            unfocusedTextColor = Color.White,
                            focusedBorderColor = TealLight,
                            unfocusedBorderColor = CharcoalBorder
                        ),
                        modifier = Modifier.fillMaxWidth().menuAnchor()
                    )
                    ExposedDropdownMenu(
                        expanded = priorityExpanded,
                        onDismissRequest = { priorityExpanded = false }
                    ) {
                        priorities.forEach { p ->
                            DropdownMenuItem(
                                text = { Text(p) },
                                onClick = {
                                    priority = p
                                    priorityExpanded = false
                                }
                            )
                        }
                    }
                }

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
                            if (title.isNotBlank()) {
                                onAdd(title, description, selectedAssoc, dueDate, priority, selectedCall)
                            }
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = TealLight, contentColor = Color(0xFF0F172A)),
                        shape = RoundedCornerShape(8.dp),
                        contentPadding = PaddingValues(horizontal = 14.dp, vertical = 6.dp),
                        modifier = Modifier.testTag("submit_action_button")
                    ) {
                        Text("Save Action", fontWeight = FontWeight.Bold, maxLines = 1, softWrap = false)
                    }
                }
            }
        }
    }
}

@Composable
private fun AddGovernanceCallDialog(
    onDismiss: () -> Unit,
    onAdd: (title: String, stakeholder: String, callType: String, severity: String, minutes: String) -> Unit
) {
    var title by remember { mutableStateOf("") }
    var stakeholder by remember { mutableStateOf("") }
    var callType by remember { mutableStateOf("Steering Committee") }
    var severity by remember { mutableStateOf("High") }
    var minutes by remember { mutableStateOf("") }

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
                Text("Log Governance Call / Complaint", color = Color.White, fontSize = 18.sp, fontWeight = FontWeight.Bold)

                OutlinedTextField(
                    value = title,
                    onValueChange = { title = it },
                    label = { Text("Call / Escalation Title") },
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = Color.White,
                        unfocusedTextColor = Color.White,
                        focusedBorderColor = TealLight,
                        unfocusedBorderColor = CharcoalBorder
                    ),
                    modifier = Modifier.fillMaxWidth().testTag("gov_title_input")
                )

                OutlinedTextField(
                    value = stakeholder,
                    onValueChange = { stakeholder = it },
                    label = { Text("Client Stakeholder Name & Role") },
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = Color.White,
                        unfocusedTextColor = Color.White,
                        focusedBorderColor = TealLight,
                        unfocusedBorderColor = CharcoalBorder
                    ),
                    modifier = Modifier.fillMaxWidth()
                )

                OutlinedTextField(
                    value = minutes,
                    onValueChange = { minutes = it },
                    label = { Text("Meeting Minutes & Action Agreed") },
                    minLines = 3,
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
                            if (title.isNotBlank()) {
                                onAdd(title, stakeholder, callType, severity, minutes)
                            }
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = TealLight, contentColor = Color(0xFF0F172A)),
                        shape = RoundedCornerShape(8.dp),
                        contentPadding = PaddingValues(horizontal = 14.dp, vertical = 6.dp)
                    ) {
                        Text("Save Call Log", fontWeight = FontWeight.Bold, maxLines = 1, softWrap = false)
                    }
                }
            }
        }
    }
}
