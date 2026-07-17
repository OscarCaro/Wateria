@file:OptIn(androidx.compose.material3.ExperimentalMaterial3Api::class)
@file:Suppress("MagicNumber", "LongMethod", "TooManyFunctions")

package com.wateria.revamp.feature.editor

import androidx.activity.compose.BackHandler
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
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.wateria.R
import com.wateria.domain.model.PlantIcon
import com.wateria.domain.validation.PlantNameValidator
import com.wateria.revamp.design.PlantIconCategory
import com.wateria.revamp.design.PlantIconOption
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
        topBar = {
            EditorTopBar(
                isEditing = uiState.isEditing,
                isSaving = uiState.isSaving,
                onBack = attemptBack,
                onSave = actions.save
            )
        }
    ) { paddingValues ->
        when {
            uiState.isLoading -> EditorLoading(Modifier.padding(paddingValues))

            uiState.error == PlantEditorError.PLANT_NOT_FOUND ->
                MissingPlant(actions.navigateBack, Modifier.padding(paddingValues))

            else ->
                EditorForm(
                    uiState = uiState,
                    actions = actions.copy(delete = { showDeleteConfirmation = true }),
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
            onDismiss = { showDiscardConfirmation = false },
            onConfirm = actions.navigateBack
        )
    }
}

@Composable
private fun EditorTopBar(
    isEditing: Boolean,
    isSaving: Boolean,
    onBack: () -> Unit,
    onSave: () -> Unit
) {
    TopAppBar(
        navigationIcon = {
            TextButton(onClick = onBack) {
                Text(
                    text = "‹",
                    style = MaterialTheme.typography.headlineMedium,
                    color = MaterialTheme.colorScheme.onPrimary
                )
            }
        },
        title = {
            Text(
                stringResource(
                    if (isEditing) {
                        R.string.editPlantActivityTitle
                    } else {
                        R.string.addPlantActivitytitle
                    }
                )
            )
        },
        actions = {
            TextButton(onClick = onSave, enabled = !isSaving) {
                if (isSaving) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(20.dp),
                        color = MaterialTheme.colorScheme.onPrimary,
                        strokeWidth = 2.dp
                    )
                } else {
                    Text(
                        stringResource(
                            if (isEditing) {
                                R.string.edit_plant_save_button_text
                            } else {
                                R.string.newPlantAcceptButtonText
                            }
                        ),
                        color = MaterialTheme.colorScheme.onPrimary
                    )
                }
            }
        },
        colors =
            TopAppBarDefaults.topAppBarColors(
                containerColor = MaterialTheme.colorScheme.primary,
                titleContentColor = MaterialTheme.colorScheme.onPrimary
            )
    )
}

@Composable
private fun EditorForm(
    uiState: PlantEditorUiState,
    actions: PlantEditorActions,
    onOpenIconPicker: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier =
            modifier.fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 20.dp, vertical = 18.dp),
        verticalArrangement = Arrangement.spacedBy(22.dp)
    ) {
        OutlinedTextField(
            value = uiState.name,
            onValueChange = actions.changeName,
            modifier = Modifier.fillMaxWidth(),
            label = { Text(stringResource(R.string.new_plant_options_name_text)) },
            placeholder = { Text(stringResource(R.string.new_plant_options_name_hint)) },
            singleLine = true,
            isError =
                uiState.error == PlantEditorError.NAME_REQUIRED ||
                    uiState.error == PlantEditorError.NAME_TOO_LONG,
            supportingText = {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(editorErrorText(uiState.error))
                    Text("${uiState.name.length}/${PlantNameValidator.MAX_LENGTH}")
                }
            }
        )

        SectionTitle(stringResource(R.string.new_plant_options_plant_icon_text))
        Card(
            modifier = Modifier.fillMaxWidth().clickable(onClick = onOpenIconPicker),
            colors =
                CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer
                )
        ) {
            Row(
                modifier = Modifier.fillMaxWidth().padding(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Image(
                    painter = painterResource(uiState.selectedIcon.toDrawableRes()),
                    contentDescription = null,
                    modifier = Modifier.size(72.dp)
                )
                Spacer(Modifier.width(16.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        stringResource(R.string.revamp_choose_icon),
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                    Text(
                        stringResource(R.string.revamp_tap_to_change),
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                Text("›", style = MaterialTheme.typography.headlineMedium)
            }
        }

        HorizontalDivider()
        NumberSelector(
            title = stringResource(R.string.new_plant_options_watering_frequency_text),
            explanation = stringResource(R.string.new_plant_options_watering_freq_explanation),
            value = uiState.wateringIntervalDays,
            onDecrease = { actions.changeInterval(-1) },
            onIncrease = { actions.changeInterval(1) }
        )
        NumberSelector(
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
            onDecrease = { actions.changeNextWatering(-1) },
            onIncrease = { actions.changeNextWatering(1) }
        )

        val generalError = editorErrorText(uiState.error)
        if (generalError.isNotEmpty() &&
            uiState.error != PlantEditorError.NAME_REQUIRED &&
            uiState.error != PlantEditorError.NAME_TOO_LONG
        ) {
            Text(
                text = generalError,
                color = MaterialTheme.colorScheme.error,
                style = MaterialTheme.typography.bodyMedium
            )
        }

        if (uiState.isEditing) {
            Spacer(Modifier.height(6.dp))
            OutlinedButton(
                onClick = actions.delete,
                enabled = !uiState.isSaving,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    stringResource(R.string.edit_plant_delete_button_text),
                    color = MaterialTheme.colorScheme.error
                )
            }
        }
        Spacer(Modifier.height(32.dp))
    }
}

@Composable
private fun SectionTitle(text: String) {
    Text(
        text = text,
        style = MaterialTheme.typography.titleMedium,
        color = MaterialTheme.colorScheme.primary,
        fontWeight = FontWeight.Bold
    )
}

@Composable
private fun NumberSelector(
    title: String,
    explanation: String,
    value: Int,
    onDecrease: () -> Unit,
    onIncrease: () -> Unit
) {
    Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
        SectionTitle(title)
        Text(
            text = explanation,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Row(
            modifier = Modifier.fillMaxWidth().padding(top = 8.dp),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            NumberButton(
                label = "−",
                description = stringResource(R.string.revamp_decrease),
                onClick = onDecrease
            )
            Text(
                text = value.toString(),
                modifier = Modifier.width(72.dp),
                textAlign = TextAlign.Center,
                style = MaterialTheme.typography.headlineMedium,
                color = MaterialTheme.colorScheme.primary,
                fontWeight = FontWeight.Bold
            )
            NumberButton(
                label = "+",
                description = stringResource(R.string.revamp_increase),
                onClick = onIncrease
            )
            Spacer(Modifier.width(8.dp))
            Text(stringResource(R.string.new_plant_options_watering_frequency_text_days))
        }
    }
}

@Composable
private fun NumberButton(label: String, description: String, onClick: () -> Unit) {
    OutlinedButton(
        onClick = onClick,
        contentPadding = PaddingValues(0.dp),
        modifier = Modifier.size(48.dp).semantics { contentDescription = description }
    ) {
        Text(label, style = MaterialTheme.typography.headlineSmall)
    }
}

@Composable
private fun PlantIconPicker(
    selectedIcon: PlantIcon,
    onDismiss: () -> Unit,
    onSelected: (PlantIcon) -> Unit
) {
    ModalBottomSheet(onDismissRequest = onDismiss) {
        Text(
            text = stringResource(R.string.dialog_title),
            modifier = Modifier.fillMaxWidth().padding(horizontal = 20.dp),
            style = MaterialTheme.typography.headlineSmall,
            color = MaterialTheme.colorScheme.primary,
            textAlign = TextAlign.Center
        )
        Spacer(Modifier.height(12.dp))
        LazyVerticalGrid(
            columns = GridCells.Fixed(4),
            modifier = Modifier.fillMaxWidth().heightIn(max = 560.dp),
            contentPadding = PaddingValues(start = 12.dp, end = 12.dp, bottom = 32.dp),
            horizontalArrangement = Arrangement.spacedBy(6.dp),
            verticalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            PlantIconCategory.entries.forEach { category ->
                item(span = { GridItemSpan(maxLineSpan) }) {
                    Text(
                        text = categoryLabel(category),
                        modifier = Modifier.fillMaxWidth().padding(top = 14.dp, bottom = 6.dp),
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.primary,
                        textAlign = TextAlign.Center
                    )
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
    Card(
        modifier =
            Modifier.semantics {
                selected = isSelected
                role = Role.RadioButton
                contentDescription = selectionDescription
            },
        onClick = { onSelected(option.icon) },
        colors =
            CardDefaults.cardColors(
                containerColor =
                    if (isSelected) {
                        MaterialTheme.colorScheme.secondaryContainer
                    } else {
                        Color.Transparent
                    }
            )
    ) {
        Box(
            modifier = Modifier.fillMaxWidth().padding(10.dp),
            contentAlignment = Alignment.Center
        ) {
            Image(
                painter = painterResource(option.drawableRes),
                contentDescription = null,
                modifier = Modifier.size(52.dp)
            )
        }
    }
}

@Composable
private fun ConfirmDialog(
    title: String,
    body: String,
    confirmLabel: String,
    onDismiss: () -> Unit,
    onConfirm: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(title) },
        text = { Text(body) },
        confirmButton = {
            TextButton(onClick = onConfirm) {
                Text(confirmLabel, color = MaterialTheme.colorScheme.error)
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
private fun EditorLoading(modifier: Modifier = Modifier) {
    Box(modifier = modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        CircularProgressIndicator()
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
            textAlign = TextAlign.Center
        )
        Spacer(Modifier.height(20.dp))
        Button(onClick = onNavigateBack) { Text(stringResource(R.string.revamp_go_back)) }
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
