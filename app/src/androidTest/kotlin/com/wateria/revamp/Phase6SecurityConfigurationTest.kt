package com.wateria.revamp

import android.Manifest
import android.content.ComponentName
import android.content.pm.ApplicationInfo
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import com.wateria.notifications.ReminderActionReceiver
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class Phase6SecurityConfigurationTest {
    @Suppress("DEPRECATION")
    @Test
    fun mergedManifestKeepsNotificationEntryPointInternalAndNetworkLocalOnly() {
        val context = InstrumentationRegistry.getInstrumentation().targetContext
        val packageManager = context.packageManager
        val receiver =
            packageManager.getReceiverInfo(
                ComponentName(context, ReminderActionReceiver::class.java),
                0
            )
        val packageInfo = packageManager.getPackageInfo(context.packageName, 0x00001000)
        val applicationInfo = context.applicationInfo

        assertFalse(receiver.exported)
        assertTrue(
            packageInfo.requestedPermissions.orEmpty().contains(
                Manifest.permission.POST_NOTIFICATIONS
            )
        )
        assertTrue(applicationInfo.flags and ApplicationInfo.FLAG_ALLOW_BACKUP != 0)
        assertFalse(applicationInfo.flags and ApplicationInfo.FLAG_USES_CLEARTEXT_TRAFFIC != 0)
    }
}
