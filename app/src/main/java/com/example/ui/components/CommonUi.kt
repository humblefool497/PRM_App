package com.example.ui.components

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.widget.Toast
import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AssignmentTurnedIn
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.ErrorOutline
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.PendingActions
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.data.export.ReportExportUtils
import com.example.data.local.entity.PTSStatus
import com.example.ui.ActiveReportModalState
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
fun PtsStatusBadge(
    status: String,
    modifier: Modifier = Modifier,
    onClick: (() -> Unit)? = null
) {
    val (bgColor, textColor, icon, label) = when (status) {
        PTSStatus.PENDING -> Quad(
            AlertCrimsonBg,
            AlertCrimson,
            Icons.Default.ErrorOutline,
            "Pending Submission"
        )
        PTSStatus.SUBMITTED -> Quad(
            TealContainer,
            TealAccent,
            Icons.Default.AssignmentTurnedIn,
            "Submitted - Review"
        )
        PTSStatus.CLIENT_ASSIGNED -> Quad(
            Color(0xFF1E293B),
            Color(0xFF38BDF8),
            Icons.Default.Lock,
            "Client Manager Locked"
        )
        else -> Quad(
            Color(0xFF334155),
            SlateLight,
            Icons.Default.PendingActions,
            status
        )
    }

    Surface(
        modifier = modifier
            .clip(RoundedCornerShape(8.dp))
            .then(if (onClick != null) Modifier.clickable { onClick() } else Modifier),
        color = bgColor,
        shape = RoundedCornerShape(8.dp),
        border = androidx.compose.foundation.BorderStroke(1.dp, textColor.copy(alpha = 0.5f))
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = textColor,
                modifier = Modifier.size(14.dp)
            )
            Text(
                text = label,
                color = textColor,
                fontSize = 11.sp,
                fontWeight = FontWeight.SemiBold,
                maxLines = 1,
                softWrap = false
            )
        }
    }
}

private data class Quad<A, B, C, D>(val first: A, val second: B, val third: C, val fourth: D)

@Composable
fun PriorityBadge(priority: String) {
    val (color, bg) = when (priority.lowercase()) {
        "high", "critical" -> AlertCrimson to AlertCrimsonBg
        "medium" -> AlertAmber to Color(0xFF451A03)
        else -> SlateLight to Color(0xFF1E293B)
    }
    Surface(
        color = bg,
        shape = RoundedCornerShape(6.dp),
        border = androidx.compose.foundation.BorderStroke(1.dp, color.copy(alpha = 0.4f))
    ) {
        Text(
            text = priority.uppercase(),
            color = color,
            fontSize = 10.sp,
            fontWeight = FontWeight.Bold,
            maxLines = 1,
            softWrap = false,
            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
        )
    }
}

@Composable
fun ActionStatusBadge(status: String) {
    val (color, bg) = when (status) {
        "Open" -> AlertCrimson to AlertCrimsonBg
        "In Progress" -> AlertAmber to Color(0xFF451A03)
        "Resolved" -> AlertGreen to Color(0xFF064E3B)
        else -> SlateLight to Color(0xFF1E293B)
    }
    Surface(
        color = bg,
        shape = RoundedCornerShape(6.dp),
        border = androidx.compose.foundation.BorderStroke(1.dp, color.copy(alpha = 0.4f))
    ) {
        Text(
            text = status,
            color = color,
            fontSize = 10.sp,
            fontWeight = FontWeight.SemiBold,
            maxLines = 1,
            softWrap = false,
            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
        )
    }
}

@Composable
fun DefaulterBadge(isCompleted: Boolean, isOverdue: Boolean = true) {
    if (!isCompleted) {
        Surface(
            color = AlertCrimsonBg,
            shape = RoundedCornerShape(6.dp),
            border = androidx.compose.foundation.BorderStroke(1.dp, AlertCrimson)
        ) {
            Row(
                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Warning,
                    contentDescription = "Overdue",
                    tint = AlertCrimson,
                    modifier = Modifier.size(12.dp)
                )
                Text(
                    text = "DEFAULTER",
                    color = AlertCrimson,
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Black,
                    maxLines = 1,
                    softWrap = false
                )
            }
        }
    } else {
        Surface(
            color = Color(0xFF064E3B),
            shape = RoundedCornerShape(6.dp),
            border = androidx.compose.foundation.BorderStroke(1.dp, AlertGreen.copy(alpha = 0.5f))
        ) {
            Text(
                text = "COMPLIANT",
                color = AlertGreen,
                fontSize = 10.sp,
                fontWeight = FontWeight.Bold,
                maxLines = 1,
                softWrap = false,
                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
            )
        }
    }
}

@Composable
fun MetricStatCard(
    title: String,
    value: String,
    subtitle: String,
    icon: ImageVector,
    accentColor: Color = TealLight,
    modifier: Modifier = Modifier,
    onClick: (() -> Unit)? = null
) {
    Card(
        modifier = modifier
            .then(if (onClick != null) Modifier.clickable { onClick() } else Modifier),
        colors = CardDefaults.cardColors(containerColor = CharcoalCard),
        shape = RoundedCornerShape(12.dp),
        border = androidx.compose.foundation.BorderStroke(1.dp, CharcoalBorder)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = title.uppercase(),
                    color = SlateLight,
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 0.5.sp,
                    maxLines = 1,
                    softWrap = false,
                    modifier = Modifier.weight(1f, fill = false)
                )
                Box(
                    modifier = Modifier
                        .size(26.dp)
                        .clip(CircleShape)
                        .background(accentColor.copy(alpha = 0.15f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = icon,
                        contentDescription = null,
                        tint = accentColor,
                        modifier = Modifier.size(15.dp)
                    )
                }
            }
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = value,
                color = MaterialTheme.colorScheme.onSurface,
                fontSize = 20.sp,
                fontWeight = FontWeight.ExtraBold,
                maxLines = 1,
                softWrap = false
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = subtitle,
                color = SlateMuted,
                fontSize = 11.sp,
                maxLines = 1,
                softWrap = false
            )
        }
    }
}

/**
 * Elegant, compact report export button used across headers and action zones.
 * Never creates an awkward, heavy horizontal bar on mobile.
 */
@Composable
fun ExportReportButton(
    label: String = "Report",
    icon: ImageVector = Icons.Default.Description,
    accentColor: Color = TealAccent,
    containerColor: Color = TealContainer,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    testTag: String = "export_report_button"
) {
    Surface(
        onClick = onClick,
        shape = RoundedCornerShape(20.dp),
        color = containerColor,
        border = androidx.compose.foundation.BorderStroke(1.dp, accentColor.copy(alpha = 0.45f)),
        modifier = modifier.testTag(testTag)
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Icon(
                imageVector = icon,
                contentDescription = label,
                tint = accentColor,
                modifier = Modifier.size(14.dp)
            )
            Text(
                text = label,
                color = accentColor,
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                maxLines = 1,
                softWrap = false
            )
        }
    }
}

/**
 * Sleek, mobile-optimized Report preview modal dialog with Markdown / CSV views,
 * Copy to clipboard, and System Share without any horizontal bars or overflow.
 */
@Composable
fun ReportDialog(
    report: ActiveReportModalState,
    onDismiss: () -> Unit
) {
    val context = LocalContext.current
    var showCsvView by remember { mutableStateOf(false) }
    val displayContent = if (showCsvView && report.isCsvAvailable) report.csvContent else report.content

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Card(
            modifier = Modifier
                .padding(horizontal = 16.dp, vertical = 24.dp)
                .fillMaxWidth()
                .widthIn(max = 520.dp)
                .clip(RoundedCornerShape(16.dp))
                .testTag("report_dialog_card"),
            colors = CardDefaults.cardColors(containerColor = Color(0xFF0F172A)),
            border = androidx.compose.foundation.BorderStroke(1.dp, TealLight.copy(alpha = 0.35f))
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            ) {
                // Header
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f).padding(end = 8.dp)) {
                        Text(
                            text = report.title,
                            color = Color.White,
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            maxLines = 1
                        )
                        Text(
                            text = "Formatted for executive export",
                            color = SlateLight,
                            fontSize = 11.sp
                        )
                    }
                    IconButton(
                        onClick = onDismiss,
                        modifier = Modifier.size(32.dp).testTag("close_report_button")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "Close",
                            tint = SlateLight,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                // Actions & Format controls row
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    if (report.isCsvAvailable) {
                        Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                            FormatPill(
                                label = "Markdown",
                                isSelected = !showCsvView,
                                onClick = { showCsvView = false }
                            )
                            FormatPill(
                                label = "CSV",
                                isSelected = showCsvView,
                                onClick = { showCsvView = true }
                            )
                        }
                    } else {
                        Surface(
                            color = Color(0xFF1E293B),
                            shape = RoundedCornerShape(6.dp)
                        ) {
                            Text(
                                text = "Markdown Summary",
                                color = TealAccent,
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                            )
                        }
                    }

                    Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                        // Copy Button
                        Surface(
                            onClick = {
                                val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                                val clip = ClipData.newPlainText(report.subject, displayContent)
                                clipboard.setPrimaryClip(clip)
                                Toast.makeText(context, "Report copied to clipboard!", Toast.LENGTH_SHORT).show()
                            },
                            shape = RoundedCornerShape(6.dp),
                            color = Color(0xFF1E293B),
                            border = androidx.compose.foundation.BorderStroke(1.dp, CharcoalBorder),
                            modifier = Modifier.testTag("copy_report_button")
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 5.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(3.dp)
                            ) {
                                Icon(Icons.Default.ContentCopy, contentDescription = null, tint = SlateLight, modifier = Modifier.size(13.dp))
                                Text("Copy", color = SlateLight, fontSize = 11.sp, fontWeight = FontWeight.SemiBold, maxLines = 1, softWrap = false)
                            }
                        }

                        // Share Button
                        Surface(
                            onClick = {
                                ReportExportUtils.shareText(context, report.subject, displayContent)
                            },
                            shape = RoundedCornerShape(6.dp),
                            color = TealLight,
                            modifier = Modifier.testTag("share_report_button")
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(3.dp)
                            ) {
                                Icon(Icons.Default.Share, contentDescription = null, tint = Color(0xFF0F172A), modifier = Modifier.size(13.dp))
                                Text("Share", color = Color(0xFF0F172A), fontSize = 11.sp, fontWeight = FontWeight.Bold, maxLines = 1, softWrap = false)
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                // Text viewer box with clean vertical scroll and soft word-wrap (no ugly horizontal bar)
                Surface(
                    modifier = Modifier
                        .fillMaxWidth()
                        .heightIn(min = 200.dp, max = 340.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .border(1.dp, CharcoalBorder, RoundedCornerShape(8.dp)),
                    color = Color(0xFF090D14)
                ) {
                    val scrollState = rememberScrollState()
                    Text(
                        text = displayContent,
                        color = Color(0xFFE2E8F0),
                        fontFamily = FontFamily.Monospace,
                        fontSize = 11.sp,
                        lineHeight = 15.sp,
                        modifier = Modifier
                            .verticalScroll(scrollState)
                            .padding(12.dp)
                    )
                }
            }
        }
    }
}

@Composable
private fun FormatPill(
    label: String,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    val bg by animateColorAsState(if (isSelected) TealContainer else Color(0xFF1E293B), label = "bg")
    val textCol by animateColorAsState(if (isSelected) TealAccent else SlateLight, label = "text")

    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(6.dp))
            .background(bg)
            .clickable { onClick() }
            .padding(horizontal = 8.dp, vertical = 4.dp)
    ) {
        Text(
            text = label,
            color = textCol,
            fontSize = 10.sp,
            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
            maxLines = 1,
            softWrap = false
        )
    }
}
