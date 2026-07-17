@file:Suppress("MagicNumber", "LongParameterList")

package com.wateria.revamp.feature.tips

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.wateria.R
import com.wateria.revamp.design.WateriaDeepGreen
import com.wateria.revamp.design.WateriaOrange
import com.wateria.revamp.design.WateriaPanelShape
import com.wateria.revamp.design.WateriaPillButton
import com.wateria.revamp.design.WateriaPillShape
import com.wateria.revamp.design.WateriaRed

@Composable
fun TipRoute(onNavigateBack: () -> Unit, viewModel: TipViewModel = hiltViewModel()) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    TipScreen(uiState = uiState, onNavigateBack = onNavigateBack)
}

@Composable
private fun TipScreen(uiState: TipUiState, onNavigateBack: () -> Unit) {
    Surface(modifier = Modifier.fillMaxSize(), color = WateriaDeepGreen) {
        when {
            uiState.isLoading ->
                Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator(color = Color.White)
                }

            uiState.failed ->
                Box(Modifier.fillMaxSize().padding(12.dp), contentAlignment = Alignment.Center) {
                    Surface(color = Color.White, shape = WateriaPanelShape) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            modifier = Modifier.padding(24.dp)
                        ) {
                            Text(
                                stringResource(R.string.revamp_tip_failed),
                                textAlign = TextAlign.Center
                            )
                            Spacer(Modifier.height(18.dp))
                            WateriaPillButton(
                                text = stringResource(R.string.revamp_go_back),
                                onClick = onNavigateBack
                            )
                        }
                    }
                }

            else -> TipContent(uiState, onNavigateBack)
        }
    }
}

@Composable
private fun TipContent(uiState: TipUiState, onNavigateBack: () -> Unit) {
    val tip = tipContent(uiState.index)
    val stackActions = LocalDensity.current.fontScale >= 1.3f
    Box(modifier = Modifier.fillMaxSize().padding(horizontal = 12.dp, vertical = 18.dp)) {
        Surface(
            color = Color.White,
            shape = WateriaPanelShape,
            shadowElevation = 14.dp,
            modifier = Modifier.align(Alignment.Center).fillMaxWidth().heightIn(max = 680.dp)
        ) {
            Column(
                modifier = Modifier.padding(horizontal = 24.dp, vertical = 24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = stringResource(R.string.tip_title).uppercase(),
                    style = MaterialTheme.typography.headlineLarge.copy(fontSize = 24.sp),
                    color = MaterialTheme.colorScheme.primary,
                    textAlign = TextAlign.Center
                )
                Spacer(Modifier.height(30.dp))
                Image(
                    painter = painterResource(tip.imageRes),
                    contentDescription = null,
                    modifier = Modifier.fillMaxWidth().height(110.dp),
                    contentScale = ContentScale.Fit
                )
                Spacer(Modifier.height(30.dp))
                Text(
                    text = stringResource(tip.titleRes),
                    style = MaterialTheme.typography.bodyLarge.copy(fontSize = 16.sp),
                    color = MaterialTheme.colorScheme.primary,
                    textAlign = TextAlign.Center
                )
                Spacer(Modifier.height(8.dp))
                Text(
                    text = stringResource(tip.bodyRes),
                    style = MaterialTheme.typography.bodyLarge.copy(fontSize = 16.sp),
                    color = MaterialTheme.colorScheme.onSurface,
                    textAlign = TextAlign.Center
                )
                Spacer(Modifier.height(26.dp))
                Text(
                    text =
                        stringResource(
                            R.string.tip_unlock_text,
                            uiState.hoursUntilNext,
                            uiState.minutesUntilNext
                        ),
                    style = MaterialTheme.typography.bodySmall.copy(fontSize = 12.sp),
                    color = MaterialTheme.colorScheme.primary
                )
                Spacer(Modifier.height(8.dp))
                TipActions(stackActions, onNavigateBack)
            }
        }
    }
}

@Composable
private fun TipActions(stacked: Boolean, onNavigateBack: () -> Unit) {
    if (stacked) {
        TipAction(
            icon = R.drawable.icon_thumb_down_red,
            label = stringResource(R.string.tip_button_dislike),
            color = WateriaRed,
            filled = false,
            onClick = onNavigateBack,
            modifier = Modifier.fillMaxWidth()
        )
        TipAction(
            icon = R.drawable.icon_thumb_up,
            label = stringResource(R.string.tip_button),
            color = MaterialTheme.colorScheme.primary,
            filled = true,
            onClick = onNavigateBack,
            modifier = Modifier.fillMaxWidth()
        )
    } else {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(5.dp)
        ) {
            TipAction(
                icon = R.drawable.icon_thumb_down_red,
                label = stringResource(R.string.tip_button_dislike),
                color = WateriaRed,
                filled = false,
                onClick = onNavigateBack,
                modifier = Modifier.weight(1f)
            )
            TipAction(
                icon = R.drawable.icon_thumb_up,
                label = stringResource(R.string.tip_button),
                color = MaterialTheme.colorScheme.primary,
                filled = true,
                onClick = onNavigateBack,
                modifier = Modifier.weight(1f)
            )
        }
    }
}

@Composable
private fun TipAction(
    icon: Int,
    label: String,
    color: Color,
    filled: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        onClick = onClick,
        shape = WateriaPillShape,
        color = if (filled) color else Color.White,
        contentColor = if (filled) Color.White else color,
        border = BorderStroke(2.dp, color),
        modifier = modifier.height(45.dp)
    ) {
        Row(
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(horizontal = 10.dp)
        ) {
            Image(
                painter = painterResource(icon),
                contentDescription = null,
                modifier = Modifier.size(42.dp)
            )
            Spacer(Modifier.size(5.dp))
            Text(label, style = MaterialTheme.typography.titleLarge.copy(fontSize = 20.sp))
        }
    }
}

private data class TipContent(val titleRes: Int, val bodyRes: Int, val imageRes: Int)

private fun tipContent(index: Int): TipContent = when (index) {
    0 ->
        TipContent(
            R.string.revamp_tip_0_title,
            R.string.revamp_tip_0_body,
            R.drawable.icon_fertilizer
        )

    1 ->
        TipContent(
            R.string.revamp_tip_1_title,
            R.string.revamp_tip_1_body,
            R.drawable.icon_pots
        )

    2 ->
        TipContent(
            R.string.revamp_tip_2_title,
            R.string.revamp_tip_2_body,
            R.drawable.icon_spray
        )

    3 ->
        TipContent(
            R.string.revamp_tip_3_title,
            R.string.revamp_tip_3_body,
            R.drawable.icon_brain
        )

    4 ->
        TipContent(
            R.string.revamp_tip_4_title,
            R.string.revamp_tip_4_body,
            R.drawable.icon_fog
        )

    5 ->
        TipContent(
            R.string.revamp_tip_5_title,
            R.string.revamp_tip_5_body,
            R.drawable.icon_soil
        )

    else ->
        TipContent(
            R.string.revamp_tip_6_title,
            R.string.revamp_tip_6_body,
            R.drawable.icon_window
        )
}
