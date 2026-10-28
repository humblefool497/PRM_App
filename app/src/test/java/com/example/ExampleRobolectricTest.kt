package com.example

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import com.example.data.export.ReportExportUtils
import com.example.data.local.entity.PTSStatus
import com.example.data.local.entity.TimesheetEntity
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [36])
class ExampleRobolectricTest {

    @Test
    fun `read string from context`() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val appName = context.getString(R.string.app_name)
        assertEquals("OpsManager", appName)
    }

    @Test
    fun `verify daily PTS report generation flags missing entries`() {
        val testTimesheets = listOf(
            TimesheetEntity(1, 1, "Sarah Chen", "EMP-1042", "2026-10-28", PTSStatus.SUBMITTED, 8.0, "Completed testing"),
            TimesheetEntity(2, 2, "Alex Rivera", "EMP-1142", "2026-10-28", PTSStatus.PENDING, 0.0, "")
        )
        val report = ReportExportUtils.generateDailyPtsMarkdown("2026-10-28", testTimesheets)
        assertTrue(report.contains("DAILY PROJECT TIMESHEET (PTS) REPORT"))
        assertTrue(report.contains("[!! DEFAULTER / MISSING !!]"))
        assertTrue(report.contains("Sarah Chen"))
        assertTrue(report.contains("Alex Rivera"))
    }
}
