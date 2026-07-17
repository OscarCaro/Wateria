package com.wateria.revamp.feature.plants

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.pluralStringResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.wateria.R
import com.wateria.revamp.design.toDrawableRes

@Composable
internal fun PlantSummary(plant: PlantCardUiState, modifier: Modifier = Modifier) {
    Row(modifier = modifier, verticalAlignment = Alignment.CenterVertically) {
        Image(
            painter = painterResource(plant.icon.toDrawableRes()),
            contentDescription = plant.name,
            contentScale = ContentScale.Fit,
            modifier = Modifier.size(68.dp).clip(MaterialTheme.shapes.large).padding(4.dp)
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
    }
}

@Composable
internal fun WaterPlantButton(
    plant: PlantCardUiState,
    onWater: () -> Unit,
    modifier: Modifier = Modifier
) {
    FilledTonalButton(onClick = onWater, enabled = !plant.isBusy, modifier = modifier) {
        if (plant.isBusy) {
            CircularProgressIndicator(modifier = Modifier.size(18.dp), strokeWidth = 2.dp)
        } else {
            Text(stringResource(R.string.revamp_water))
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
