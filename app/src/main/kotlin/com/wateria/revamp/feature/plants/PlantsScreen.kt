@file:OptIn(androidx.compose.material3.ExperimentalMaterial3Api::class)
@file:Suppress("MagicNumber")

package com.wateria.revamp.feature.plants

import androidx.compose.foundation.Image
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
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.pluralStringResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.wateria.R
import com.wateria.domain.model.PlantId
import com.wateria.revamp.design.toDrawableRes
import com.wateria.revamp.feature.tips.DailyTipPromptEffect
import com.wateria.revamp.feature.tips.DailyTipPromptViewModel

private data class PlantsActions(
    val addPlant: () -> Unit,
    val editPlant: (PlantId) -> Unit,
    val openSettings: () -> Unit,
    val waterPlant: (PlantId) -> Unit,
    val showTip: () -> Unit,
    val identifyPlant: () -> Unit,
    val rateWateria: () -> Unit,
    val dismissLensDialog: () -> Unit,
    val installLens: () -> Unit
)

@Composable
fun PlantsRoute(
    onAddPlant: () -> Unit,
    onEditPlant: (PlantId) -> Unit,
    onOpenSettings: () -> Unit,
    onShowTip: () -> Unit
) {
    val viewModel: PlantsViewModel = hiltViewModel()
    val dailyTipViewModel: DailyTipPromptViewModel = hiltViewModel()
    val homeActionsViewModel: HomeActionsViewModel = hiltViewModel()
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val homeActionsUiState by homeActionsViewModel.uiState.collectAsStateWithLifecycle()
    val snackbarHostState = remember { SnackbarHostState() }
    val failureMessage = stringResource(R.string.revamp_action_failed)
    val lifecycleOwner = LocalLifecycleOwner.current

    DisposableEffect(lifecycleOwner, viewModel) {
        val observer =
            LifecycleEventObserver { _, event ->
                if (event == Lifecycle.Event.ON_RESUME) viewModel.refreshDate()
            }
        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose { lifecycleOwner.lifecycle.removeObserver(observer) }
    }

    LaunchedEffect(viewModel, snackbarHostState) {
        viewModel.effects.collect { effect ->
            when (effect) {
                PlantsEffect.ActionFailed -> snackbarHostState.showSnackbar(failureMessage)
            }
        }
    }

    LaunchedEffect(dailyTipViewModel) {
        dailyTipViewModel.effects.collect { effect ->
            if (effect == DailyTipPromptEffect.OpenTip) onShowTip()
        }
    }

    LaunchedEffect(homeActionsViewModel) {
        homeActionsViewModel.effects.collect {
            snackbarHostState.showSnackbar(failureMessage)
        }
    }

    PlantsScreen(
        uiState = uiState,
        snackbarHostState = snackbarHostState,
        actions =
            PlantsActions(
                addPlant = onAddPlant,
                editPlant = onEditPlant,
                openSettings = onOpenSettings,
                waterPlant = viewModel::water,
                showTip = onShowTip,
                identifyPlant = homeActionsViewModel::identifyPlant,
                rateWateria = homeActionsViewModel::rateWateria,
                dismissLensDialog = homeActionsViewModel::dismissLensDialog,
                installLens = homeActionsViewModel::installLens
            ),
        homeActionsUiState = homeActionsUiState
    )
}

@Composable
private fun PlantsScreen(
    uiState: PlantsUiState,
    snackbarHostState: SnackbarHostState,
    actions: PlantsActions,
    homeActionsUiState: HomeActionsUiState
) {
    var showActionSheet by rememberSaveable { mutableStateOf(false) }
    Scaffold(
        topBar = { PlantsTopBar() },
        snackbarHost = { SnackbarHost(snackbarHostState) },
        bottomBar = {
            HomeBottomBar(
                onSettings = actions.openSettings,
                onMore = { showActionSheet = true },
                onAdd = actions.addPlant
            )
        }
    ) { paddingValues ->
        when {
            uiState.isLoading ->
                Box(
                    modifier = Modifier.fillMaxSize().padding(paddingValues),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }

            uiState.plants.isEmpty() -> EmptyPlants(Modifier.padding(paddingValues))

            else ->
                PlantList(
                    plants = uiState.plants,
                    onEditPlant = actions.editPlant,
                    onWaterPlant = actions.waterPlant,
                    modifier = Modifier.padding(paddingValues)
                )
        }
    }
    if (showActionSheet) {
        HomeActionSheet(
            onDismiss = { showActionSheet = false },
            onIdentify = {
                showActionSheet = false
                actions.identifyPlant()
            },
            onRate = {
                showActionSheet = false
                actions.rateWateria()
            },
            onTip = {
                showActionSheet = false
                actions.showTip()
            }
        )
    }
    if (homeActionsUiState.showLensInstallDialog) {
        LensUnavailableDialog(actions.dismissLensDialog, actions.installLens)
    }
}

@Composable
private fun HomeBottomBar(onSettings: () -> Unit, onMore: () -> Unit, onAdd: () -> Unit) {
    BottomAppBar(
        containerColor = MaterialTheme.colorScheme.primary,
        contentColor = MaterialTheme.colorScheme.onPrimary,
        actions = {
            IconButton(onClick = onSettings, modifier = Modifier.weight(1f)) {
                Icon(
                    painterResource(R.drawable.icon_settings),
                    contentDescription = stringResource(R.string.settingsActivityTitle),
                    modifier = Modifier.size(28.dp)
                )
            }
            IconButton(onClick = onMore, modifier = Modifier.weight(1f)) {
                Icon(
                    painterResource(R.drawable.icon_navigate_up_arrows),
                    contentDescription = stringResource(R.string.revamp_more_actions),
                    modifier = Modifier.size(34.dp),
                    tint = MaterialTheme.colorScheme.tertiary
                )
            }
            IconButton(onClick = onAdd, modifier = Modifier.weight(1f)) {
                Icon(
                    painterResource(R.drawable.icon_add),
                    contentDescription = stringResource(R.string.revamp_add_plant),
                    modifier = Modifier.size(28.dp)
                )
            }
        }
    )
}

@Composable
private fun HomeActionSheet(
    onDismiss: () -> Unit,
    onIdentify: () -> Unit,
    onRate: () -> Unit,
    onTip: () -> Unit
) {
    ModalBottomSheet(onDismissRequest = onDismiss) {
        Text(
            text = stringResource(R.string.revamp_more_actions),
            modifier = Modifier.fillMaxWidth().padding(horizontal = 20.dp, vertical = 8.dp),
            style = MaterialTheme.typography.headlineSmall,
            color = MaterialTheme.colorScheme.primary,
            textAlign = TextAlign.Center
        )
        HomeActionRow(R.drawable.icon_google_lens, R.string.main_middle_lens_text, onIdentify)
        HomeActionRow(R.drawable.icon_award, R.string.main_middle_rate_text, onRate)
        HomeActionRow(R.drawable.icon_wand, R.string.main_middle_tip_text, onTip)
        Spacer(Modifier.height(24.dp))
    }
}

@Composable
private fun HomeActionRow(iconRes: Int, textRes: Int, onClick: () -> Unit) {
    Row(
        modifier = Modifier.fillMaxWidth().clickable(onClick = onClick).padding(18.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Image(painterResource(iconRes), contentDescription = null, modifier = Modifier.size(48.dp))
        Spacer(Modifier.width(18.dp))
        Text(stringResource(textRes), style = MaterialTheme.typography.titleMedium)
    }
}

@Composable
private fun LensUnavailableDialog(onDismiss: () -> Unit, onInstall: () -> Unit) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(stringResource(R.string.google_lens_dialog_title)) },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                Text(stringResource(R.string.google_lens_dialog_subtitle))
                Text(stringResource(R.string.google_lens_dialog_text2))
            }
        },
        confirmButton = {
            Button(onClick = onInstall) {
                Text(stringResource(R.string.google_lens_dialog_button_install))
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text(stringResource(R.string.edit_plant_delete_dialog_cancel))
            }
        }
    )
}

@Composable
private fun PlantList(
    plants: List<PlantCardUiState>,
    onEditPlant: (PlantId) -> Unit,
    onWaterPlant: (PlantId) -> Unit,
    modifier: Modifier = Modifier
) {
    LazyColumn(
        modifier = modifier.fillMaxSize(),
        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 14.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        items(items = plants, key = { plant -> plant.id.value }) { plant ->
            PlantCard(
                plant = plant,
                onClick = { onEditPlant(plant.id) },
                onWater = { onWaterPlant(plant.id) }
            )
        }
    }
}

@Composable
private fun PlantCard(plant: PlantCardUiState, onClick: () -> Unit, onWater: () -> Unit) {
    Card(
        onClick = onClick,
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Image(
                painter = painterResource(plant.icon.toDrawableRes()),
                contentDescription = plant.name,
                contentScale = ContentScale.Fit,
                modifier =
                    Modifier.size(68.dp)
                        .clip(MaterialTheme.shapes.large)
                        .padding(4.dp)
            )
            Spacer(Modifier.width(14.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = plant.name,
                    style = MaterialTheme.typography.titleLarge,
                    color = MaterialTheme.colorScheme.primary,
                    fontWeight = FontWeight.SemiBold
                )
                Spacer(Modifier.height(4.dp))
                WateringStatusLabel(plant.watering)
                Text(
                    text =
                        pluralStringResource(
                            R.plurals.revamp_every_days,
                            plant.wateringIntervalDays,
                            plant.wateringIntervalDays
                        ),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            Spacer(Modifier.width(8.dp))
            FilledTonalButton(onClick = onWater, enabled = !plant.isBusy) {
                if (plant.isBusy) {
                    CircularProgressIndicator(modifier = Modifier.size(18.dp), strokeWidth = 2.dp)
                } else {
                    Text(stringResource(R.string.revamp_water))
                }
            }
        }
    }
}

@Composable
private fun WateringStatusLabel(watering: WateringUiState) {
    val text =
        when (watering) {
            is WateringUiState.Upcoming ->
                pluralStringResource(
                    R.plurals.revamp_watering_in_days,
                    watering.daysRemaining,
                    watering.daysRemaining
                )

            WateringUiState.DueToday -> stringResource(R.string.revamp_due_today)

            is WateringUiState.Overdue ->
                pluralStringResource(
                    R.plurals.revamp_overdue_days,
                    watering.daysOverdue,
                    watering.daysOverdue
                )
        }
    val color =
        when (watering) {
            is WateringUiState.Upcoming -> MaterialTheme.colorScheme.primary
            WateringUiState.DueToday -> MaterialTheme.colorScheme.tertiary
            is WateringUiState.Overdue -> MaterialTheme.colorScheme.error
        }
    Text(text = text, style = MaterialTheme.typography.titleMedium, color = color)
}

@Composable
private fun EmptyPlants(modifier: Modifier = Modifier) {
    Column(
        modifier = modifier.fillMaxSize().padding(horizontal = 32.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Image(
            painter = painterResource(R.drawable.image_girl_plants),
            contentDescription = null,
            modifier = Modifier.fillMaxWidth().height(220.dp),
            contentScale = ContentScale.Fit
        )
        Text(
            text = stringResource(R.string.mainActivityNoPlantsText1),
            style = MaterialTheme.typography.headlineSmall,
            color = MaterialTheme.colorScheme.primary,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center
        )
        Spacer(Modifier.height(8.dp))
        Text(
            text = stringResource(R.string.revamp_empty_plants_body),
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center
        )
    }
}
