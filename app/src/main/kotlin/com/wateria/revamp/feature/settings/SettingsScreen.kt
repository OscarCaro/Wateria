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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.pluralStringResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.wateria.R
import com.wateria.revamp.design.WateriaBackButton
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

    val permissionState = remember(refresh, remindersEnabled) {
        notificationPermissionState(context, activity)
    }
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
    Scaffold(
        topBar = { SettingsTopBar(onNavigateBack) },
        snackbarHost = { SnackbarHost(snackbar) }
    ) { padding ->
        if (uiState.isLoading) {
            Box(Modifier.fillMaxSize().padding(padding), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
        } else {
            SettingsContent(
                uiState = uiState,
                permissionState = notificationPermissionState,
                actions = actions.copy(deleteAll = { showDeleteConfirmation = true }),
                modifier = Modifier.padding(padding)
            )
        }
    }
    if (showDeleteConfirmation) {
        AlertDialog(
            onDismissRequest = { showDeleteConfirmation = false },
            title = { Text(stringResource(R.string.settings_delete_all_warning_title)) },
            text = { Text(stringResource(R.string.settings_delete_all_warning_text)) },
            confirmButton = {
                TextButton(
                    onClick = {
                        showDeleteConfirmation = false
                        actions.deleteAll()
                    }
                ) {
                    Text(
                        stringResource(R.string.settings_delete_all_warning_yes),
                        color = MaterialTheme.colorScheme.error
                    )
                }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteConfirmation = false }) {
                    Text(stringResource(R.string.settings_delete_all_warning_no))
                }
            }
        )
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
    modifier: Modifier = Modifier
) {
    val settings = uiState.reminderSettings
    Column(
        modifier = modifier.fillMaxSize().verticalScroll(rememberScrollState()).padding(18.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        SettingsSectionTitle(stringResource(R.string.revamp_reminders_section))
        SettingsCard {
            SettingsSwitchRow(
                title = stringResource(R.string.settings_notif_enabler_text),
                checked = settings.isEnabled,
                enabled = !uiState.isSaving,
                onCheckedChange = actions.toggleReminders
            )
            HorizontalDivider()
            ReminderTimeRow(settings.isEnabled, settings.time, actions.setTime)
            HorizontalDivider()
            SnoozeRow(
                enabled = settings.isEnabled && !uiState.isSaving,
                hours = settings.snoozeDuration.toHours().toInt().coerceIn(1, 23),
                onHoursChanged = actions.setSnoozeHours
            )
        }

        if (settings.isEnabled && permissionState != NotificationPermissionUiState.GRANTED) {
            PermissionCard(permissionState, actions.requestPermission, actions.openSystemSettings)
        }

        SettingsSectionTitle(stringResource(R.string.revamp_app_section))
        SettingsCard {
            SettingsNavigationRow(stringResource(R.string.settings_about_text), actions.openAbout)
            HorizontalDivider()
            SettingsNavigationRow(
                stringResource(R.string.settings_license_text),
                actions.openLicenses
            )
        }

        SettingsSectionTitle(stringResource(R.string.revamp_data_section))
        OutlinedButton(
            onClick = actions.deleteAll,
            enabled = !uiState.isSaving,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(
                stringResource(R.string.settings_delete_text),
                color = MaterialTheme.colorScheme.error
            )
        }
        Spacer(Modifier.height(32.dp))
    }
}

@Composable
private fun SettingsTopBar(onBack: () -> Unit) {
    TopAppBar(
        navigationIcon = {
            WateriaBackButton(onBack)
        },
        title = { Text(stringResource(R.string.settingsActivityTitle)) },
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = MaterialTheme.colorScheme.primary,
            titleContentColor = MaterialTheme.colorScheme.onPrimary
        )
    )
}

@Composable
private fun SettingsCard(content: @Composable () -> Unit) {
    Card(modifier = Modifier.fillMaxWidth()) { Column(content = { content() }) }
}

@Composable
private fun SettingsSectionTitle(text: String) {
    Text(
        text,
        style = MaterialTheme.typography.titleMedium,
        color = MaterialTheme.colorScheme.primary,
        fontWeight = FontWeight.Bold
    )
}

@Composable
private fun SettingsSwitchRow(
    title: String,
    checked: Boolean,
    enabled: Boolean,
    onCheckedChange: (Boolean) -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth().padding(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(title, modifier = Modifier.weight(1f), style = MaterialTheme.typography.titleMedium)
        Switch(checked = checked, onCheckedChange = onCheckedChange, enabled = enabled)
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
        title = stringResource(R.string.settings_notif_timing_text),
        value = time.format(DateTimeFormatter.ofPattern("HH:mm")),
        enabled = enabled,
        onClick = picker::show
    )
}

@Composable
private fun SnoozeRow(enabled: Boolean, hours: Int, onHoursChanged: (Int) -> Unit) {
    Column(Modifier.fillMaxWidth().padding(16.dp)) {
        Text(
            stringResource(R.string.settings_notif_postpone_text),
            style = MaterialTheme.typography.titleMedium
        )
        Spacer(Modifier.height(10.dp))
        Row(verticalAlignment = Alignment.CenterVertically) {
            OutlinedButton(
                onClick = { onHoursChanged((hours - 1).coerceAtLeast(1)) },
                enabled = enabled
            ) {
                Text("−")
            }
            Text(
                pluralStringResource(R.plurals.revamp_snooze_hours, hours, hours),
                modifier = Modifier.weight(1f),
                style = MaterialTheme.typography.titleLarge,
                color =
                    if (enabled) {
                        MaterialTheme.colorScheme.tertiary
                    } else {
                        MaterialTheme.colorScheme.onSurfaceVariant
                    },
                textAlign = androidx.compose.ui.text.style.TextAlign.Center
            )
            OutlinedButton(
                onClick = { onHoursChanged((hours + 1).coerceAtMost(23)) },
                enabled = enabled
            ) {
                Text("+")
            }
        }
    }
}

@Composable
private fun SettingsValueRow(title: String, value: String, enabled: Boolean, onClick: () -> Unit) {
    Row(
        modifier = Modifier.fillMaxWidth().clickable(
            enabled = enabled,
            onClick = onClick
        ).padding(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(title, modifier = Modifier.weight(1f), style = MaterialTheme.typography.titleMedium)
        Text(
            value,
            color =
                if (enabled) {
                    MaterialTheme.colorScheme.tertiary
                } else {
                    MaterialTheme.colorScheme.onSurfaceVariant
                },
            fontWeight = FontWeight.Bold
        )
    }
}

@Composable
private fun SettingsNavigationRow(title: String, onClick: () -> Unit) {
    Row(
        modifier = Modifier.fillMaxWidth().clickable(onClick = onClick).padding(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(title, modifier = Modifier.weight(1f), style = MaterialTheme.typography.titleMedium)
        Text(
            "›",
            style = MaterialTheme.typography.headlineSmall,
            color = MaterialTheme.colorScheme.primary
        )
    }
}

@Composable
private fun PermissionCard(
    state: NotificationPermissionUiState,
    onRequestPermission: () -> Unit,
    onOpenSettings: () -> Unit
) {
    Card(modifier = Modifier.fillMaxWidth()) {
        Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
            Text(
                stringResource(R.string.revamp_permission_title),
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.tertiary
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
