package com.wateria.revamp.feature.plants

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.role
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp
import com.wateria.R
import com.wateria.revamp.design.WateriaBlue
import com.wateria.revamp.design.WateriaPillShape

@Composable
internal fun WaterPlantButton(
    plant: PlantCardUiState,
    onWater: () -> Unit,
    modifier: Modifier = Modifier
) {
    val description = stringResource(R.string.revamp_water_plant, plant.name)
    Surface(
        onClick = onWater,
        enabled = !plant.isBusy,
        shape = WateriaPillShape,
        color = WateriaBlue,
        contentColor = Color.White,
        modifier =
            modifier
                .size(48.dp)
                .semantics {
                    contentDescription = description
                    role = Role.Button
                }
    ) {
        Box(contentAlignment = Alignment.Center) {
            if (plant.isBusy) {
                CircularProgressIndicator(
                    color = Color.White,
                    modifier = Modifier.size(22.dp),
                    strokeWidth = 2.dp
                )
            } else {
                Image(
                    painter = painterResource(R.drawable.icon_watering),
                    contentDescription = null,
                    modifier = Modifier.size(36.dp)
                )
            }
        }
    }
}
