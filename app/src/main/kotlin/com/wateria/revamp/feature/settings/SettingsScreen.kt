@file:OptIn(androidx.compose.material3.ExperimentalMaterial3Api::class)
@file:Suppress("MagicNumber", "LongMethod", "TooManyFunctions")

package com.wateria.revamp.feature.settings

import android.Manifest
import android.app.Activity
import android.app.TimePickerDialog
import android.content.Context
import android.content.ContextWrapper
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.provider.Settings
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.pluralStringResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.role
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.wateria.R
import com.wateria.revamp.design.WateriaDialog
import com.wateria.revamp.design.WateriaGreenDivider
import com.wateria.revamp.design.WateriaNumberFont
import com.wateria.revamp.design.WateriaOrange
import com.wateria.revamp.design.WateriaPanelShape
import com.wateria.revamp.design.WateriaPillButton
import com.wateria.revamp.design.WateriaRed
import com.wateria.revamp.design.WateriaScreenHeader
import java.time.LocalTime
import java.time.format.DateTimeFormatter

@Composable
fun SettingsRoute(
    onNavigateBack: () -> Unit,
    onOpenAbout: () -> Unit,
    onOpenLicenses: () -> Unit,
    viewModel: SettingsViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val snackbar = remember { SnackbarHostState() }
    val failure = stringResource(R.string.revamp_settings_failed)
    val deleted = stringResource(R.string.revamp_all_plants_deleted)

    LaunchedEffect(viewModel) {
        viewModel.effects.collect { effect ->
            snackbar.showSnackbar(
                when (effect) {
                    SettingsEffect.UpdateFailed -> failure
                    SettingsEffect.PlantsDeleted -> deleted
                }
            )
        }
    }

    NotificationPermissionHost(
        remindersEnabled = uiState.reminderSettings.isEnabled,
        content = { permissionState, requestPermission, openSystemSettings ->
            val actions =
                SettingsActions(
                    toggleReminders = viewModel::setRemindersEnabled,
                    setTime = viewModel::setReminderTime,
                    setSnoozeHours = viewModel::setSnoozeHours,
                    requestPermission = requestPermission,
                    openSystemSettings = openSystemSettings,
                    openAbout = onOpenAbout,
                    openLicenses = onOpenLicenses,
                    deleteAll = viewModel::deleteAll
                )
            SettingsScreen(
                uiState = uiState,
                notificationPermissionState = permissionState,
                snackbar = snackbar,
                onNavigateBack = onNavigateBack,
                actions = actions
            )
        }
    )
}

private enum class NotificationPermissionUiState {
    GRANTED,
    CAN_REQUEST,
    DENIED
}

@Composable
private fun NotificationPermissionHost(
    remindersEnabled: Boolean,
    content: @Composable (NotificationPermissionUiState, () -> Unit, () -> Unit) -> Unit
) {
    val context = LocalContext.current
    val activity = context.findActivity()
    val lifecycleOwner = LocalLifecycleOwner.current
    var refresh by remember { mutableIntStateOf(0) }
    val launcher =
        rememberLauncherForActivityResult(ActivityResultContracts.RequestPermission()) { refresh++ }
    DisposableEffect(lifecycleOwner) {
        val observer =
            LifecycleEventObserver { _, event ->
                if (event == Lifecycle.Event.ON_RESUME) refresh++
            }
        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose { lifecycleOwner.lifecycle.removeObserver(observer) }
    }

    val permissionState =
        remember(refresh, remindersEnabled) { notificationPermissionState(context, activity) }
    content(
        permissionState,
        {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                launcher.launch(Manifest.permission.POST_NOTIFICATIONS)
            }
        },
        {
            context.startActivity(
                Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
                    .setData(Uri.fromParts("package", context.packageName, null))
            )
        }
    )
}

@Composable
private fun SettingsScreen(
    uiState: SettingsUiState,
    notificationPermissionState: NotificationPermissionUiState,
    snackbar: SnackbarHostState,
    onNavigateBack: () -> Unit,
    actions: SettingsActions
) {
    var showDeleteConfirmation by rememberSaveable { mutableStateOf(false) }
    var showSnoozePicker by rememberSaveable { mutableStateOf(false) }
    val snoozeHours = uiState.reminderSettings.snoozeDuration.toHours().toInt().coerceIn(1, 23)
    Scaffold(
        containerColor = MaterialTheme.colorScheme.primary,
        topBar = { WateriaScreenHeader(stringResource(R.string.settingsActivityTitle)) },
        snackbarHost = { SnackbarHost(snackbar) },
        bottomBar = { SettingsHomeButton(onNavigateBack) }
    ) { padding ->
        if (uiState.isLoading) {
            Box(Modifier.fillMaxSize().padding(padding), contentAlignment = Alignment.Center) {
                CircularProgressIndicator(color = Color.White)
            }
        } else {
            SettingsContent(
                uiState = uiState,
                permissionState = notificationPermissionState,
                actions = actions.copy(deleteAll = { showDeleteConfirmation = true }),
                onOpenSnooze = { showSnoozePicker = true },
                modifier = Modifier.padding(padding)
            )
        }
    }
    if (showSnoozePicker) {
        SnoozePickerDialog(
            hours = snoozeHours,
            onHoursChanged = actions.setSnoozeHours,
            onDismiss = { showSnoozePicker = false }
        )
    }
    if (showDeleteConfirmation) {
        WateriaDialog(onDismissRequest = { showDeleteConfirmation = false }) {
            Image(
                painter = painterResource(R.drawable.icon_trash_can),
                contentDescription = null,
                modifier = Modifier.size(74.dp)
            )
            Text(
                text = stringResource(R.string.settings_delete_all_warning_title).uppercase(),
                style = MaterialTheme.typography.headlineLarge,
                color = WateriaRed,
                textAlign = TextAlign.Center
            )
            Text(
                text = stringResource(R.string.settings_delete_all_warning_text),
                style = MaterialTheme.typography.bodyLarge,
                textAlign = TextAlign.Center
            )
            Spacer(Modifier.height(16.dp))
            WateriaPillButton(
                text = stringResource(R.string.settings_delete_all_warning_yes),
                onClick = {
                    showDeleteConfirmation = false
                    actions.deleteAll()
                },
                color = WateriaRed,
                modifier = Modifier.fillMaxWidth()
            )
            WateriaPillButton(
                text = stringResource(R.string.settings_delete_all_warning_no),
                onClick = { showDeleteConfirmation = false },
                filled = false,
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}

private data class SettingsActions(
    val toggleReminders: (Boolean) -> Unit,
    val setTime: (LocalTime) -> Unit,
    val setSnoozeHours: (Int) -> Unit,
    val requestPermission: () -> Unit,
    val openSystemSettings: () -> Unit,
    val openAbout: () -> Unit,
    val openLicenses: () -> Unit,
    val deleteAll: () -> Unit
)

@Composable
private fun SettingsContent(
    uiState: SettingsUiState,
    permissionState: NotificationPermissionUiState,
    actions: SettingsActions,
    onOpenSnooze: () -> Unit,
    modifier: Modifier = Modifier
) {
    val settings = uiState.reminderSettings
    Column(
        modifier =
            modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(start = 8.dp, top = 10.dp, end = 8.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        SettingsGroup {
            SettingsSwitchRow(
                icon = R.drawable.icon_notif_bell,
                title = stringResource(R.string.settings_notif_enabler_text),
                checked = settings.isEnabled,
                enabled = !uiState.isSaving,
                onCheckedChange = actions.toggleReminders
            )
            WateriaGreenDivider()
            ReminderTimeRow(settings.isEnabled, settings.time, actions.setTime)
            WateriaGreenDivider()
            SettingsValueRow(
                icon = R.drawable.icon_clock_remind_later_green,
                title = stringResource(R.string.settings_notif_postpone_text),
                value =
                    pluralStringResource(
                        R.plurals.revamp_snooze_hours,
                        settings.snoozeDuration.toHours().toInt(),
                        settings.snoozeDuration.toHours().toInt()
                    ),
                enabled = settings.isEnabled && !uiState.isSaving,
                onClick = onOpenSnooze
            )
        }

        if (settings.isEnabled && permissionState != NotificationPermissionUiState.GRANTED) {
            PermissionCard(permissionState, actions.requestPermission, actions.openSystemSettings)
        }

        SettingsGroup {
            SettingsNavigationRow(
                icon = R.drawable.icon_trash_can,
                title = stringResource(R.string.settings_delete_text),
                onClick = actions.deleteAll
            )
        }

        SettingsGroup {
            SettingsNavigationRow(
                icon = R.drawable.icon_documents,
                title = stringResource(R.string.settings_license_text),
                onClick = actions.openLicenses
            )
            WateriaGreenDivider()
            SettingsNavigationRow(
                icon = R.drawable.icon_about_info,
                title = stringResource(R.string.settings_about_text),
                onClick = actions.openAbout
            )
        }
        Spacer(Modifier.height(20.dp))
    }
}

@Composable
private fun SettingsHomeButton(onClick: () -> Unit) {
    val description = stringResource(R.string.revamp_go_back)
    Box(modifier = Modifier.fillMaxWidth().height(83.dp), contentAlignment = Alignment.Center) {
        Surface(
            color = Color.White,
            shape = WateriaPanelShape,
            modifier = Modifier.align(Alignment.BottomCenter).fillMaxWidth().height(46.dp)
        ) { }
        Surface(
            onClick = onClick,
            shape = CircleShape,
            color = MaterialTheme.colorScheme.primary,
            contentColor = Color.White,
            shadowElevation = 7.dp,
            border = BorderStroke(3.dp, Color.White),
            modifier = Modifier.align(Alignment.TopCenter).size(74.dp).semantics {
                role =
                    Role.Button
            }
        ) {
            Box(contentAlignment = Alignment.Center) {
                Icon(
                    painter = painterResource(R.drawable.icon_home),
                    contentDescription = description,
                    tint = Color.White,
                    modifier = Modifier.size(34.dp)
                )
            }
        }
    }
}

@Composable
private fun SettingsGroup(content: @Composable () -> Unit) {
    Surface(
        color = Color.White,
        shape = RoundedCornerShape(22.dp),
        shadowElevation = 2.dp,
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(content = { content() })
    }
}

@Composable
private fun SettingsSwitchRow(
    icon: Int,
    title: String,
    checked: Boolean,
    enabled: Boolean,
    onCheckedChange: (Boolean) -> Unit
) {
    SettingsRow(icon = icon) {
        Text(
            title,
            modifier = Modifier.weight(1f),
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.primary
        )
        Switch(
            checked = checked,
            onCheckedChange = onCheckedChange,
            enabled = enabled,
            colors =
                SwitchDefaults.colors(
                    checkedThumbColor = Color.White,
                    checkedTrackColor = WateriaOrange,
                    uncheckedThumbColor = MaterialTheme.colorScheme.primary,
                    uncheckedTrackColor = Color.White,
                    uncheckedBorderColor = MaterialTheme.colorScheme.primary
                )
        )
    }
}

@Composable
private fun ReminderTimeRow(enabled: Boolean, time: LocalTime, onTimeChanged: (LocalTime) -> Unit) {
    val context = LocalContext.current
    val picker =
        remember(time, context) {
            TimePickerDialog(
                context,
                { _, hour, minute -> onTimeChanged(LocalTime.of(hour, minute)) },
                time.hour,
                time.minute,
                true
            )
        }
    SettingsValueRow(
        icon = R.drawable.icon_alarm_clock,
        title = stringResource(R.string.settings_notif_timing_text),
        value = time.format(DateTimeFormatter.ofPattern("HH:mm")),
        enabled = enabled,
        onClick = picker::show
    )
}

@Composable
private fun SettingsValueRow(
    icon: Int,
    title: String,
    value: String,
    enabled: Boolean,
    onClick: () -> Unit
) {
    SettingsRow(icon = icon, enabled = enabled, onClick = onClick) {
        Text(
            title,
            modifier = Modifier.weight(1f),
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.primary.copy(alpha = if (enabled) 1f else 0.45f)
        )
        Text(
            value,
            color = WateriaOrange.copy(alpha = if (enabled) 1f else 0.45f),
            style =
                MaterialTheme.typography.displayMedium.copy(
                    fontFamily = WateriaNumberFont,
                    fontSize = 36.sp,
                    lineHeight = 40.sp
                )
        )
    }
}

@Composable
private fun SettingsNavigationRow(
    icon: Int,
    title: String,
    color: Color = MaterialTheme.colorScheme.primary,
    onClick: () -> Unit
) {
    SettingsRow(icon = icon, onClick = onClick) {
        Text(
            title,
            modifier = Modifier.weight(1f),
            style = MaterialTheme.typography.titleMedium,
            color = color
        )
    }
}

@Composable
private fun SettingsRow(
    icon: Int,
    enabled: Boolean = true,
    onClick: (() -> Unit)? = null,
    content: @Composable androidx.compose.foundation.layout.RowScope.() -> Unit
) {
    val clickableModifier =
        if (onClick != null) Modifier.clickable(enabled = enabled, onClick = onClick) else Modifier
    Row(
        modifier =
            clickableModifier
                .fillMaxWidth()
                .height(56.dp)
                .padding(start = 2.dp, end = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Image(
            painter = painterResource(icon),
            contentDescription = null,
            modifier = Modifier.size(50.dp).padding(10.dp)
        )
        Spacer(Modifier.width(2.dp))
        content()
    }
}

@Composable
private fun SnoozePickerDialog(hours: Int, onHoursChanged: (Int) -> Unit, onDismiss: () -> Unit) {
    WateriaDialog(onDismissRequest = onDismiss) {
        Image(
            painter = painterResource(R.drawable.icon_clock_remind_later_green),
            contentDescription = null,
            modifier = Modifier.size(76.dp)
        )
        Text(
            text = stringResource(R.string.settings_notif_postpone_text).uppercase(),
            style = MaterialTheme.typography.headlineLarge,
            color = MaterialTheme.colorScheme.primary,
            textAlign = TextAlign.Center
        )
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            OutlinedButton(
                onClick = { onHoursChanged((hours - 1).coerceAtLeast(1)) },
                shape = CircleShape,
                contentPadding = PaddingValuesZero,
                modifier = Modifier.size(50.dp)
            ) {
                Text("−", style = MaterialTheme.typography.headlineMedium)
            }
            Text(
                text = hours.toString(),
                color = WateriaOrange,
                style =
                    MaterialTheme.typography.displayLarge.copy(
                        fontFamily = WateriaNumberFont,
                        fontSize = 58.sp,
                        lineHeight = 64.sp
                    ),
                textAlign = TextAlign.Center,
                modifier = Modifier.width(92.dp)
            )
            OutlinedButton(
                onClick = { onHoursChanged((hours + 1).coerceAtMost(23)) },
                shape = CircleShape,
                contentPadding = PaddingValuesZero,
                modifier = Modifier.size(50.dp)
            ) {
                Text("+", style = MaterialTheme.typography.headlineMedium)
            }
        }
        Text(
            text = pluralStringResource(R.plurals.revamp_snooze_hours, hours, hours),
            color = WateriaOrange,
            style = MaterialTheme.typography.titleMedium
        )
        Spacer(Modifier.height(10.dp))
        WateriaPillButton(
            text = stringResource(R.string.newPlantAcceptButtonText),
            onClick = onDismiss,
            modifier = Modifier.fillMaxWidth()
        )
    }
}

private val PaddingValuesZero = androidx.compose.foundation.layout.PaddingValues(0.dp)

@Composable
private fun PermissionCard(
    state: NotificationPermissionUiState,
    onRequestPermission: () -> Unit,
    onOpenSettings: () -> Unit
) {
    Surface(color = Color.White, shape = WateriaPanelShape, modifier = Modifier.fillMaxWidth()) {
        Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
            Text(
                stringResource(R.string.revamp_permission_title),
                style = MaterialTheme.typography.titleMedium,
                color = WateriaOrange
            )
            Text(
                stringResource(R.string.revamp_permission_body),
                style = MaterialTheme.typography.bodyMedium
            )
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                if (state == NotificationPermissionUiState.CAN_REQUEST) {
                    Button(onClick = onRequestPermission) {
                        Text(stringResource(R.string.revamp_allow_notifications))
                    }
                }
                OutlinedButton(onClick = onOpenSettings) {
                    Text(stringResource(R.string.revamp_system_settings))
                }
            }
        }
    }
}

private fun notificationPermissionState(
    context: Context,
    activity: Activity?
): NotificationPermissionUiState = when {
    Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU ->
        notificationManagerPermissionState(context)

    ContextCompat.checkSelfPermission(context, Manifest.permission.POST_NOTIFICATIONS) ==
        PackageManager.PERMISSION_GRANTED -> notificationManagerPermissionState(context)

    activity != null -> NotificationPermissionUiState.CAN_REQUEST

    else -> NotificationPermissionUiState.DENIED
}

private fun notificationManagerPermissionState(context: Context): NotificationPermissionUiState {
    val notificationsEnabled = NotificationManagerCompat.from(context).areNotificationsEnabled()
    return if (notificationsEnabled) {
        NotificationPermissionUiState.GRANTED
    } else {
        NotificationPermissionUiState.DENIED
    }
}

private tailrec fun Context.findActivity(): Activity? = when (this) {
    is Activity -> this
    is ContextWrapper -> baseContext.findActivity()
    else -> null
}
