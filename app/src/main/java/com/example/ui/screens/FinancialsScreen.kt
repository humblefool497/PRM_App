package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.AssignmentTurnedIn
import androidx.compose.material.icons.filled.AttachMoney
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.ExpandLess
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material.icons.filled.Flight
import androidx.compose.material.icons.filled.HourglassBottom
import androidx.compose.material.icons.filled.Key
import androidx.compose.material.icons.filled.LocalAtm
import androidx.compose.material.icons.filled.MoreHoriz
import androidx.compose.material.icons.filled.PendingActions
import androidx.compose.material.icons.filled.PostAdd
import androidx.compose.material.icons.filled.ReceiptLong
import androidx.compose.material.icons.filled.Storage
import androidx.compose.material.icons.filled.TrendingUp
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
import androidx.compose.material3.LinearProgressIndicator
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
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.data.local.entity.ExpenseCategory
import com.example.data.local.entity.ExpenseEntity
import com.example.data.local.entity.MilestoneEntity
import com.example.data.local.entity.SowAmendmentEntity
import com.example.data.local.entity.SowEntity
import com.example.ui.OpsViewModel
import com.example.ui.components.ExportReportButton
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
import java.text.NumberFormat
import java.util.Locale

@Composable
fun FinancialsScreen(viewModel: OpsViewModel) {
    var selectedSubTab by remember { mutableIntStateOf(0) }
    val tabs = listOf("Projections", "SOWs & Amendments", "Milestones Matrix", "Expense Log")

    var showAddSowDialog by remember { mutableStateOf(false) }
    var showAddAmendmentDialog by remember { mutableStateOf(false) }
    var showAddMilestoneDialog by remember { mutableStateOf(false) }
    var showAddExpenseDialog by remember { mutableStateOf(false) }

    val sows by viewModel.sows.collectAsStateWithLifecycle()

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
                        text = "Project Financials",
                        color = Color.White,
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Black
                    )
                    Text(
                        text = "SOW Contracts, Milestones & Margins",
                        color = SlateLight,
                        fontSize = 11.sp,
                        maxLines = 1
                    )
                }

                ExportReportButton(
                    label = "Fin Report",
                    onClick = { viewModel.generateFinancialReport() },
                    testTag = "generate_financial_report_top_button"
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
                                color = if (selectedSubTab == index) TealLight else SlateLight,
                                maxLines = 1,
                                softWrap = false
                            )
                        }
                    )
                }
            }

            when (selectedSubTab) {
                0 -> ProjectionsDashboardSubView(viewModel)
                1 -> SowsSubView(viewModel, onAddAmendment = { showAddAmendmentDialog = true })
                2 -> MilestonesSubView(viewModel)
                3 -> ExpenseLogSubView(viewModel)
            }
        }

        // Floating Action Button
        FloatingActionButton(
            onClick = {
                when (selectedSubTab) {
                    0, 1 -> showAddSowDialog = true
                    2 -> showAddMilestoneDialog = true
                    3 -> showAddExpenseDialog = true
                }
            },
            containerColor = TealLight,
            contentColor = Color(0xFF0F172A),
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(16.dp)
                .testTag("financials_add_fab")
        ) {
            Icon(Icons.Default.Add, contentDescription = "Add Item")
        }
    }

    if (showAddSowDialog) {
        AddSowDialog(
            onDismiss = { showAddSowDialog = false },
            onAdd = { num, title, client, value, start, end ->
                viewModel.addSow(num, title, client, value, start, end)
                showAddSowDialog = false
            }
        )
    }

    if (showAddAmendmentDialog) {
        AddAmendmentDialog(
            sows = sows,
            onDismiss = { showAddAmendmentDialog = false },
            onAdd = { sow, num, summary, impact, ext ->
                viewModel.addAmendment(sow, num, summary, impact, ext)
                showAddAmendmentDialog = false
            }
        )
    }

    if (showAddMilestoneDialog) {
        AddMilestoneDialog(
            sows = sows,
            onDismiss = { showAddMilestoneDialog = false },
            onAdd = { sow, name, target, quarter, amount, trigger ->
                viewModel.addMilestone(sow, name, target, quarter, amount, trigger)
                showAddMilestoneDialog = false
            }
        )
    }

    if (showAddExpenseDialog) {
        AddExpenseDialog(
            sows = sows,
            onDismiss = { showAddExpenseDialog = false },
            onAdd = { sow, category, amount, desc, inv ->
                viewModel.addExpense(sow, category, amount, desc, inv)
                showAddExpenseDialog = false
            }
        )
    }
}

/**
 * SubView 0: Quarterly Projections Dashboard
 */
@Composable
private fun ProjectionsDashboardSubView(viewModel: OpsViewModel) {
    val metrics by viewModel.dashboardMetrics.collectAsStateWithLifecycle()
    val milestones by viewModel.milestones.collectAsStateWithLifecycle()
    val expenses by viewModel.expenses.collectAsStateWithLifecycle()

    val currencyFormatter = NumberFormat.getCurrencyInstance(Locale.US).apply {
        maximumFractionDigits = 0
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        item {
            Spacer(modifier = Modifier.height(10.dp))
            // Analytical KPI Card
            Card(
                colors = CardDefaults.cardColors(containerColor = CharcoalCard),
                shape = RoundedCornerShape(12.dp),
                border = androidx.compose.foundation.BorderStroke(1.dp, CharcoalBorder)
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    // Header row with active quarter indicator
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.TrendingUp, contentDescription = null, tint = TealAccent, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "Current Quarter (Q4 2026)",
                                color = Color.White,
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold,
                                maxLines = 1,
                                softWrap = false
                            )
                        }

                        Surface(
                            color = TealContainer,
                            shape = RoundedCornerShape(4.dp)
                        ) {
                            Text(
                                text = "ACTIVE Q4",
                                color = TealAccent,
                                fontSize = 9.sp,
                                fontWeight = FontWeight.Bold,
                                maxLines = 1,
                                softWrap = false,
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    // Primary Milestone Earnings vs Projected Revenue Box
                    Surface(
                        modifier = Modifier.fillMaxWidth(),
                        color = Color(0xFF141C27),
                        shape = RoundedCornerShape(10.dp),
                        border = androidx.compose.foundation.BorderStroke(1.dp, CharcoalBorder)
                    ) {
                        Column(modifier = Modifier.padding(12.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column(modifier = Modifier.weight(1f, fill = false)) {
                                    Text(
                                        text = "Milestones Achieved",
                                        color = SlateMuted,
                                        fontSize = 11.sp,
                                        maxLines = 1,
                                        softWrap = false
                                    )
                                    Spacer(modifier = Modifier.height(2.dp))
                                    Text(
                                        text = currencyFormatter.format(metrics.q4EarningsAchieved),
                                        color = TealAccent,
                                        fontSize = 22.sp,
                                        fontWeight = FontWeight.Black,
                                        maxLines = 1,
                                        softWrap = false
                                    )
                                }

                                Surface(
                                    color = Color(0xFF1E293B),
                                    shape = RoundedCornerShape(6.dp)
                                ) {
                                    Column(
                                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                                        horizontalAlignment = Alignment.End
                                    ) {
                                        Text(
                                            text = "Target Revenue",
                                            color = SlateMuted,
                                            fontSize = 10.sp,
                                            maxLines = 1,
                                            softWrap = false
                                        )
                                        Text(
                                            text = currencyFormatter.format(metrics.q4ProjectedTotal),
                                            color = Color.White,
                                            fontSize = 13.sp,
                                            fontWeight = FontWeight.Bold,
                                            maxLines = 1,
                                            softWrap = false
                                        )
                                    }
                                }
                            }

                            Spacer(modifier = Modifier.height(10.dp))

                            val progress = if (metrics.q4ProjectedTotal > 0) (metrics.q4EarningsAchieved / metrics.q4ProjectedTotal).toFloat().coerceIn(0f, 1f) else 0f

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = "Target Completion",
                                    color = SlateLight,
                                    fontSize = 10.sp,
                                    maxLines = 1,
                                    softWrap = false
                                )
                                Text(
                                    text = "${(progress * 100).toInt()}% Achieved",
                                    color = TealAccent,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    maxLines = 1,
                                    softWrap = false
                                )
                            }

                            Spacer(modifier = Modifier.height(4.dp))

                            LinearProgressIndicator(
                                progress = { progress },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(7.dp)
                                    .clip(RoundedCornerShape(4.dp)),
                                color = TealLight,
                                trackColor = Color(0xFF0F172A)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    // 2-Column Metrics (Expenses & Margin)
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Surface(
                            modifier = Modifier.weight(1f),
                            color = Color(0xFF141C27),
                            shape = RoundedCornerShape(8.dp),
                            border = androidx.compose.foundation.BorderStroke(1.dp, CharcoalBorder)
                        ) {
                            Column(modifier = Modifier.padding(10.dp)) {
                                Text("Op. Expenses", color = SlateMuted, fontSize = 11.sp, maxLines = 1, softWrap = false)
                                Spacer(modifier = Modifier.height(2.dp))
                                Text(
                                    text = currencyFormatter.format(metrics.totalExpenses),
                                    color = AlertCrimson,
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Bold,
                                    maxLines = 1,
                                    softWrap = false
                                )
                            }
                        }

                        Surface(
                            modifier = Modifier.weight(1f),
                            color = Color(0xFF141C27),
                            shape = RoundedCornerShape(8.dp),
                            border = androidx.compose.foundation.BorderStroke(1.dp, CharcoalBorder)
                        ) {
                            Column(modifier = Modifier.padding(10.dp)) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text("Net Margin", color = SlateMuted, fontSize = 11.sp, maxLines = 1, softWrap = false)
                                    Text(
                                        text = "${String.format(Locale.US, "%.0f", metrics.marginPct)}%",
                                        color = AlertGreen,
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold,
                                        maxLines = 1,
                                        softWrap = false
                                    )
                                }
                                Spacer(modifier = Modifier.height(2.dp))
                                Text(
                                    text = currencyFormatter.format(metrics.netOperatingMargin),
                                    color = AlertGreen,
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Bold,
                                    maxLines = 1,
                                    softWrap = false
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    // Pipeline Forecast Banner
                    Surface(
                        modifier = Modifier.fillMaxWidth(),
                        color = Color(0xFF0F2338),
                        shape = RoundedCornerShape(8.dp),
                        border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF1E3A5F))
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 12.dp, vertical = 8.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(modifier = Modifier.weight(1f, fill = false)) {
                                Text("Q1 2027 Pipeline Forecast", color = Color(0xFF94A3B8), fontSize = 11.sp, fontWeight = FontWeight.SemiBold, maxLines = 1, softWrap = false)
                                Text("Based on active SOW structures", color = SlateMuted, fontSize = 10.sp, maxLines = 1, softWrap = false)
                            }
                            Text(
                                text = currencyFormatter.format(metrics.q1Forecast),
                                color = Color(0xFF38BDF8),
                                fontSize = 14.sp,
                                fontWeight = FontWeight.ExtraBold,
                                maxLines = 1,
                                softWrap = false
                            )
                        }
                    }
                }
            }
        }

        // Expense breakdown by category
        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = CharcoalCard),
                shape = RoundedCornerShape(12.dp),
                border = androidx.compose.foundation.BorderStroke(1.dp, CharcoalBorder)
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Text(
                        text = "EXPENSES BY CATEGORY",
                        color = SlateLight,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 0.5.sp
                    )
                    Spacer(modifier = Modifier.height(10.dp))

                    val infraExp = expenses.filter { it.category == ExpenseCategory.INFRASTRUCTURE }.sumOf { it.amount }
                    val licExp = expenses.filter { it.category == ExpenseCategory.SOFTWARE_LICENSES }.sumOf { it.amount }
                    val trvExp = expenses.filter { it.category == ExpenseCategory.TRAVEL }.sumOf { it.amount }
                    val miscExp = expenses.filter { it.category == ExpenseCategory.MISCELLANEOUS }.sumOf { it.amount }

                    ExpenseCategoryRow("Cloud Infrastructure", currencyFormatter.format(infraExp), Icons.Default.Storage, TealAccent)
                    ExpenseCategoryRow("Software Licenses", currencyFormatter.format(licExp), Icons.Default.Key, Color(0xFF38BDF8))
                    ExpenseCategoryRow("Corporate Travel", currencyFormatter.format(trvExp), Icons.Default.Flight, AlertAmber)
                    ExpenseCategoryRow("Miscellaneous", currencyFormatter.format(miscExp), Icons.Default.MoreHoriz, SlateLight)
                }
            }
        }

        item {
            Spacer(modifier = Modifier.height(80.dp))
        }
    }
}

@Composable
private fun ExpenseCategoryRow(label: String, amount: String, icon: ImageVector, color: Color) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 6.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(
            modifier = Modifier.weight(1f, fill = false),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(icon, contentDescription = null, tint = color, modifier = Modifier.size(16.dp))
            Spacer(modifier = Modifier.width(8.dp))
            Text(label, color = Color.White, fontSize = 13.sp, maxLines = 1, softWrap = false)
        }
        Text(amount, color = color, fontSize = 13.sp, fontWeight = FontWeight.Bold, maxLines = 1, softWrap = false)
    }
}

/**
 * SubView 1: SOWs & Amendments
 */
@Composable
private fun SowsSubView(
    viewModel: OpsViewModel,
    onAddAmendment: () -> Unit
) {
    val sows by viewModel.sows.collectAsStateWithLifecycle()
    val amendments by viewModel.amendments.collectAsStateWithLifecycle()

    var sowToEdit by remember { mutableStateOf<SowEntity?>(null) }
    var sowToDelete by remember { mutableStateOf<SowEntity?>(null) }
    var amendmentToEdit by remember { mutableStateOf<SowAmendmentEntity?>(null) }
    var amendmentToDelete by remember { mutableStateOf<SowAmendmentEntity?>(null) }

    val currencyFormatter = NumberFormat.getCurrencyInstance(Locale.US).apply {
        maximumFractionDigits = 0
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        item {
            Spacer(modifier = Modifier.height(10.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Active SOW Portfolio (${sows.size})",
                    color = Color.White,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold
                )
                OutlinedButton(
                    onClick = onAddAmendment,
                    shape = RoundedCornerShape(8.dp),
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = TealAccent),
                    contentPadding = PaddingValues(horizontal = 10.dp, vertical = 6.dp)
                ) {
                    Icon(Icons.Default.PostAdd, contentDescription = null, modifier = Modifier.size(15.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("+ Amendment", fontSize = 11.sp, maxLines = 1, softWrap = false)
                }
            }
        }

        items(sows) { sow ->
            val sowAmds = amendments.filter { it.sowId == sow.id }
            val amdTotal = sowAmds.sumOf { it.financialImpact }
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
                            color = TealContainer,
                            shape = RoundedCornerShape(4.dp)
                        ) {
                            Text(
                                text = sow.sowNumber,
                                color = TealAccent,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                            )
                        }

                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Surface(
                                color = if (sow.status == "Active") Color(0xFF064E3B) else Color(0xFF451A03),
                                shape = RoundedCornerShape(4.dp)
                            ) {
                                Text(
                                    text = sow.status,
                                    color = if (sow.status == "Active") AlertGreen else AlertAmber,
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold,
                                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                )
                            }
                            Spacer(modifier = Modifier.width(4.dp))
                            IconButton(
                                onClick = { sowToEdit = sow },
                                modifier = Modifier.size(32.dp).testTag("edit_sow_${sow.id}")
                            ) {
                                Icon(Icons.Default.Edit, contentDescription = "Edit SOW", tint = TealAccent, modifier = Modifier.size(16.dp))
                            }
                            IconButton(
                                onClick = { sowToDelete = sow },
                                modifier = Modifier.size(32.dp).testTag("delete_sow_${sow.id}")
                            ) {
                                Icon(Icons.Default.Delete, contentDescription = "Delete SOW", tint = AlertCrimson, modifier = Modifier.size(16.dp))
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = sow.title,
                        color = Color.White,
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "Client: ${sow.clientName} • Dates: ${sow.startDate} to ${sow.endDate}",
                        color = SlateLight,
                        fontSize = 11.sp
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text("Base Value", color = SlateMuted, fontSize = 10.sp)
                            Text(currencyFormatter.format(sow.totalValue), color = Color.White, fontSize = 13.sp, fontWeight = FontWeight.Bold)
                        }
                        Column {
                            Text("Signed Amendments", color = SlateMuted, fontSize = 10.sp)
                            Text("+${currencyFormatter.format(amdTotal)}", color = TealAccent, fontSize = 13.sp, fontWeight = FontWeight.Bold)
                        }
                        Column(horizontalAlignment = Alignment.End) {
                            Text("Adjusted Contract", color = SlateMuted, fontSize = 10.sp)
                            Text(currencyFormatter.format(sow.totalValue + amdTotal), color = AlertGreen, fontSize = 13.sp, fontWeight = FontWeight.Bold)
                        }
                    }

                    if (sowAmds.isNotEmpty()) {
                        Spacer(modifier = Modifier.height(10.dp))
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { isExpanded = !isExpanded },
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "${sowAmds.size} Signed Amendment(s)",
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
                                    .padding(10.dp),
                                verticalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                sowAmds.forEach { amd ->
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Column(modifier = Modifier.weight(1f).padding(end = 4.dp)) {
                                            Text(
                                                text = "${amd.amendmentNumber} (${amd.signedDate})",
                                                color = Color.White,
                                                fontSize = 11.sp,
                                                fontWeight = FontWeight.Bold
                                            )
                                            Text(
                                                text = amd.summary,
                                                color = SlateLight,
                                                fontSize = 11.sp
                                            )
                                        }
                                        Row(verticalAlignment = Alignment.CenterVertically) {
                                            Text(
                                                text = "+${currencyFormatter.format(amd.financialImpact)}",
                                                color = TealAccent,
                                                fontSize = 11.sp,
                                                fontWeight = FontWeight.Bold
                                            )
                                            IconButton(
                                                onClick = { amendmentToEdit = amd },
                                                modifier = Modifier.size(28.dp).testTag("edit_amd_${amd.id}")
                                            ) {
                                                Icon(Icons.Default.Edit, contentDescription = "Edit Amendment", tint = TealAccent, modifier = Modifier.size(14.dp))
                                            }
                                            IconButton(
                                                onClick = { amendmentToDelete = amd },
                                                modifier = Modifier.size(28.dp).testTag("delete_amd_${amd.id}")
                                            ) {
                                                Icon(Icons.Default.Delete, contentDescription = "Delete Amendment", tint = AlertCrimson, modifier = Modifier.size(14.dp))
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }

        item {
            Spacer(modifier = Modifier.height(80.dp))
        }
    }

    sowToEdit?.let { sow ->
        EditSowDialog(
            sow = sow,
            onDismiss = { sowToEdit = null },
            onSave = { updated ->
                viewModel.updateSow(updated)
                sowToEdit = null
            }
        )
    }

    sowToDelete?.let { sow ->
        FinancialDeleteConfirmDialog(
            title = "Delete Statement of Work",
            message = "Are you sure you want to delete ${sow.sowNumber} (${sow.title})? All linked amendments, milestones, and operational expenses will also be permanently deleted.",
            onDismiss = { sowToDelete = null },
            onConfirm = {
                viewModel.deleteSow(sow)
                sowToDelete = null
            }
        )
    }

    amendmentToEdit?.let { amd ->
        EditAmendmentDialog(
            amendment = amd,
            onDismiss = { amendmentToEdit = null },
            onSave = { updated ->
                viewModel.updateAmendment(updated)
                amendmentToEdit = null
            }
        )
    }

    amendmentToDelete?.let { amd ->
        FinancialDeleteConfirmDialog(
            title = "Delete SOW Amendment",
            message = "Are you sure you want to delete ${amd.amendmentNumber} (+${currencyFormatter.format(amd.financialImpact)}) from ${amd.sowNumber}?",
            onDismiss = { amendmentToDelete = null },
            onConfirm = {
                viewModel.deleteAmendment(amd)
                amendmentToDelete = null
            }
        )
    }
}

/**
 * SubView 2: Milestones Matrix
 */
@Composable
private fun MilestonesSubView(viewModel: OpsViewModel) {
    val milestones by viewModel.milestones.collectAsStateWithLifecycle()
    var milestoneToEdit by remember { mutableStateOf<MilestoneEntity?>(null) }
    var milestoneToDelete by remember { mutableStateOf<MilestoneEntity?>(null) }

    val currencyFormatter = NumberFormat.getCurrencyInstance(Locale.US).apply {
        maximumFractionDigits = 0
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        item {
            Spacer(modifier = Modifier.height(10.dp))
            Text(
                text = "Milestones & Billing Triggers (${milestones.size})",
                color = Color.White,
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = "Track deliverable project milestones, completion status & client billing gates",
                color = SlateLight,
                fontSize = 12.sp
            )
        }

        items(milestones) { m ->
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
                    Column(modifier = Modifier.weight(1f).padding(end = 6.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Surface(
                                color = when (m.status) {
                                    "Achieved" -> Color(0xFF064E3B)
                                    "In Progress" -> Color(0xFF451A03)
                                    else -> Color(0xFF1E293B)
                                },
                                shape = RoundedCornerShape(4.dp)
                            ) {
                                Text(
                                    text = m.status.uppercase(),
                                    color = when (m.status) {
                                        "Achieved" -> AlertGreen
                                        "In Progress" -> AlertAmber
                                        else -> SlateLight
                                    },
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold,
                                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                )
                            }
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = m.quarter,
                                color = TealAccent,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }

                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = m.name,
                            color = Color.White,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "Billing Trigger: ${m.billingTrigger}",
                            color = Color(0xFF38BDF8),
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Medium
                        )
                        Text(
                            text = "Target Date: ${m.targetDate} • ${m.sowNumber}",
                            color = SlateMuted,
                            fontSize = 11.sp
                        )
                    }

                    Column(horizontalAlignment = Alignment.End) {
                        Text(
                            text = currencyFormatter.format(m.amount),
                            color = Color.White,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.ExtraBold
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Button(
                                onClick = { viewModel.cycleMilestoneStatus(m) },
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = if (m.status == "Achieved") AlertGreen else TealContainer,
                                    contentColor = if (m.status == "Achieved") Color.White else TealAccent
                                ),
                                shape = RoundedCornerShape(6.dp),
                                contentPadding = PaddingValues(horizontal = 8.dp, vertical = 4.dp)
                            ) {
                                Text("Cycle Status", fontSize = 10.sp, fontWeight = FontWeight.Bold, maxLines = 1, softWrap = false)
                            }
                            IconButton(
                                onClick = { milestoneToEdit = m },
                                modifier = Modifier.size(28.dp).testTag("edit_milestone_${m.id}")
                            ) {
                                Icon(Icons.Default.Edit, contentDescription = "Edit Milestone", tint = TealAccent, modifier = Modifier.size(15.dp))
                            }
                            IconButton(
                                onClick = { milestoneToDelete = m },
                                modifier = Modifier.size(28.dp).testTag("delete_milestone_${m.id}")
                            ) {
                                Icon(Icons.Default.Delete, contentDescription = "Delete Milestone", tint = AlertCrimson, modifier = Modifier.size(15.dp))
                            }
                        }
                    }
                }
            }
        }

        item {
            Spacer(modifier = Modifier.height(80.dp))
        }
    }

    milestoneToEdit?.let { m ->
        EditMilestoneDialog(
            milestone = m,
            onDismiss = { milestoneToEdit = null },
            onSave = { updated ->
                viewModel.updateMilestone(updated)
                milestoneToEdit = null
            }
        )
    }

    milestoneToDelete?.let { m ->
        FinancialDeleteConfirmDialog(
            title = "Delete Project Milestone",
            message = "Are you sure you want to delete deliverable milestone '${m.name}' (${currencyFormatter.format(m.amount)}) from ${m.sowNumber}?",
            onDismiss = { milestoneToDelete = null },
            onConfirm = {
                viewModel.deleteMilestone(m)
                milestoneToDelete = null
            }
        )
    }
}

/**
 * SubView 3: Expense Log
 */
@Composable
private fun ExpenseLogSubView(viewModel: OpsViewModel) {
    val expenses by viewModel.expenses.collectAsStateWithLifecycle()
    var expenseToEdit by remember { mutableStateOf<ExpenseEntity?>(null) }
    var expenseToDelete by remember { mutableStateOf<ExpenseEntity?>(null) }

    val totalExp = expenses.sumOf { it.amount }

    val currencyFormatter = NumberFormat.getCurrencyInstance(Locale.US).apply {
        maximumFractionDigits = 0
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        item {
            Spacer(modifier = Modifier.height(10.dp))
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
                    Column {
                        Text("TOTAL OPERATIONAL EXPENSES", color = SlateLight, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                        Text(currencyFormatter.format(totalExp), color = AlertCrimson, fontSize = 20.sp, fontWeight = FontWeight.Black)
                    }
                    Text("${expenses.size} Items Logged", color = SlateLight, fontSize = 12.sp)
                }
            }
        }

        items(expenses) { exp ->
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
                    Column(modifier = Modifier.weight(1f).padding(end = 6.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Surface(
                                color = Color(0xFF1E293B),
                                shape = RoundedCornerShape(4.dp)
                            ) {
                                Text(
                                    text = exp.category,
                                    color = TealAccent,
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold,
                                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                )
                            }
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(exp.date, color = SlateLight, fontSize = 11.sp)
                        }
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = exp.description,
                            color = Color.White,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "Ref: ${exp.invoiceRef} • SOW: ${exp.sowNumber}",
                            color = SlateMuted,
                            fontSize = 11.sp
                        )
                    }

                    Column(horizontalAlignment = Alignment.End) {
                        Text(
                            text = currencyFormatter.format(exp.amount),
                            color = AlertCrimson,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            IconButton(
                                onClick = { expenseToEdit = exp },
                                modifier = Modifier.size(28.dp).testTag("edit_expense_${exp.id}")
                            ) {
                                Icon(Icons.Default.Edit, contentDescription = "Edit Expense", tint = TealAccent, modifier = Modifier.size(15.dp))
                            }
                            IconButton(
                                onClick = { expenseToDelete = exp },
                                modifier = Modifier.size(28.dp).testTag("delete_expense_${exp.id}")
                            ) {
                                Icon(Icons.Default.Delete, contentDescription = "Delete Expense", tint = AlertCrimson, modifier = Modifier.size(15.dp))
                            }
                        }
                    }
                }
            }
        }

        item {
            Spacer(modifier = Modifier.height(80.dp))
        }
    }

    expenseToEdit?.let { exp ->
        EditExpenseDialog(
            expense = exp,
            onDismiss = { expenseToEdit = null },
            onSave = { updated ->
                viewModel.updateExpense(updated)
                expenseToEdit = null
            }
        )
    }

    expenseToDelete?.let { exp ->
        FinancialDeleteConfirmDialog(
            title = "Delete Expense Item",
            message = "Are you sure you want to delete expense record '${exp.description}' (${currencyFormatter.format(exp.amount)})?",
            onDismiss = { expenseToDelete = null },
            onConfirm = {
                viewModel.deleteExpense(exp)
                expenseToDelete = null
            }
        )
    }
}

@Composable
private fun AddSowDialog(
    onDismiss: () -> Unit,
    onAdd: (num: String, title: String, client: String, value: Double, start: String, end: String) -> Unit
) {
    var sowNum by remember { mutableStateOf("SOW-2026-") }
    var title by remember { mutableStateOf("") }
    var client by remember { mutableStateOf("") }
    var valueStr by remember { mutableStateOf("500000") }
    var start by remember { mutableStateOf("2026-01-01") }
    var end by remember { mutableStateOf("2026-12-31") }

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
                Text("Add Statement of Work (SOW)", color = Color.White, fontSize = 18.sp, fontWeight = FontWeight.Bold)

                OutlinedTextField(
                    value = sowNum,
                    onValueChange = { sowNum = it },
                    label = { Text("SOW Identifier (e.g. SOW-2026-BNK-102)") },
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = Color.White,
                        unfocusedTextColor = Color.White,
                        focusedBorderColor = TealLight,
                        unfocusedBorderColor = CharcoalBorder
                    ),
                    modifier = Modifier.fillMaxWidth().testTag("sow_num_input")
                )

                OutlinedTextField(
                    value = title,
                    onValueChange = { title = it },
                    label = { Text("SOW Title / Scope") },
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
                    label = { Text("Client Organization Name") },
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = Color.White,
                        unfocusedTextColor = Color.White,
                        focusedBorderColor = TealLight,
                        unfocusedBorderColor = CharcoalBorder
                    ),
                    modifier = Modifier.fillMaxWidth()
                )

                OutlinedTextField(
                    value = valueStr,
                    onValueChange = { valueStr = it },
                    label = { Text("Total Base Value (USD)") },
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
                            val v = valueStr.toDoubleOrNull() ?: 0.0
                            if (sowNum.isNotBlank() && title.isNotBlank()) {
                                onAdd(sowNum, title, client, v, start, end)
                            }
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = TealLight, contentColor = Color(0xFF0F172A)),
                        shape = RoundedCornerShape(8.dp),
                        contentPadding = PaddingValues(horizontal = 14.dp, vertical = 6.dp)
                    ) {
                        Text("Save SOW", fontWeight = FontWeight.Bold, maxLines = 1, softWrap = false)
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun AddAmendmentDialog(
    sows: List<SowEntity>,
    onDismiss: () -> Unit,
    onAdd: (sow: SowEntity, num: String, summary: String, impact: Double, ext: Int) -> Unit
) {
    var selectedSow by remember { mutableStateOf(sows.firstOrNull()) }
    var amdNum by remember { mutableStateOf("AMD-01") }
    var summary by remember { mutableStateOf("") }
    var impactStr by remember { mutableStateOf("50000") }
    var extMonthsStr by remember { mutableStateOf("2") }

    var sowExpanded by remember { mutableStateOf(false) }

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
                Text("Log Signed SOW Amendment", color = Color.White, fontSize = 18.sp, fontWeight = FontWeight.Bold)

                ExposedDropdownMenuBox(
                    expanded = sowExpanded,
                    onExpandedChange = { sowExpanded = !sowExpanded }
                ) {
                    OutlinedTextField(
                        value = selectedSow?.let { "${it.sowNumber} - ${it.title}" } ?: "Select SOW",
                        onValueChange = {},
                        readOnly = true,
                        label = { Text("Parent SOW") },
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = sowExpanded) },
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedTextColor = Color.White,
                            unfocusedTextColor = Color.White,
                            focusedBorderColor = TealLight,
                            unfocusedBorderColor = CharcoalBorder
                        ),
                        modifier = Modifier.fillMaxWidth().menuAnchor()
                    )
                    ExposedDropdownMenu(
                        expanded = sowExpanded,
                        onDismissRequest = { sowExpanded = false }
                    ) {
                        sows.forEach { sow ->
                            DropdownMenuItem(
                                text = { Text("${sow.sowNumber} - ${sow.title}") },
                                onClick = {
                                    selectedSow = sow
                                    sowExpanded = false
                                }
                            )
                        }
                    }
                }

                OutlinedTextField(
                    value = amdNum,
                    onValueChange = { amdNum = it },
                    label = { Text("Amendment Number (e.g. AMD-02)") },
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = Color.White,
                        unfocusedTextColor = Color.White,
                        focusedBorderColor = TealLight,
                        unfocusedBorderColor = CharcoalBorder
                    ),
                    modifier = Modifier.fillMaxWidth()
                )

                OutlinedTextField(
                    value = summary,
                    onValueChange = { summary = it },
                    label = { Text("Amendment Summary & Scope Impact") },
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = Color.White,
                        unfocusedTextColor = Color.White,
                        focusedBorderColor = TealLight,
                        unfocusedBorderColor = CharcoalBorder
                    ),
                    modifier = Modifier.fillMaxWidth()
                )

                OutlinedTextField(
                    value = impactStr,
                    onValueChange = { impactStr = it },
                    label = { Text("Financial Impact Amount (USD)") },
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
                            selectedSow?.let { s ->
                                val imp = impactStr.toDoubleOrNull() ?: 0.0
                                val ext = extMonthsStr.toIntOrNull() ?: 0
                                if (summary.isNotBlank()) {
                                    onAdd(s, amdNum, summary, imp, ext)
                                }
                            }
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = TealLight, contentColor = Color(0xFF0F172A)),
                        shape = RoundedCornerShape(8.dp),
                        contentPadding = PaddingValues(horizontal = 14.dp, vertical = 6.dp)
                    ) {
                        Text("Save Amendment", fontWeight = FontWeight.Bold, maxLines = 1, softWrap = false)
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun AddMilestoneDialog(
    sows: List<SowEntity>,
    onDismiss: () -> Unit,
    onAdd: (sow: SowEntity, name: String, target: String, quarter: String, amount: Double, trigger: String) -> Unit
) {
    var selectedSow by remember { mutableStateOf(sows.firstOrNull()) }
    var name by remember { mutableStateOf("") }
    var target by remember { mutableStateOf("2026-12-15") }
    var quarter by remember { mutableStateOf("Q4 2026") }
    var amountStr by remember { mutableStateOf("100000") }
    var trigger by remember { mutableStateOf("Client UAT Acceptance") }

    var sowExpanded by remember { mutableStateOf(false) }

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
                Text("Add Project Milestone", color = Color.White, fontSize = 18.sp, fontWeight = FontWeight.Bold)

                ExposedDropdownMenuBox(
                    expanded = sowExpanded,
                    onExpandedChange = { sowExpanded = !sowExpanded }
                ) {
                    OutlinedTextField(
                        value = selectedSow?.let { "${it.sowNumber} - ${it.title}" } ?: "Select SOW",
                        onValueChange = {},
                        readOnly = true,
                        label = { Text("Parent SOW") },
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = sowExpanded) },
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedTextColor = Color.White,
                            unfocusedTextColor = Color.White,
                            focusedBorderColor = TealLight,
                            unfocusedBorderColor = CharcoalBorder
                        ),
                        modifier = Modifier.fillMaxWidth().menuAnchor()
                    )
                    ExposedDropdownMenu(
                        expanded = sowExpanded,
                        onDismissRequest = { sowExpanded = false }
                    ) {
                        sows.forEach { sow ->
                            DropdownMenuItem(
                                text = { Text("${sow.sowNumber} - ${sow.title}") },
                                onClick = {
                                    selectedSow = sow
                                    sowExpanded = false
                                }
                            )
                        }
                    }
                }

                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text("Milestone Deliverable Name") },
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = Color.White,
                        unfocusedTextColor = Color.White,
                        focusedBorderColor = TealLight,
                        unfocusedBorderColor = CharcoalBorder
                    ),
                    modifier = Modifier.fillMaxWidth()
                )

                OutlinedTextField(
                    value = trigger,
                    onValueChange = { trigger = it },
                    label = { Text("Billing Trigger (e.g. UAT Sign-off)") },
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = Color.White,
                        unfocusedTextColor = Color.White,
                        focusedBorderColor = TealLight,
                        unfocusedBorderColor = CharcoalBorder
                    ),
                    modifier = Modifier.fillMaxWidth()
                )

                OutlinedTextField(
                    value = amountStr,
                    onValueChange = { amountStr = it },
                    label = { Text("Milestone Amount (USD)") },
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
                            selectedSow?.let { s ->
                                val amt = amountStr.toDoubleOrNull() ?: 0.0
                                if (name.isNotBlank()) {
                                    onAdd(s, name, target, quarter, amt, trigger)
                                }
                            }
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = TealLight, contentColor = Color(0xFF0F172A)),
                        shape = RoundedCornerShape(8.dp),
                        contentPadding = PaddingValues(horizontal = 14.dp, vertical = 6.dp)
                    ) {
                        Text("Save Milestone", fontWeight = FontWeight.Bold, maxLines = 1, softWrap = false)
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun AddExpenseDialog(
    sows: List<SowEntity>,
    onDismiss: () -> Unit,
    onAdd: (sow: SowEntity, category: String, amount: Double, desc: String, inv: String) -> Unit
) {
    var selectedSow by remember { mutableStateOf(sows.firstOrNull()) }
    var selectedCategory by remember { mutableStateOf(ExpenseCategory.INFRASTRUCTURE) }
    var amountStr by remember { mutableStateOf("15000") }
    var desc by remember { mutableStateOf("") }
    var invRef by remember { mutableStateOf("INV-2026-") }

    var sowExpanded by remember { mutableStateOf(false) }
    var catExpanded by remember { mutableStateOf(false) }

    val categories = listOf(
        ExpenseCategory.INFRASTRUCTURE,
        ExpenseCategory.SOFTWARE_LICENSES,
        ExpenseCategory.TRAVEL,
        ExpenseCategory.MISCELLANEOUS
    )

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
                Text("Log Operational Project Expense", color = Color.White, fontSize = 18.sp, fontWeight = FontWeight.Bold)

                ExposedDropdownMenuBox(
                    expanded = sowExpanded,
                    onExpandedChange = { sowExpanded = !sowExpanded }
                ) {
                    OutlinedTextField(
                        value = selectedSow?.let { "${it.sowNumber} - ${it.title}" } ?: "Select SOW",
                        onValueChange = {},
                        readOnly = true,
                        label = { Text("Attributed SOW") },
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = sowExpanded) },
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedTextColor = Color.White,
                            unfocusedTextColor = Color.White,
                            focusedBorderColor = TealLight,
                            unfocusedBorderColor = CharcoalBorder
                        ),
                        modifier = Modifier.fillMaxWidth().menuAnchor()
                    )
                    ExposedDropdownMenu(
                        expanded = sowExpanded,
                        onDismissRequest = { sowExpanded = false }
                    ) {
                        sows.forEach { sow ->
                            DropdownMenuItem(
                                text = { Text("${sow.sowNumber} - ${sow.title}") },
                                onClick = {
                                    selectedSow = sow
                                    sowExpanded = false
                                }
                            )
                        }
                    }
                }

                ExposedDropdownMenuBox(
                    expanded = catExpanded,
                    onExpandedChange = { catExpanded = !catExpanded }
                ) {
                    OutlinedTextField(
                        value = selectedCategory,
                        onValueChange = {},
                        readOnly = true,
                        label = { Text("Expense Category") },
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = catExpanded) },
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedTextColor = Color.White,
                            unfocusedTextColor = Color.White,
                            focusedBorderColor = TealLight,
                            unfocusedBorderColor = CharcoalBorder
                        ),
                        modifier = Modifier.fillMaxWidth().menuAnchor()
                    )
                    ExposedDropdownMenu(
                        expanded = catExpanded,
                        onDismissRequest = { catExpanded = false }
                    ) {
                        categories.forEach { cat ->
                            DropdownMenuItem(
                                text = { Text(cat) },
                                onClick = {
                                    selectedCategory = cat
                                    catExpanded = false
                                }
                            )
                        }
                    }
                }

                OutlinedTextField(
                    value = amountStr,
                    onValueChange = { amountStr = it },
                    label = { Text("Amount (USD)") },
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = Color.White,
                        unfocusedTextColor = Color.White,
                        focusedBorderColor = TealLight,
                        unfocusedBorderColor = CharcoalBorder
                    ),
                    modifier = Modifier.fillMaxWidth()
                )

                OutlinedTextField(
                    value = desc,
                    onValueChange = { desc = it },
                    label = { Text("Expense Description") },
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
                            selectedSow?.let { s ->
                                val amt = amountStr.toDoubleOrNull() ?: 0.0
                                if (desc.isNotBlank()) {
                                    onAdd(s, selectedCategory, amt, desc, invRef)
                                }
                            }
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = TealLight, contentColor = Color(0xFF0F172A)),
                        shape = RoundedCornerShape(8.dp),
                        contentPadding = PaddingValues(horizontal = 14.dp, vertical = 6.dp)
                    ) {
                        Text("Log Expense", fontWeight = FontWeight.Bold, maxLines = 1, softWrap = false)
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun EditSowDialog(
    sow: SowEntity,
    onDismiss: () -> Unit,
    onSave: (SowEntity) -> Unit
) {
    var sowNum by remember { mutableStateOf(sow.sowNumber) }
    var title by remember { mutableStateOf(sow.title) }
    var client by remember { mutableStateOf(sow.clientName) }
    var valueStr by remember { mutableStateOf(sow.totalValue.toString()) }
    var start by remember { mutableStateOf(sow.startDate) }
    var end by remember { mutableStateOf(sow.endDate) }
    var status by remember { mutableStateOf(sow.status) }
    var statusExpanded by remember { mutableStateOf(false) }

    val statusOptions = listOf("Active", "Extended", "Pending Signature", "Closed")

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
                Text("Edit Statement of Work", color = Color.White, fontSize = 18.sp, fontWeight = FontWeight.Bold)

                OutlinedTextField(
                    value = sowNum,
                    onValueChange = { sowNum = it },
                    label = { Text("SOW Identifier") },
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = Color.White,
                        unfocusedTextColor = Color.White,
                        focusedBorderColor = TealLight,
                        unfocusedBorderColor = CharcoalBorder
                    ),
                    modifier = Modifier.fillMaxWidth().testTag("edit_sow_num_input")
                )

                OutlinedTextField(
                    value = title,
                    onValueChange = { title = it },
                    label = { Text("Scope Title") },
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
                    label = { Text("Client Name") },
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = Color.White,
                        unfocusedTextColor = Color.White,
                        focusedBorderColor = TealLight,
                        unfocusedBorderColor = CharcoalBorder
                    ),
                    modifier = Modifier.fillMaxWidth()
                )

                OutlinedTextField(
                    value = valueStr,
                    onValueChange = { valueStr = it },
                    label = { Text("Total Base Value (USD)") },
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = Color.White,
                        unfocusedTextColor = Color.White,
                        focusedBorderColor = TealLight,
                        unfocusedBorderColor = CharcoalBorder
                    ),
                    modifier = Modifier.fillMaxWidth()
                )

                ExposedDropdownMenuBox(
                    expanded = statusExpanded,
                    onExpandedChange = { statusExpanded = !statusExpanded }
                ) {
                    OutlinedTextField(
                        value = status,
                        onValueChange = {},
                        readOnly = true,
                        label = { Text("Contract Status") },
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = statusExpanded) },
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedTextColor = Color.White,
                            unfocusedTextColor = Color.White,
                            focusedBorderColor = TealLight,
                            unfocusedBorderColor = CharcoalBorder
                        ),
                        modifier = Modifier.fillMaxWidth().menuAnchor()
                    )
                    ExposedDropdownMenu(
                        expanded = statusExpanded,
                        onDismissRequest = { statusExpanded = false }
                    ) {
                        statusOptions.forEach { opt ->
                            DropdownMenuItem(
                                text = { Text(opt) },
                                onClick = {
                                    status = opt
                                    statusExpanded = false
                                }
                            )
                        }
                    }
                }

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
                            val v = valueStr.toDoubleOrNull() ?: sow.totalValue
                            if (sowNum.isNotBlank() && title.isNotBlank()) {
                                onSave(
                                    sow.copy(
                                        sowNumber = sowNum,
                                        title = title,
                                        clientName = client,
                                        totalValue = v,
                                        startDate = start,
                                        endDate = end,
                                        status = status
                                    )
                                )
                            }
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = TealLight, contentColor = Color(0xFF0F172A)),
                        shape = RoundedCornerShape(8.dp),
                        contentPadding = PaddingValues(horizontal = 14.dp, vertical = 6.dp),
                        modifier = Modifier.testTag("save_edit_sow_button")
                    ) {
                        Text("Save Changes", fontWeight = FontWeight.Bold, maxLines = 1, softWrap = false)
                    }
                }
            }
        }
    }
}

@Composable
private fun EditAmendmentDialog(
    amendment: SowAmendmentEntity,
    onDismiss: () -> Unit,
    onSave: (SowAmendmentEntity) -> Unit
) {
    var amdNum by remember { mutableStateOf(amendment.amendmentNumber) }
    var summary by remember { mutableStateOf(amendment.summary) }
    var impactStr by remember { mutableStateOf(amendment.financialImpact.toString()) }
    var extMonthsStr by remember { mutableStateOf(amendment.extensionMonths.toString()) }

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
                Text("Edit SOW Amendment", color = Color.White, fontSize = 18.sp, fontWeight = FontWeight.Bold)

                OutlinedTextField(
                    value = amdNum,
                    onValueChange = { amdNum = it },
                    label = { Text("Amendment Number") },
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = Color.White,
                        unfocusedTextColor = Color.White,
                        focusedBorderColor = TealLight,
                        unfocusedBorderColor = CharcoalBorder
                    ),
                    modifier = Modifier.fillMaxWidth().testTag("edit_amd_num_input")
                )

                OutlinedTextField(
                    value = summary,
                    onValueChange = { summary = it },
                    label = { Text("Summary & Scope Impact") },
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = Color.White,
                        unfocusedTextColor = Color.White,
                        focusedBorderColor = TealLight,
                        unfocusedBorderColor = CharcoalBorder
                    ),
                    modifier = Modifier.fillMaxWidth()
                )

                OutlinedTextField(
                    value = impactStr,
                    onValueChange = { impactStr = it },
                    label = { Text("Financial Impact Amount (USD)") },
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
                            val impact = impactStr.toDoubleOrNull() ?: amendment.financialImpact
                            val ext = extMonthsStr.toIntOrNull() ?: amendment.extensionMonths
                            if (amdNum.isNotBlank() && summary.isNotBlank()) {
                                onSave(
                                    amendment.copy(
                                        amendmentNumber = amdNum,
                                        summary = summary,
                                        financialImpact = impact,
                                        extensionMonths = ext
                                    )
                                )
                            }
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = TealLight, contentColor = Color(0xFF0F172A)),
                        shape = RoundedCornerShape(8.dp),
                        contentPadding = PaddingValues(horizontal = 14.dp, vertical = 6.dp),
                        modifier = Modifier.testTag("save_edit_amd_button")
                    ) {
                        Text("Save Changes", fontWeight = FontWeight.Bold, maxLines = 1, softWrap = false)
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun EditMilestoneDialog(
    milestone: MilestoneEntity,
    onDismiss: () -> Unit,
    onSave: (MilestoneEntity) -> Unit
) {
    var name by remember { mutableStateOf(milestone.name) }
    var targetDate by remember { mutableStateOf(milestone.targetDate) }
    var quarter by remember { mutableStateOf(milestone.quarter) }
    var amountStr by remember { mutableStateOf(milestone.amount.toString()) }
    var trigger by remember { mutableStateOf(milestone.billingTrigger) }
    var status by remember { mutableStateOf(milestone.status) }
    var statusExpanded by remember { mutableStateOf(false) }

    val statusOptions = listOf("Pending", "In Progress", "Achieved")

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
                Text("Edit Project Milestone", color = Color.White, fontSize = 18.sp, fontWeight = FontWeight.Bold)

                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text("Milestone Deliverable Name") },
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = Color.White,
                        unfocusedTextColor = Color.White,
                        focusedBorderColor = TealLight,
                        unfocusedBorderColor = CharcoalBorder
                    ),
                    modifier = Modifier.fillMaxWidth().testTag("edit_milestone_name_input")
                )

                OutlinedTextField(
                    value = trigger,
                    onValueChange = { trigger = it },
                    label = { Text("Billing Trigger Requirement") },
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = Color.White,
                        unfocusedTextColor = Color.White,
                        focusedBorderColor = TealLight,
                        unfocusedBorderColor = CharcoalBorder
                    ),
                    modifier = Modifier.fillMaxWidth()
                )

                OutlinedTextField(
                    value = amountStr,
                    onValueChange = { amountStr = it },
                    label = { Text("Milestone Amount (USD)") },
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = Color.White,
                        unfocusedTextColor = Color.White,
                        focusedBorderColor = TealLight,
                        unfocusedBorderColor = CharcoalBorder
                    ),
                    modifier = Modifier.fillMaxWidth()
                )

                OutlinedTextField(
                    value = targetDate,
                    onValueChange = { targetDate = it },
                    label = { Text("Target Date (YYYY-MM-DD)") },
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = Color.White,
                        unfocusedTextColor = Color.White,
                        focusedBorderColor = TealLight,
                        unfocusedBorderColor = CharcoalBorder
                    ),
                    modifier = Modifier.fillMaxWidth()
                )

                ExposedDropdownMenuBox(
                    expanded = statusExpanded,
                    onExpandedChange = { statusExpanded = !statusExpanded }
                ) {
                    OutlinedTextField(
                        value = status,
                        onValueChange = {},
                        readOnly = true,
                        label = { Text("Milestone Status") },
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = statusExpanded) },
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedTextColor = Color.White,
                            unfocusedTextColor = Color.White,
                            focusedBorderColor = TealLight,
                            unfocusedBorderColor = CharcoalBorder
                        ),
                        modifier = Modifier.fillMaxWidth().menuAnchor()
                    )
                    ExposedDropdownMenu(
                        expanded = statusExpanded,
                        onDismissRequest = { statusExpanded = false }
                    ) {
                        statusOptions.forEach { opt ->
                            DropdownMenuItem(
                                text = { Text(opt) },
                                onClick = {
                                    status = opt
                                    statusExpanded = false
                                }
                            )
                        }
                    }
                }

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
                            val amt = amountStr.toDoubleOrNull() ?: milestone.amount
                            if (name.isNotBlank()) {
                                onSave(
                                    milestone.copy(
                                        name = name,
                                        billingTrigger = trigger,
                                        amount = amt,
                                        targetDate = targetDate,
                                        quarter = quarter,
                                        status = status
                                    )
                                )
                            }
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = TealLight, contentColor = Color(0xFF0F172A)),
                        shape = RoundedCornerShape(8.dp),
                        contentPadding = PaddingValues(horizontal = 14.dp, vertical = 6.dp),
                        modifier = Modifier.testTag("save_edit_milestone_button")
                    ) {
                        Text("Save Changes", fontWeight = FontWeight.Bold, maxLines = 1, softWrap = false)
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun EditExpenseDialog(
    expense: ExpenseEntity,
    onDismiss: () -> Unit,
    onSave: (ExpenseEntity) -> Unit
) {
    var selectedCategory by remember { mutableStateOf(expense.category) }
    var amountStr by remember { mutableStateOf(expense.amount.toString()) }
    var desc by remember { mutableStateOf(expense.description) }
    var invRef by remember { mutableStateOf(expense.invoiceRef) }
    var date by remember { mutableStateOf(expense.date) }
    var catExpanded by remember { mutableStateOf(false) }

    val categories = listOf(
        ExpenseCategory.INFRASTRUCTURE,
        ExpenseCategory.SOFTWARE_LICENSES,
        ExpenseCategory.TRAVEL,
        ExpenseCategory.MISCELLANEOUS
    )

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
                Text("Edit Project Expense", color = Color.White, fontSize = 18.sp, fontWeight = FontWeight.Bold)

                ExposedDropdownMenuBox(
                    expanded = catExpanded,
                    onExpandedChange = { catExpanded = !catExpanded }
                ) {
                    OutlinedTextField(
                        value = selectedCategory,
                        onValueChange = {},
                        readOnly = true,
                        label = { Text("Expense Category") },
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = catExpanded) },
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedTextColor = Color.White,
                            unfocusedTextColor = Color.White,
                            focusedBorderColor = TealLight,
                            unfocusedBorderColor = CharcoalBorder
                        ),
                        modifier = Modifier.fillMaxWidth().menuAnchor()
                    )
                    ExposedDropdownMenu(
                        expanded = catExpanded,
                        onDismissRequest = { catExpanded = false }
                    ) {
                        categories.forEach { cat ->
                            DropdownMenuItem(
                                text = { Text(cat) },
                                onClick = {
                                    selectedCategory = cat
                                    catExpanded = false
                                }
                            )
                        }
                    }
                }

                OutlinedTextField(
                    value = amountStr,
                    onValueChange = { amountStr = it },
                    label = { Text("Amount (USD)") },
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = Color.White,
                        unfocusedTextColor = Color.White,
                        focusedBorderColor = TealLight,
                        unfocusedBorderColor = CharcoalBorder
                    ),
                    modifier = Modifier.fillMaxWidth().testTag("edit_expense_amount_input")
                )

                OutlinedTextField(
                    value = desc,
                    onValueChange = { desc = it },
                    label = { Text("Description") },
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = Color.White,
                        unfocusedTextColor = Color.White,
                        focusedBorderColor = TealLight,
                        unfocusedBorderColor = CharcoalBorder
                    ),
                    modifier = Modifier.fillMaxWidth()
                )

                OutlinedTextField(
                    value = invRef,
                    onValueChange = { invRef = it },
                    label = { Text("Invoice / PO Ref") },
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
                            val amt = amountStr.toDoubleOrNull() ?: expense.amount
                            if (desc.isNotBlank()) {
                                onSave(
                                    expense.copy(
                                        category = selectedCategory,
                                        amount = amt,
                                        description = desc,
                                        invoiceRef = invRef,
                                        date = date
                                    )
                                )
                            }
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = TealLight, contentColor = Color(0xFF0F172A)),
                        shape = RoundedCornerShape(8.dp),
                        contentPadding = PaddingValues(horizontal = 14.dp, vertical = 6.dp),
                        modifier = Modifier.testTag("save_edit_expense_button")
                    ) {
                        Text("Save Changes", fontWeight = FontWeight.Bold, maxLines = 1, softWrap = false)
                    }
                }
            }
        }
    }
}

@Composable
private fun FinancialDeleteConfirmDialog(
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
                        modifier = Modifier.testTag("confirm_financial_delete_button")
                    ) {
                        Text("Delete", fontWeight = FontWeight.Bold, maxLines = 1, softWrap = false)
                    }
                }
            }
        }
    }
}
