@file:OptIn(androidx.compose.material3.ExperimentalMaterial3Api::class)
@file:Suppress("MagicNumber", "LongMethod", "LongParameterList", "TooManyFunctions")

package com.wateria.revamp.feature.plants

import androidx.compose.foundation.BorderStroke
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
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.role
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.wateria.R
import com.wateria.domain.model.PlantId
import com.wateria.revamp.design.WateriaBlue
import com.wateria.revamp.design.WateriaDialog
import com.wateria.revamp.design.WateriaNumberFont
import com.wateria.revamp.design.WateriaOrange
import com.wateria.revamp.design.WateriaPanelShape
import com.wateria.revamp.design.WateriaPillButton
import com.wateria.revamp.design.WateriaPillShape
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
        homeActionsViewModel.effects.collect { snackbarHostState.showSnackbar(failureMessage) }
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
        containerColor = MaterialTheme.colorScheme.primary,
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
                    CircularProgressIndicator(color = Color.White)
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
    val settingsDescription = stringResource(R.string.settingsActivityTitle)
    val moreDescription = stringResource(R.string.revamp_more_actions)
    val addDescription = stringResource(R.string.revamp_add_plant)
    Box(modifier = Modifier.fillMaxWidth().height(83.dp)) {
        Surface(
            color = MaterialTheme.colorScheme.surface,
            shape = RoundedCornerShape(topStart = 32.dp, topEnd = 32.dp),
            shadowElevation = 7.dp,
            border = BorderStroke(2.dp, MaterialTheme.colorScheme.primary),
            modifier =
                Modifier
                    .align(Alignment.BottomCenter)
                    .fillMaxWidth()
                    .height(46.dp)
        ) {}
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier =
                Modifier
                    .align(Alignment.BottomCenter)
                    .fillMaxWidth()
                    .height(74.dp)
        ) {
            Spacer(Modifier.weight(1f))
            FloatingNavigationButton(
                icon = R.drawable.icon_settings,
                description = settingsDescription,
                onClick = onSettings
            )
            Spacer(Modifier.weight(1f))
            FloatingNavigationButton(
                icon = R.drawable.icon_navigate_up_arrows,
                description = moreDescription,
                onClick = onMore,
                size = 74.dp,
                backgroundColor = MaterialTheme.colorScheme.primary,
                iconTint = Color.White,
                iconSize = 32.dp
            )
            Spacer(Modifier.weight(1f))
            FloatingNavigationButton(
                icon = R.drawable.icon_add,
                description = addDescription,
                onClick = onAdd,
                iconTint = MaterialTheme.colorScheme.primary
            )
            Spacer(Modifier.weight(1f))
        }
    }
}

@Composable
private fun FloatingNavigationButton(
    icon: Int,
    description: String,
    onClick: () -> Unit,
    size: androidx.compose.ui.unit.Dp = 60.dp,
    backgroundColor: Color = MaterialTheme.colorScheme.surface,
    iconTint: Color = MaterialTheme.colorScheme.primary,
    iconSize: androidx.compose.ui.unit.Dp = 28.dp
) {
    Surface(
        onClick = onClick,
        shape = CircleShape,
        color = backgroundColor,
        contentColor = iconTint,
        shadowElevation = 7.dp,
        border = BorderStroke(2.dp, MaterialTheme.colorScheme.primary),
        modifier = Modifier.size(size).semantics { role = Role.Button }
    ) {
        Box(contentAlignment = Alignment.Center) {
            Icon(
                painter = painterResource(icon),
                contentDescription = description,
                tint = iconTint,
                modifier = Modifier.size(iconSize)
            )
        }
    }
}

@Composable
private fun HomeActionSheet(
    onDismiss: () -> Unit,
    onIdentify: () -> Unit,
    onRate: () -> Unit,
    onTip: () -> Unit
) {
    ModalBottomSheet(
        onDismissRequest = onDismiss,
        containerColor = MaterialTheme.colorScheme.surface,
        shape = RoundedCornerShape(topStart = 36.dp, topEnd = 36.dp),
        dragHandle = null
    ) {
        Column(
            verticalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier.fillMaxWidth().padding(horizontal = 12.dp, vertical = 14.dp)
        ) {
            HomeActionRow(R.drawable.icon_google_lens, R.string.main_middle_lens_text, onIdentify)
            HomeActionRow(R.drawable.icon_award, R.string.main_middle_rate_text, onRate)
            HomeActionRow(
                R.drawable.icon_wand,
                R.string.main_middle_tip_text,
                onTip,
                filled = true
            )
        }
    }
}

@Composable
private fun HomeActionRow(
    iconRes: Int,
    textRes: Int,
    onClick: () -> Unit,
    filled: Boolean = false
) {
    val background = if (filled) MaterialTheme.colorScheme.primary else Color.White
    val foreground = if (filled) Color.White else MaterialTheme.colorScheme.primary
    Surface(
        onClick = onClick,
        color = background,
        contentColor = foreground,
        shape = WateriaPillShape,
        border = BorderStroke(2.dp, MaterialTheme.colorScheme.primary),
        modifier = Modifier.fillMaxWidth().height(56.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(horizontal = 15.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Image(
                painterResource(iconRes),
                contentDescription = null,
                modifier = Modifier.size(30.dp)
            )
            Spacer(Modifier.width(15.dp))
            Text(
                stringResource(textRes),
                style = MaterialTheme.typography.titleMedium.copy(fontSize = 18.sp),
                color = foreground
            )
        }
    }
}

@Composable
private fun LensUnavailableDialog(onDismiss: () -> Unit, onInstall: () -> Unit) {
    WateriaDialog(onDismissRequest = onDismiss) {
        Text(
            text = stringResource(R.string.google_lens_dialog_title).uppercase(),
            style = MaterialTheme.typography.headlineLarge.copy(fontSize = 24.sp),
            color = MaterialTheme.colorScheme.primary,
            textAlign = TextAlign.Center
        )
        Text(
            text = stringResource(R.string.google_lens_dialog_subtitle),
            style = MaterialTheme.typography.bodyLarge.copy(fontSize = 16.sp),
            color = WateriaOrange,
            textAlign = TextAlign.Center
        )
        Spacer(Modifier.height(30.dp))
        Image(
            painter = painterResource(R.drawable.icon_google_lens),
            contentDescription = null,
            modifier = Modifier.size(90.dp)
        )
        Spacer(Modifier.height(30.dp))
        Text(
            text = stringResource(R.string.google_lens_dialog_text2),
            style = MaterialTheme.typography.bodyLarge.copy(fontSize = 16.sp),
            color = MaterialTheme.colorScheme.onSurface,
            textAlign = TextAlign.Center
        )
        Spacer(Modifier.height(8.dp))
        WateriaPillButton(
            text = stringResource(R.string.google_lens_dialog_button_install),
            onClick = onInstall,
            color = WateriaOrange,
            modifier = Modifier.fillMaxWidth()
        )
        WateriaPillButton(
            text = stringResource(R.string.edit_plant_delete_dialog_cancel),
            onClick = onDismiss,
            filled = false,
            modifier = Modifier.fillMaxWidth()
        )
    }
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
        contentPadding = PaddingValues(start = 8.dp, top = 6.dp, end = 8.dp, bottom = 14.dp),
        verticalArrangement = Arrangement.spacedBy(4.dp)
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
    Surface(
        shape = WateriaPillShape,
        color = MaterialTheme.colorScheme.surface,
        shadowElevation = 2.dp,
        modifier = Modifier.fillMaxWidth().height(54.dp).clickable(onClick = onClick)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(horizontal = 3.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Image(
                painter = painterResource(plant.icon.toDrawableRes()),
                contentDescription = plant.name,
                contentScale = ContentScale.Fit,
                modifier = Modifier.size(48.dp).padding(8.dp)
            )
            Spacer(Modifier.width(4.dp))
            Text(
                text = plant.name,
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.primary,
                maxLines = 1,
                modifier = Modifier.weight(1f)
            )
            WateringCountdownInline(plant.watering)
            Spacer(Modifier.width(4.dp))
            WaterPlantButton(plant = plant, onWater = onWater)
        }
    }
}

@Composable
private fun WateringCountdownInline(watering: WateringUiState) {
    val count =
        when (watering) {
            is WateringUiState.Upcoming -> watering.daysRemaining
            WateringUiState.DueToday -> 0
            is WateringUiState.Overdue -> -watering.daysOverdue
        }
    val color =
        when (watering) {
            is WateringUiState.Upcoming -> MaterialTheme.colorScheme.primary
            WateringUiState.DueToday -> WateriaOrange
            is WateringUiState.Overdue -> MaterialTheme.colorScheme.error
        }
    Row(verticalAlignment = Alignment.Bottom) {
        Text(
            text = count.toString(),
            color = color,
            style =
                MaterialTheme.typography.displayMedium.copy(
                    fontFamily = WateriaNumberFont,
                    fontSize = 40.sp,
                    lineHeight = 42.sp
                ),
            modifier = Modifier.alignByBaseline()
        )
        Text(
            text = stringResource(R.string.new_plant_options_watering_frequency_text_days),
            color = color,
            style = MaterialTheme.typography.bodyLarge.copy(fontSize = 16.sp, lineHeight = 18.sp),
            modifier = Modifier.alignByBaseline().padding(start = 2.dp)
        )
    }
}

@Composable
private fun EmptyPlants(modifier: Modifier = Modifier) {
    Column(
        modifier = modifier.fillMaxSize().padding(horizontal = 8.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Image(
            painter = painterResource(R.drawable.image_girl_plants),
            contentDescription = null,
            modifier = Modifier.fillMaxWidth().height(200.dp),
            contentScale = ContentScale.Fit
        )
        Spacer(Modifier.height(18.dp))
        Text(
            text = stringResource(R.string.mainActivityNoPlantsText1).uppercase(),
            style = MaterialTheme.typography.headlineLarge.copy(fontSize = 22.sp),
            color = Color.White,
            textAlign = TextAlign.Center
        )
        Spacer(Modifier.height(4.dp))
        Text(
            text = stringResource(R.string.revamp_empty_plants_body),
            style = MaterialTheme.typography.bodySmall.copy(fontSize = 13.sp),
            color = Color.White,
            textAlign = TextAlign.Center
        )
    }
}
