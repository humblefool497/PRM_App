package com.example.ui.screens

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
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.Assessment
import androidx.compose.material.icons.filled.AttachMoney
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.Error
import androidx.compose.material.icons.filled.NotificationImportant
import androidx.compose.material.icons.filled.Speed
import androidx.compose.material.icons.filled.TrendingUp
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.data.local.entity.PTSStatus
import com.example.ui.DashboardMetrics
import com.example.ui.OpsViewModel
import com.example.ui.components.ActionStatusBadge
import com.example.ui.components.DefaulterBadge
import com.example.ui.components.ExportReportButton
import com.example.ui.components.MetricStatCard
import com.example.ui.components.PriorityBadge
import com.example.ui.components.PtsStatusBadge
import com.example.ui.navigation.NavDestination
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
fun DashboardScreen(
    viewModel: OpsViewModel,
    onNavigate: (NavDestination) -> Unit
) {
    val metrics by viewModel.dashboardMetrics.collectAsStateWithLifecycle()
    val timesheets by viewModel.timesheets.collectAsStateWithLifecycle()
    val defaulters by viewModel.activeDefaulters.collectAsStateWithLifecycle()
    val actionItems by viewModel.actionItems.collectAsStateWithLifecycle()
    val selectedDate by viewModel.selectedDate.collectAsStateWithLifecycle()

    val currencyFormatter = NumberFormat.getCurrencyInstance(Locale.US).apply {
        maximumFractionDigits = 0
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF0B0F17))
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            Spacer(modifier = Modifier.height(8.dp))
            // Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f).padding(end = 8.dp)) {
                    Text(
                        text = "Operations Dashboard",
                        color = Color.White,
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Black
                    )
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.DateRange,
                            contentDescription = null,
                            tint = TealAccent,
                            modifier = Modifier.size(13.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "Executive Brief • $selectedDate",
                            color = SlateLight,
                            fontSize = 12.sp
                        )
                    }
                }

                Surface(
                    color = Color(0xFF0F2327),
                    shape = RoundedCornerShape(12.dp),
                    border = androidx.compose.foundation.BorderStroke(1.dp, TealAccent.copy(alpha = 0.35f))
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 7.dp, vertical = 3.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(5.dp)
                                .clip(CircleShape)
                                .background(AlertGreen)
                        )
                        Text(
                            text = "Active",
                            color = TealAccent,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                }
            }
        }

        // 4 Primary KPI Cards in 2x2 Grid
        item {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    MetricStatCard(
                        title = "Daily PTS Progress",
                        value = "${metrics.ptsCompletionPct}%",
                        subtitle = "${metrics.ptsPendingCount} pending missing entries",
                        icon = Icons.Default.Speed,
                        accentColor = if (metrics.ptsCompletionPct >= 80) TealLight else AlertAmber,
                        modifier = Modifier
                            .weight(1f)
                            .testTag("pts_metric_card"),
                        onClick = { onNavigate(NavDestination.TEAM_COMPLIANCE) }
                    )
                    MetricStatCard(
                        title = "Active Defaulters",
                        value = "${metrics.activeDefaultersCount}",
                        subtitle = "Overdue training & portal logs",
                        icon = Icons.Default.Error,
                        accentColor = AlertCrimson,
                        modifier = Modifier
                            .weight(1f)
                            .testTag("defaulters_metric_card"),
                        onClick = { onNavigate(NavDestination.TEAM_COMPLIANCE) }
                    )
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    MetricStatCard(
                        title = "Open Action Items",
                        value = "${metrics.openActionsCount}",
                        subtitle = "${metrics.highPriorityActionsCount} High/Critical priority",
                        icon = Icons.Default.NotificationImportant,
                        accentColor = if (metrics.highPriorityActionsCount > 0) AlertCrimson else TealLight,
                        modifier = Modifier
                            .weight(1f)
                            .testTag("action_items_metric_card"),
                        onClick = { onNavigate(NavDestination.GOVERNANCE) }
                    )
                    MetricStatCard(
                        title = "Q4 Milestones",
                        value = currencyFormatter.format(metrics.q4ProjectedTotal),
                        subtitle = "${currencyFormatter.format(metrics.q4EarningsAchieved)} (${if (metrics.q4ProjectedTotal > 0) (metrics.q4EarningsAchieved * 100 / metrics.q4ProjectedTotal).toInt() else 0}%) done",
                        icon = Icons.Default.AttachMoney,
                        accentColor = TealAccent,
                        modifier = Modifier
                            .weight(1f)
                            .testTag("financials_metric_card"),
                        onClick = { onNavigate(NavDestination.FINANCIALS) }
                    )
                }
            }
        }

        // Quick Actions Row
        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = CharcoalCard),
                shape = RoundedCornerShape(12.dp),
                border = androidx.compose.foundation.BorderStroke(1.dp, CharcoalBorder)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 14.dp, vertical = 10.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f).padding(end = 8.dp)) {
                        Text(
                            text = "PTS ACTIONS & REPORTING",
                            color = SlateLight,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 0.5.sp
                        )
                        Text(
                            text = "Daily audit & quick submission",
                            color = SlateMuted,
                            fontSize = 11.sp
                        )
                    }

                    Row(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        ExportReportButton(
                            label = "PTS Report",
                            onClick = { viewModel.generateDailyPtsReport() },
                            testTag = "generate_pts_report_dash_button"
                        )

                        Surface(
                            onClick = { viewModel.submitAllPendingTimesheets() },
                            shape = RoundedCornerShape(20.dp),
                            color = Color(0xFF1E293B),
                            border = androidx.compose.foundation.BorderStroke(1.dp, CharcoalBorder),
                            modifier = Modifier.testTag("bulk_submit_pts_dash_button")
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(4.dp)
                            ) {
                                Icon(Icons.Default.CheckCircle, contentDescription = null, tint = TealAccent, modifier = Modifier.size(14.dp))
                                Text("Fast Verify", color = Color.White, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }
            }
        }

        // Financial Projections Bar Chart Snapshot
        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = CharcoalCard),
                shape = RoundedCornerShape(12.dp),
                border = androidx.compose.foundation.BorderStroke(1.dp, CharcoalBorder),
                modifier = Modifier.clickable { onNavigate(NavDestination.FINANCIALS) }
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(
                            modifier = Modifier.weight(1f),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(Icons.Default.TrendingUp, contentDescription = null, tint = TealAccent, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "Financial Projections (Q4 2026)",
                                color = Color.White,
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold,
                                maxLines = 1,
                                softWrap = false
                            )
                        }
                        Icon(Icons.Default.ArrowForward, contentDescription = "View", tint = SlateLight, modifier = Modifier.size(16.dp))
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    // Progress bar for Milestones achieved vs projected
                    val pct = if (metrics.q4ProjectedTotal > 0) (metrics.q4EarningsAchieved / metrics.q4ProjectedTotal).toFloat().coerceIn(0f, 1f) else 0f
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Milestones Achieved",
                            color = SlateLight,
                            fontSize = 11.sp,
                            maxLines = 1,
                            softWrap = false
                        )
                        Text(
                            text = "${currencyFormatter.format(metrics.q4EarningsAchieved)} of ${currencyFormatter.format(metrics.q4ProjectedTotal)}",
                            color = TealAccent,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            maxLines = 1,
                            softWrap = false
                        )
                    }
                    Spacer(modifier = Modifier.height(5.dp))
                    LinearProgressIndicator(
                        progress = { pct },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(6.dp)
                            .clip(RoundedCornerShape(3.dp)),
                        color = TealLight,
                        trackColor = Color(0xFF1E293B)
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    // Projections vs Expenses summary chips (2-column + banner layout to prevent folding)
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Surface(
                            modifier = Modifier.weight(1f),
                            color = Color(0xFF141C27),
                            shape = RoundedCornerShape(6.dp),
                            border = androidx.compose.foundation.BorderStroke(1.dp, CharcoalBorder)
                        ) {
                            Column(modifier = Modifier.padding(8.dp)) {
                                Text("Total Expenses", color = SlateMuted, fontSize = 10.sp, maxLines = 1, softWrap = false)
                                Spacer(modifier = Modifier.height(2.dp))
                                Text(
                                    text = currencyFormatter.format(metrics.totalExpenses),
                                    color = AlertCrimson,
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Bold,
                                    maxLines = 1,
                                    softWrap = false
                                )
                            }
                        }

                        Surface(
                            modifier = Modifier.weight(1f),
                            color = Color(0xFF141C27),
                            shape = RoundedCornerShape(6.dp),
                            border = androidx.compose.foundation.BorderStroke(1.dp, CharcoalBorder)
                        ) {
                            Column(modifier = Modifier.padding(8.dp)) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text("Net Margin", color = SlateMuted, fontSize = 10.sp, maxLines = 1, softWrap = false)
                                    Text(
                                        text = "${String.format(Locale.US, "%.0f", metrics.marginPct)}%",
                                        color = if (metrics.netOperatingMargin >= 0) AlertGreen else AlertCrimson,
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.Bold,
                                        maxLines = 1,
                                        softWrap = false
                                    )
                                }
                                Spacer(modifier = Modifier.height(2.dp))
                                Text(
                                    text = currencyFormatter.format(metrics.netOperatingMargin),
                                    color = if (metrics.netOperatingMargin >= 0) AlertGreen else AlertCrimson,
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Bold,
                                    maxLines = 1,
                                    softWrap = false
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(6.dp))

                    Surface(
                        modifier = Modifier.fillMaxWidth(),
                        color = Color(0xFF0F2338),
                        shape = RoundedCornerShape(6.dp),
                        border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF1E3A5F))
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 10.dp, vertical = 6.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text("Q1 2027 Pipeline Forecast", color = Color(0xFF94A3B8), fontSize = 11.sp, maxLines = 1, softWrap = false)
                            Text(
                                text = currencyFormatter.format(metrics.q1Forecast),
                                color = Color(0xFF38BDF8),
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold,
                                maxLines = 1,
                                softWrap = false
                            )
                        }
                    }
                }
            }
        }

        // Section: Daily PTS Quick Triage
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Today's PTS Roster (${timesheets.size})",
                    color = Color.White,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "Tap badge to cycle status",
                    color = SlateLight,
                    fontSize = 11.sp
                )
            }
        }

        items(timesheets.take(5)) { ts ->
            Card(
                colors = CardDefaults.cardColors(
                    containerColor = if (ts.status == PTSStatus.PENDING) Color(0xFF1F151B) else CharcoalCard
                ),
                shape = RoundedCornerShape(10.dp),
                border = androidx.compose.foundation.BorderStroke(
                    1.dp,
                    if (ts.status == PTSStatus.PENDING) AlertCrimson.copy(alpha = 0.5f) else CharcoalBorder
                )
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(12.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = ts.associateName,
                            color = Color.White,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "${ts.empId} • ${ts.hoursLogged} hrs logged",
                            color = SlateLight,
                            fontSize = 11.sp
                        )
                        if (ts.taskNotes.isNotBlank()) {
                            Text(
                                text = ts.taskNotes,
                                color = SlateMuted,
                                fontSize = 10.sp,
                                maxLines = 1
                            )
                        }
                    }

                    PtsStatusBadge(
                        status = ts.status,
                        onClick = { viewModel.cycleTimesheetStatus(ts) }
                    )
                }
            }
        }

        if (timesheets.size > 5) {
            item {
                OutlinedButton(
                    onClick = { onNavigate(NavDestination.TEAM_COMPLIANCE) },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(8.dp),
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = TealAccent),
                    contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp)
                ) {
                    Text("View Full Roster (${timesheets.size - 5} more)", fontSize = 12.sp, maxLines = 1, softWrap = false)
                    Spacer(modifier = Modifier.width(6.dp))
                    Icon(Icons.Default.ArrowForward, contentDescription = null, modifier = Modifier.size(16.dp))
                }
            }
        }

        // Section: Defaulter Highlights
        if (defaulters.isNotEmpty()) {
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.Warning, contentDescription = null, tint = AlertCrimson, modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "Overdue Defaulters Alert (${defaulters.size})",
                            color = AlertCrimson,
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                    Text(
                        text = "High Visibility",
                        color = AlertCrimson,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            items(defaulters.take(3)) { def ->
                Card(
                    colors = CardDefaults.cardColors(containerColor = AlertCrimsonBg),
                    shape = RoundedCornerShape(10.dp),
                    border = androidx.compose.foundation.BorderStroke(1.dp, AlertCrimson.copy(alpha = 0.8f))
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(12.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = def.associateName,
                                color = Color.White,
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = def.title,
                                color = Color(0xFFFECDD3),
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Medium
                            )
                            Text(
                                text = "Due: ${def.dueDate} • ${def.streamType}",
                                color = Color(0xFFFDA4AF),
                                fontSize = 10.sp
                            )
                        }

                        Surface(
                            onClick = { viewModel.toggleCompliance(def) },
                            color = AlertCrimson,
                            shape = RoundedCornerShape(6.dp),
                            modifier = Modifier.padding(start = 8.dp)
                        ) {
                            Text(
                                text = "Resolve",
                                color = Color.White,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                maxLines = 1,
                                softWrap = false,
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp)
                            )
                        }
                    }
                }
            }
        }

        item {
            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}
