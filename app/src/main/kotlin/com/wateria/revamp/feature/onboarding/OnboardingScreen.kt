@file:Suppress("MagicNumber")

package com.wateria.revamp.feature.onboarding

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import androidx.activity.compose.BackHandler
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.wateria.R

@Composable
fun OnboardingRoute(viewModel: OnboardingViewModel = hiltViewModel()) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val context = LocalContext.current
    val permissionLauncher =
        rememberLauncherForActivityResult(ActivityResultContracts.RequestPermission()) { }

    LaunchedEffect(viewModel) {
        viewModel.effects.collect { effect ->
            if (effect == OnboardingEffect.Completed &&
                Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU &&
                ContextCompat.checkSelfPermission(
                    context,
                    Manifest.permission.POST_NOTIFICATIONS
                ) !=
                PackageManager.PERMISSION_GRANTED
            ) {
                permissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
            }
        }
    }

    if (uiState.isLoading || uiState.isVisible) {
        BackHandler(enabled = true) { }
        Surface(
            modifier = Modifier.fillMaxSize(),
            color = MaterialTheme.colorScheme.background
        ) {
            if (uiState.isLoading) {
                Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator()
                }
            } else {
                OnboardingScreen(
                    page = uiState.page,
                    isCompleting = uiState.isCompleting,
                    onNext = viewModel::next,
                    onComplete = viewModel::complete
                )
            }
        }
    }
}

@Composable
private fun OnboardingScreen(
    page: Int,
    isCompleting: Boolean,
    onNext: () -> Unit,
    onComplete: () -> Unit
) {
    val content = onboardingPage(page)
    Column(
        modifier = Modifier.fillMaxSize().padding(horizontal = 28.dp, vertical = 36.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        OnboardingProgress(page)
        Image(
            painter = painterResource(content.imageRes),
            contentDescription = null,
            modifier = Modifier.fillMaxWidth().height(300.dp),
            contentScale = ContentScale.Fit
        )
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
                text = stringResource(content.titleRes),
                style = MaterialTheme.typography.headlineMedium,
                color = MaterialTheme.colorScheme.primary,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center
            )
            Spacer(Modifier.height(14.dp))
            Text(
                text = stringResource(content.bodyRes),
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = TextAlign.Center
            )
        }
        Button(
            onClick = if (page == LAST_PAGE) onComplete else onNext,
            enabled = !isCompleting,
            modifier = Modifier.fillMaxWidth()
        ) {
            if (isCompleting) {
                CircularProgressIndicator(
                    modifier = Modifier.size(22.dp),
                    color = Color.White,
                    strokeWidth = 2.dp
                )
            } else {
                Text(stringResource(content.buttonRes))
            }
        }
    }
}

@Composable
private fun OnboardingProgress(page: Int) {
    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
        repeat(PAGE_COUNT) { index ->
            Surface(
                modifier = Modifier.size(if (index == page) 12.dp else 8.dp),
                shape = CircleShape,
                color =
                    if (index == page) {
                        MaterialTheme.colorScheme.tertiary
                    } else {
                        MaterialTheme.colorScheme.primaryContainer
                    }
            ) { }
        }
    }
}

private data class OnboardingPage(
    val titleRes: Int,
    val bodyRes: Int,
    val buttonRes: Int,
    val imageRes: Int
)

@Composable
private fun onboardingPage(page: Int): OnboardingPage = when (page) {
    0 ->
        OnboardingPage(
            R.string.onboarding_dialog_1_title,
            R.string.onboarding_dialog_1_text,
            R.string.onboarding_dialog_1_button,
            R.drawable.image_girl_plants
        )

    1 ->
        OnboardingPage(
            R.string.onboarding_dialog_2_title,
            R.string.onboarding_dialog_2_text_1,
            R.string.onboarding_dialog_2_button,
            R.drawable.icon_happy_plant
        )

    else ->
        OnboardingPage(
            R.string.onboarding_dialog_3_title,
            R.string.onboarding_dialog_3_text_1,
            R.string.onboarding_dialog_3_button,
            R.drawable.icon_notif_bell
        )
}

private const val LAST_PAGE = 2
private const val PAGE_COUNT = 3
