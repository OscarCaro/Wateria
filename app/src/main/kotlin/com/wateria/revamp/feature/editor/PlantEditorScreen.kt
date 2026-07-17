@file:OptIn(androidx.compose.material3.ExperimentalMaterial3Api::class)
@file:Suppress(
    "MagicNumber",
    "LongMethod",
    "TooManyFunctions",
    "LongParameterList",
    "CyclomaticComplexMethod"
)

package com.wateria.revamp.feature.editor

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
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
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.role
import androidx.compose.ui.semantics.selected
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.wateria.R
import com.wateria.domain.model.PlantIcon
import com.wateria.domain.validation.PlantNameValidator
import com.wateria.revamp.design.PlantIconCategory
import com.wateria.revamp.design.PlantIconOption
import com.wateria.revamp.design.WateriaBlue
import com.wateria.revamp.design.WateriaDialog
import com.wateria.revamp.design.WateriaGreenDivider
import com.wateria.revamp.design.WateriaNumberFont
import com.wateria.revamp.design.WateriaOrange
import com.wateria.revamp.design.WateriaPanelShape
import com.wateria.revamp.design.WateriaPillButton
import com.wateria.revamp.design.WateriaRed
import com.wateria.revamp.design.WateriaScreenHeader
import com.wateria.revamp.design.plantIconOptions
import com.wateria.revamp.design.toDrawableRes

private data class PlantEditorActions(
    val navigateBack: () -> Unit,
    val changeName: (String) -> Unit,
    val selectIcon: (PlantIcon) -> Unit,
    val changeInterval: (Int) -> Unit,
    val changeNextWatering: (Int) -> Unit,
    val save: () -> Unit,
    val delete: () -> Unit
)

@Composable
fun PlantEditorRoute(
    onNavigateBack: () -> Unit,
    onFinished: () -> Unit,
    viewModel: PlantEditorViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    LaunchedEffect(viewModel) {
        viewModel.effects.collect { effect ->
            when (effect) {
                PlantEditorEffect.Saved,
                PlantEditorEffect.Deleted -> onFinished()
            }
        }
    }

    PlantEditorScreen(
        uiState = uiState,
        actions =
            PlantEditorActions(
                navigateBack = onNavigateBack,
                changeName = viewModel::updateName,
                selectIcon = viewModel::selectIcon,
                changeInterval = viewModel::changeWateringInterval,
                changeNextWatering = viewModel::changeNextWatering,
                save = viewModel::save,
                delete = viewModel::delete
            )
    )
}

@Composable
private fun PlantEditorScreen(uiState: PlantEditorUiState, actions: PlantEditorActions) {
    var showIconPicker by rememberSaveable { mutableStateOf(false) }
    var showDeleteConfirmation by rememberSaveable { mutableStateOf(false) }
    var showDiscardConfirmation by rememberSaveable { mutableStateOf(false) }
    val attemptBack = {
        if (uiState.hasUnsavedChanges) showDiscardConfirmation = true else actions.navigateBack()
    }

    BackHandler(enabled = uiState.hasUnsavedChanges) { showDiscardConfirmation = true }

    Scaffold(
        containerColor = MaterialTheme.colorScheme.primary,
        topBar = {
            WateriaScreenHeader(
                title =
                    stringResource(
                        if (uiState.isEditing) {
                            R.string.editPlantActivityTitle
                        } else {
                            R.string.addPlantActivitytitle
                        }
                    ),
                onBack = attemptBack
            )
        },
        bottomBar = {
            if (!uiState.isLoading && uiState.error != PlantEditorError.PLANT_NOT_FOUND) {
                EditorBottomActions(
                    isEditing = uiState.isEditing,
                    isSaving = uiState.isSaving,
                    onSecondary = {
                        if (uiState.isEditing) {
                            showDeleteConfirmation = true
                        } else {
                            attemptBack()
                        }
                    },
                    onSave = actions.save
                )
            }
        }
    ) { paddingValues ->
        when {
            uiState.isLoading -> EditorLoading(Modifier.padding(paddingValues))

            uiState.error == PlantEditorError.PLANT_NOT_FOUND ->
                MissingPlant(actions.navigateBack, Modifier.padding(paddingValues))

            else ->
                EditorForm(
                    uiState = uiState,
                    actions = actions,
                    onOpenIconPicker = { showIconPicker = true },
                    modifier = Modifier.padding(paddingValues)
                )
        }
    }

    if (showIconPicker) {
        PlantIconPicker(
            selectedIcon = uiState.selectedIcon,
            onDismiss = { showIconPicker = false },
            onSelected = { icon ->
                actions.selectIcon(icon)
                showIconPicker = false
            }
        )
    }
    if (showDeleteConfirmation) {
        ConfirmDialog(
            title = stringResource(R.string.edit_plant_delete_dialog_title),
            body = stringResource(R.string.edit_plant_delete_dialog_text),
            confirmLabel = stringResource(R.string.edit_plant_delete_dialog_accept),
            destructive = true,
            onDismiss = { showDeleteConfirmation = false },
            onConfirm = {
                showDeleteConfirmation = false
                actions.delete()
            }
        )
    }
    if (showDiscardConfirmation) {
        ConfirmDialog(
            title =
                stringResource(
                    if (uiState.isEditing) {
                        R.string.edit_plant_exit_dialog_title
                    } else {
                        R.string.new_plant_exit_dialog_title
                    }
                ),
            body =
                stringResource(
                    if (uiState.isEditing) {
                        R.string.edit_plant_exit_dialog_text
                    } else {
                        R.string.new_plant_exit_dialog_text
                    }
                ),
            confirmLabel =
                stringResource(
                    if (uiState.isEditing) {
                        R.string.revamp_discard
                    } else {
                        R.string.new_plant_exit_dialog_accept
                    }
                ),
            destructive = true,
            onDismiss = { showDiscardConfirmation = false },
            onConfirm = actions.navigateBack
        )
    }
}

@Composable
private fun EditorBottomActions(
    isEditing: Boolean,
    isSaving: Boolean,
    onSecondary: () -> Unit,
    onSave: () -> Unit
) {
    Surface(color = MaterialTheme.colorScheme.surface, shadowElevation = 8.dp) {
        Row(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier.fillMaxWidth().padding(horizontal = 8.dp, vertical = 8.dp)
        ) {
            WateriaPillButton(
                text =
                    stringResource(
                        if (isEditing) {
                            R.string.edit_plant_delete_button_text
                        } else {
                            R.string.edit_plant_delete_dialog_cancel
                        }
                    ),
                onClick = onSecondary,
                enabled = !isSaving,
                filled = false,
                color = WateriaRed,
                modifier = Modifier.weight(1f)
            )
            WateriaPillButton(
                text =
                    stringResource(
                        if (isEditing) {
                            R.string.edit_plant_save_button_text
                        } else {
                            R.string.newPlantAcceptButtonText
                        }
                    ),
                onClick = onSave,
                enabled = !isSaving,
                modifier = Modifier.weight(1f)
            )
        }
    }
}

@Composable
private fun EditorForm(
    uiState: PlantEditorUiState,
    actions: PlantEditorActions,
    onOpenIconPicker: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(modifier = modifier.fillMaxSize()) {
        Surface(
            color = MaterialTheme.colorScheme.surface,
            shape = WateriaPanelShape,
            modifier =
                Modifier
                    .fillMaxSize()
                    .padding(start = 8.dp, top = 108.dp, end = 8.dp, bottom = 6.dp)
        ) {
            Column(
                modifier =
                    Modifier
                        .fillMaxSize()
                        .verticalScroll(rememberScrollState())
                        .padding(top = 72.dp, bottom = 16.dp)
            ) {
                PlantNameField(uiState, actions.changeName)
                WateriaGreenDivider()
                WheelNumberSection(
                    icon = R.drawable.icon_watering_blue,
                    title = stringResource(R.string.new_plant_options_watering_frequency_text),
                    explanation =
                        stringResource(R.string.new_plant_options_watering_freq_explanation),
                    value = uiState.wateringIntervalDays,
                    color = WateriaBlue,
                    onDecrease = { actions.changeInterval(-1) },
                    onIncrease = { actions.changeInterval(1) }
                )
                WateriaGreenDivider()
                WheelNumberSection(
                    icon = R.drawable.icon_clock,
                    title = stringResource(R.string.new_plant_options_first_watering_text),
                    explanation =
                        stringResource(
                            if (uiState.isEditing) {
                                R.string.edit_plant_options_first_watering_explanation
                            } else {
                                R.string.new_plant_options_first_watering_explanation
                            }
                        ),
                    value = uiState.nextWateringDays,
                    color = WateriaRed,
                    onDecrease = { actions.changeNextWatering(-1) },
                    onIncrease = { actions.changeNextWatering(1) }
                )

                val generalError = editorErrorText(uiState.error)
                if (generalError.isNotEmpty()) {
                    Text(
                        text = generalError,
                        color = MaterialTheme.colorScheme.error,
                        style = MaterialTheme.typography.bodyMedium,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }
        }

        Surface(
            onClick = onOpenIconPicker,
            shape = CircleShape,
            color = Color.White,
            border = BorderStroke(3.dp, MaterialTheme.colorScheme.primary.copy(alpha = 0.25f)),
            shadowElevation = 5.dp,
            modifier = Modifier.align(Alignment.TopCenter).size(180.dp)
        ) {
            Box(contentAlignment = Alignment.Center) {
                Image(
                    painter = painterResource(uiState.selectedIcon.toDrawableRes()),
                    contentDescription = stringResource(R.string.revamp_choose_icon),
                    modifier = Modifier.fillMaxSize().padding(40.dp)
                )
            }
        }
    }
}

@Composable
private fun PlantNameField(uiState: PlantEditorUiState, onValueChange: (String) -> Unit) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.fillMaxWidth().height(80.dp).padding(start = 6.dp)
    ) {
        Image(
            painter = painterResource(R.drawable.icon_name_tag),
            contentDescription = null,
            modifier = Modifier.size(50.dp).padding(10.dp)
        )
        TextField(
            value = uiState.name,
            onValueChange = onValueChange,
            label = { Text(stringResource(R.string.new_plant_options_name_text)) },
            placeholder = { Text(stringResource(R.string.new_plant_options_name_hint)) },
            singleLine = true,
            isError =
                uiState.error == PlantEditorError.NAME_REQUIRED ||
                    uiState.error == PlantEditorError.NAME_TOO_LONG,
            supportingText = {
                Text(
                    text = "${uiState.name.length}/${PlantNameValidator.MAX_LENGTH}",
                    modifier = Modifier.fillMaxWidth(),
                    textAlign = TextAlign.End
                )
            },
            colors =
                TextFieldDefaults.colors(
                    focusedContainerColor = Color.Transparent,
                    unfocusedContainerColor = Color.Transparent,
                    disabledContainerColor = Color.Transparent,
                    focusedIndicatorColor = MaterialTheme.colorScheme.primary,
                    unfocusedIndicatorColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.45f)
                ),
            modifier = Modifier.weight(1f)
        )
    }
}

@Composable
private fun WheelNumberSection(
    icon: Int,
    title: String,
    explanation: String,
    value: Int,
    color: Color,
    onDecrease: () -> Unit,
    onIncrease: () -> Unit
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.fillMaxWidth().height(120.dp).padding(start = 6.dp)
    ) {
        Image(
            painter = painterResource(icon),
            contentDescription = null,
            modifier = Modifier.size(50.dp).padding(10.dp)
        )
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = title,
                style = MaterialTheme.typography.titleMedium.copy(fontSize = 18.sp),
                color = color
            )
            Text(
                text = explanation,
                style = MaterialTheme.typography.bodySmall.copy(fontSize = 12.sp),
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.62f)
            )
        }
        WheelNumberPicker(
            value = value,
            color = color,
            onDecrease = onDecrease,
            onIncrease = onIncrease
        )
        Text(
            text = stringResource(R.string.new_plant_options_watering_frequency_text_days),
            style = MaterialTheme.typography.bodyLarge.copy(fontSize = 16.sp),
            color = color,
            modifier = Modifier.padding(end = 16.dp)
        )
    }
}

@Composable
private fun WheelNumberPicker(
    value: Int,
    color: Color,
    onDecrease: () -> Unit,
    onIncrease: () -> Unit
) {
    Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.width(50.dp)) {
        WheelNumber(
            value = (value - 1).coerceAtLeast(0),
            color = color.copy(alpha = 0.30f),
            description = stringResource(R.string.revamp_decrease),
            onClick = onDecrease
        )
        Spacer(
            Modifier
                .fillMaxWidth()
                .height(2.dp)
                .background(color.copy(alpha = 0.45f))
                .clickable(onClick = onDecrease)
        )
        Text(
            text = value.toString(),
            color = color,
            style =
                MaterialTheme.typography.displayMedium.copy(
                    fontFamily = WateriaNumberFont,
                    fontSize = 37.sp,
                    lineHeight = 43.sp
                ),
            textAlign = TextAlign.Center
        )
        Spacer(
            Modifier
                .fillMaxWidth()
                .height(2.dp)
                .background(color.copy(alpha = 0.45f))
                .clickable(onClick = onIncrease)
        )
        WheelNumber(
            value = value + 1,
            color = color.copy(alpha = 0.30f),
            description = stringResource(R.string.revamp_increase),
            onClick = onIncrease
        )
    }
}

@Composable
private fun WheelNumber(value: Int, color: Color, description: String, onClick: () -> Unit) {
    Box(
        contentAlignment = Alignment.Center,
        modifier =
            Modifier
                .fillMaxWidth()
                .height(35.dp)
                .semantics {
                    contentDescription = description
                    role = Role.Button
                }
                .clickable(onClick = onClick)
    ) {
        Text(
            text = value.toString(),
            color = color,
            style = MaterialTheme.typography.titleLarge.copy(fontFamily = WateriaNumberFont)
        )
    }
}

@Composable
private fun PlantIconPicker(
    selectedIcon: PlantIcon,
    onDismiss: () -> Unit,
    onSelected: (PlantIcon) -> Unit
) {
    ModalBottomSheet(
        onDismissRequest = onDismiss,
        containerColor = Color.White,
        shape = RoundedCornerShape(topStart = 32.dp, topEnd = 32.dp),
        dragHandle = null
    ) {
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier.fillMaxWidth().height(60.dp).padding(top = 10.dp)
        ) {
            Text(
                text = stringResource(R.string.dialog_title).uppercase(),
                style = MaterialTheme.typography.headlineLarge.copy(fontSize = 30.sp),
                color = WateriaOrange,
                textAlign = TextAlign.Center
            )
        }
        LazyVerticalGrid(
            columns = GridCells.Fixed(4),
            modifier = Modifier.fillMaxWidth().heightIn(max = 590.dp),
            contentPadding = PaddingValues(start = 12.dp, end = 12.dp, bottom = 32.dp),
            horizontalArrangement = Arrangement.spacedBy(4.dp),
            verticalArrangement = Arrangement.spacedBy(2.dp)
        ) {
            PlantIconCategory.entries.forEach { category ->
                item(span = { GridItemSpan(maxLineSpan) }) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.fillMaxWidth().height(30.dp).padding(horizontal = 10.dp)
                    ) {
                        WateriaGreenDivider(Modifier.weight(1f))
                        Text(
                            text = categoryLabel(category),
                            modifier = Modifier.padding(horizontal = 8.dp),
                            style = MaterialTheme.typography.titleMedium,
                            color = MaterialTheme.colorScheme.primary,
                            textAlign = TextAlign.Center
                        )
                        WateriaGreenDivider(Modifier.weight(1f))
                    }
                }
                items(
                    items = plantIconOptions.filter { option -> option.category == category },
                    key = { option -> option.icon.key }
                ) { option ->
                    PlantIconChoice(option, selectedIcon, onSelected)
                }
            }
        }
    }
}

@Composable
private fun PlantIconChoice(
    option: PlantIconOption,
    selectedIcon: PlantIcon,
    onSelected: (PlantIcon) -> Unit
) {
    val isSelected = option.icon == selectedIcon
    val selectionDescription = stringResource(R.string.revamp_select_icon, option.icon.key)
    Surface(
        onClick = { onSelected(option.icon) },
        shape = CircleShape,
        color = if (isSelected) WateriaOrange.copy(alpha = 0.20f) else Color.Transparent,
        border = if (isSelected) BorderStroke(2.dp, WateriaOrange) else null,
        modifier =
            Modifier.semantics {
                selected = isSelected
                role = Role.RadioButton
                contentDescription = selectionDescription
            }
    ) {
        Box(
            modifier = Modifier.fillMaxWidth().height(58.dp),
            contentAlignment = Alignment.Center
        ) {
            Image(
                painter = painterResource(option.drawableRes),
                contentDescription = null,
                modifier = Modifier.size(58.dp).padding(8.dp)
            )
        }
    }
}

@Composable
private fun ConfirmDialog(
    title: String,
    body: String,
    confirmLabel: String,
    destructive: Boolean,
    onDismiss: () -> Unit,
    onConfirm: () -> Unit
) {
    val actionColor = if (destructive) WateriaRed else MaterialTheme.colorScheme.primary
    WateriaDialog(onDismissRequest = onDismiss) {
        Text(
            text = title.uppercase(),
            style = MaterialTheme.typography.headlineLarge.copy(fontSize = 24.sp),
            color = actionColor,
            textAlign = TextAlign.Center
        )
        Spacer(Modifier.height(10.dp))
        Text(
            text = body,
            style = MaterialTheme.typography.bodyLarge.copy(fontSize = 16.sp),
            textAlign = TextAlign.Center
        )
        Spacer(Modifier.height(18.dp))
        WateriaPillButton(
            text = confirmLabel,
            onClick = onConfirm,
            color = actionColor,
            modifier = Modifier.fillMaxWidth()
        )
        WateriaPillButton(
            text = stringResource(R.string.edit_plant_delete_dialog_cancel),
            onClick = onDismiss,
            color = MaterialTheme.colorScheme.primary,
            filled = false,
            modifier = Modifier.fillMaxWidth()
        )
    }
}

@Composable
private fun EditorLoading(modifier: Modifier = Modifier) {
    Box(modifier = modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        CircularProgressIndicator(color = Color.White)
    }
}

@Composable
private fun MissingPlant(onNavigateBack: () -> Unit, modifier: Modifier = Modifier) {
    Column(
        modifier = modifier.fillMaxSize().padding(32.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            stringResource(R.string.revamp_plant_not_found),
            style = MaterialTheme.typography.titleLarge,
            color = Color.White,
            textAlign = TextAlign.Center
        )
        Spacer(Modifier.height(20.dp))
        WateriaPillButton(
            text = stringResource(R.string.revamp_go_back),
            onClick = onNavigateBack,
            color = WateriaOrange
        )
    }
}

@Composable
private fun editorErrorText(error: PlantEditorError?): String = when (error) {
    PlantEditorError.NAME_REQUIRED -> stringResource(R.string.revamp_name_required)
    PlantEditorError.NAME_TOO_LONG -> stringResource(R.string.revamp_name_too_long)
    PlantEditorError.PLANT_NOT_FOUND -> stringResource(R.string.revamp_plant_not_found)
    PlantEditorError.SAVE_FAILED -> stringResource(R.string.revamp_save_failed)
    PlantEditorError.DELETE_FAILED -> stringResource(R.string.revamp_delete_failed)
    null -> ""
}

@Composable
private fun categoryLabel(category: PlantIconCategory): String = stringResource(
    when (category) {
        PlantIconCategory.COMMON -> R.string.dialog_text_common
        PlantIconCategory.FLOWER -> R.string.dialog_text_flower
        PlantIconCategory.CACTUS -> R.string.dialog_text_cactus
        PlantIconCategory.TREE -> R.string.dialog_text_trees
        PlantIconCategory.PROPAGATION -> R.string.dialog_text_propagation
        PlantIconCategory.VEGETABLE -> R.string.dialog_text_veggies
    }
)
