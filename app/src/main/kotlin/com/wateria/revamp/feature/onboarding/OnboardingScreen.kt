@file:Suppress("MagicNumber", "LongMethod")

package com.wateria.revamp.feature.onboarding

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import androidx.activity.compose.BackHandler
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
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
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.wateria.R
import com.wateria.revamp.design.WateriaDeepGreen
import com.wateria.revamp.design.WateriaFadedGreen
import com.wateria.revamp.design.WateriaPanelShape
import com.wateria.revamp.design.WateriaPillButton

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
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                permissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
            }
        }
    }

    if (uiState.isLoading || uiState.isVisible) {
        BackHandler(enabled = true) { }
        Surface(modifier = Modifier.fillMaxSize(), color = WateriaDeepGreen) {
            if (uiState.isLoading) {
                Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator(color = Color.White)
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
internal fun OnboardingScreen(
    page: Int,
    isCompleting: Boolean,
    onNext: () -> Unit,
    onComplete: () -> Unit
) {
    val content = onboardingPage(page)
    Box(modifier = Modifier.fillMaxSize().padding(horizontal = 12.dp, vertical = 18.dp)) {
        Text(
            text = stringResource(R.string.app_name).uppercase(),
            style = MaterialTheme.typography.headlineLarge,
            color = Color.White.copy(alpha = 0.32f),
            textAlign = TextAlign.Center,
            modifier = Modifier.fillMaxWidth().padding(top = 4.dp)
        )
        Surface(
            color = Color.White,
            shape = WateriaPanelShape,
            shadowElevation = 14.dp,
            modifier =
                Modifier
                    .align(Alignment.Center)
                    .fillMaxWidth()
                    .heightIn(max = 620.dp)
        ) {
            Column(
                modifier =
                    Modifier
                        .verticalScroll(rememberScrollState())
                        .padding(start = 8.dp, top = 24.dp, end = 8.dp, bottom = 8.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = stringResource(content.titleRes).uppercase(),
                    style = MaterialTheme.typography.headlineLarge.copy(fontSize = 24.sp),
                    color = MaterialTheme.colorScheme.primary,
                    textAlign = TextAlign.Center
                )
                Spacer(Modifier.height(16.dp))
                Image(
                    painter = painterResource(content.imageRes),
                    contentDescription = null,
                    modifier = Modifier.fillMaxWidth().height(170.dp),
                    contentScale = ContentScale.Fit
                )
                Spacer(Modifier.height(16.dp))
                Text(
                    text = stringResource(content.bodyRes),
                    style =
                        MaterialTheme.typography.bodyLarge.copy(
                            fontSize = 16.sp,
                            fontStyle = FontStyle.Italic
                        ),
                    color = WateriaFadedGreen,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(horizontal = 8.dp)
                )
                Spacer(Modifier.height(8.dp))
                WateriaPillButton(
                    text = stringResource(content.buttonRes),
                    onClick = if (page == LAST_PAGE) onComplete else onNext,
                    enabled = !isCompleting,
                    color = WateriaFadedGreen,
                    border = BorderStroke(2.dp, MaterialTheme.colorScheme.primary),
                    modifier = Modifier.fillMaxWidth()
                )
                if (isCompleting) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(22.dp),
                        color = MaterialTheme.colorScheme.primary,
                        strokeWidth = 2.dp
                    )
                }
            }
        }
    }
}

private data class OnboardingPage(
    val titleRes: Int,
    val bodyRes: Int,
    val buttonRes: Int,
    val imageRes: Int
)

private fun onboardingPage(page: Int): OnboardingPage = when (page) {
    0 ->
        OnboardingPage(
            R.string.onboarding_dialog_1_title,
            R.string.onboarding_dialog_1_text,
            R.string.onboarding_dialog_1_button,
            R.drawable.icon_happy_plant
        )

    1 ->
        OnboardingPage(
            R.string.onboarding_dialog_2_title,
            R.string.onboarding_dialog_2_text_1,
            R.string.onboarding_dialog_2_button,
            R.drawable.ic_common_1
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
