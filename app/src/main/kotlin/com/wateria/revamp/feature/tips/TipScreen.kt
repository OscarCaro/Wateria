@file:OptIn(androidx.compose.material3.ExperimentalMaterial3Api::class)
@file:Suppress("MagicNumber")

package com.wateria.revamp.feature.tips

import androidx.compose.foundation.Image
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.wateria.R
import com.wateria.revamp.design.WateriaBackButton

@Composable
fun TipRoute(onNavigateBack: () -> Unit, viewModel: TipViewModel = hiltViewModel()) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    TipScreen(uiState = uiState, onNavigateBack = onNavigateBack)
}

@Composable
private fun TipScreen(uiState: TipUiState, onNavigateBack: () -> Unit) {
    Scaffold(
        topBar = {
            TopAppBar(
                navigationIcon = {
                    WateriaBackButton(onNavigateBack)
                },
                title = { Text(stringResource(R.string.tip_title)) },
                colors =
                    TopAppBarDefaults.topAppBarColors(
                        containerColor = MaterialTheme.colorScheme.primary,
                        titleContentColor = MaterialTheme.colorScheme.onPrimary
                    )
            )
        }
    ) { padding ->
        when {
            uiState.isLoading ->
                Box(Modifier.fillMaxSize().padding(padding), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator()
                }

            uiState.failed ->
                Box(Modifier.fillMaxSize().padding(32.dp), contentAlignment = Alignment.Center) {
                    Text(stringResource(R.string.revamp_tip_failed), textAlign = TextAlign.Center)
                }

            else -> TipContent(uiState, onNavigateBack, Modifier.padding(padding))
        }
    }
}

@Composable
private fun TipContent(
    uiState: TipUiState,
    onNavigateBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    val tip = tipContent(uiState.index)
    val stackActions = LocalDensity.current.fontScale >= 1.3f
    Column(
        modifier = modifier.fillMaxSize().verticalScroll(rememberScrollState()).padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(18.dp)
    ) {
        Image(
            painter = painterResource(tip.imageRes),
            contentDescription = null,
            modifier = Modifier.fillMaxWidth().height(220.dp),
            contentScale = ContentScale.Fit
        )
        Text(
            text = stringResource(tip.titleRes),
            style = MaterialTheme.typography.headlineSmall,
            color = MaterialTheme.colorScheme.primary,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center
        )
        Text(
            text = stringResource(tip.bodyRes),
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center
        )
        Text(
            text =
                stringResource(
                    R.string.tip_unlock_text,
                    uiState.hoursUntilNext,
                    uiState.minutesUntilNext
                ),
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.tertiary
        )
        Spacer(Modifier.height(8.dp))
        TipActions(stackActions, onNavigateBack)
    }
}

@Composable
private fun TipActions(stacked: Boolean, onNavigateBack: () -> Unit) {
    if (stacked) {
        OutlinedButton(onClick = onNavigateBack, modifier = Modifier.fillMaxWidth()) {
            Text(stringResource(R.string.tip_button_dislike))
        }
        Button(onClick = onNavigateBack, modifier = Modifier.fillMaxWidth()) {
            Text(stringResource(R.string.tip_button))
        }
    } else {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            OutlinedButton(onClick = onNavigateBack, modifier = Modifier.weight(1f)) {
                Text(stringResource(R.string.tip_button_dislike))
            }
            Button(onClick = onNavigateBack, modifier = Modifier.weight(1f)) {
                Text(stringResource(R.string.tip_button))
            }
        }
    }
}

private data class TipContent(val titleRes: Int, val bodyRes: Int, val imageRes: Int)

private fun tipContent(index: Int): TipContent = when (index) {
    0 -> TipContent(
        R.string.revamp_tip_0_title,
        R.string.revamp_tip_0_body,
        R.drawable.icon_fertilizer
    )

    1 -> TipContent(
        R.string.revamp_tip_1_title,
        R.string.revamp_tip_1_body,
        R.drawable.icon_pots
    )

    2 -> TipContent(
        R.string.revamp_tip_2_title,
        R.string.revamp_tip_2_body,
        R.drawable.icon_spray
    )

    3 -> TipContent(
        R.string.revamp_tip_3_title,
        R.string.revamp_tip_3_body,
        R.drawable.icon_brain
    )

    4 -> TipContent(
        R.string.revamp_tip_4_title,
        R.string.revamp_tip_4_body,
        R.drawable.icon_fog
    )

    5 -> TipContent(
        R.string.revamp_tip_5_title,
        R.string.revamp_tip_5_body,
        R.drawable.icon_soil
    )

    else -> TipContent(
        R.string.revamp_tip_6_title,
        R.string.revamp_tip_6_body,
        R.drawable.icon_window
    )
}
