@file:Suppress("MagicNumber", "LongMethod")

package com.wateria.revamp.feature.about

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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.wateria.R
import com.wateria.revamp.design.WateriaDeepGreen
import com.wateria.revamp.design.WateriaOrange
import com.wateria.revamp.design.WateriaPanelShape
import com.wateria.revamp.design.WateriaPillButton

@Composable
fun AboutRoute(onNavigateBack: () -> Unit) {
    val context = LocalContext.current
    val version =
        remember(context) {
            context.packageManager.getPackageInfo(context.packageName, 0).versionName ?: "—"
        }
    InformationCanvas {
        Column(
            modifier =
                Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = 24.dp, vertical = 26.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text(
                text = stringResource(R.string.app_name).uppercase(),
                style = MaterialTheme.typography.headlineLarge.copy(fontSize = 24.sp),
                color = MaterialTheme.colorScheme.primary,
                textAlign = TextAlign.Center
            )
            Text(
                text = stringResource(R.string.revamp_version, version),
                color = MaterialTheme.colorScheme.primary,
                style = MaterialTheme.typography.titleMedium.copy(fontSize = 18.sp)
            )
            Text(
                text = stringResource(R.string.revamp_about_body),
                style = MaterialTheme.typography.bodyLarge.copy(fontSize = 16.sp),
                textAlign = TextAlign.Center
            )
            Text(
                text = stringResource(R.string.revamp_about_thanks),
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.primary,
                textAlign = TextAlign.Center
            )
            Spacer(Modifier.height(4.dp))
            WateriaPillButton(
                text = stringResource(R.string.newPlantAcceptButtonText),
                onClick = onNavigateBack,
                color = MaterialTheme.colorScheme.primary,
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}

@Composable
fun LicensesRoute(onNavigateBack: () -> Unit) {
    val uriHandler = LocalUriHandler.current
    InformationCanvas {
        Column(
            modifier =
                Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = 18.dp, vertical = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Image(
                painter = painterResource(R.drawable.icon_documents),
                contentDescription = null,
                modifier = Modifier.size(72.dp)
            )
            Text(
                text = stringResource(R.string.settings_license_text).uppercase(),
                style = MaterialTheme.typography.headlineLarge.copy(fontSize = 24.sp),
                color = MaterialTheme.colorScheme.primary,
                textAlign = TextAlign.Center
            )
            Text(
                text = stringResource(R.string.revamp_licenses_intro),
                style = MaterialTheme.typography.bodyMedium,
                textAlign = TextAlign.Center
            )
            LicenseCard(
                title = stringResource(R.string.revamp_typography_attribution_title),
                detail = stringResource(R.string.revamp_typography_attribution_body),
                onOpen = { uriHandler.openUri("https://github.com/google/fonts") }
            )
            LicenseCard(
                title = "AndroidX & Jetpack Compose",
                detail = "Apache License 2.0",
                onOpen = {
                    uriHandler.openUri("https://developer.android.com/jetpack/androidx/versions")
                }
            )
            LicenseCard(
                title = "Kotlin",
                detail = "Apache License 2.0",
                onOpen = { uriHandler.openUri("https://github.com/JetBrains/kotlin") }
            )
            LicenseCard(
                title = "Dagger & Hilt",
                detail = "Apache License 2.0",
                onOpen = { uriHandler.openUri("https://github.com/google/dagger") }
            )
            LicenseCard(
                title = "Firebase",
                detail = "Google APIs Terms of Service",
                onOpen = { uriHandler.openUri("https://firebase.google.com/terms") }
            )
            LicenseCard(
                title = stringResource(R.string.revamp_asset_attribution_title),
                detail = stringResource(R.string.revamp_asset_attribution_body),
                onOpen = { uriHandler.openUri("https://www.flaticon.com/") }
            )
            Spacer(Modifier.height(6.dp))
            WateriaPillButton(
                text = stringResource(R.string.newPlantAcceptButtonText),
                onClick = onNavigateBack,
                color = WateriaOrange,
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}

@Composable
private fun InformationCanvas(content: @Composable () -> Unit) {
    Surface(modifier = Modifier.fillMaxSize(), color = WateriaDeepGreen) {
        Box(modifier = Modifier.fillMaxSize().padding(horizontal = 10.dp, vertical = 14.dp)) {
            Surface(
                color = Color.White,
                shape = WateriaPanelShape,
                shadowElevation = 14.dp,
                modifier = Modifier.align(Alignment.Center).fillMaxWidth().heightIn(max = 760.dp),
                content = content
            )
        }
    }
}

@Composable
private fun LicenseCard(title: String, detail: String, onOpen: () -> Unit) {
    Surface(
        onClick = onOpen,
        color = Color.White,
        shape = RoundedCornerShape(18.dp),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.primary.copy(alpha = 0.45f)),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(Modifier.padding(horizontal = 14.dp, vertical = 10.dp)) {
            Text(
                title,
                style = MaterialTheme.typography.titleSmall,
                color = MaterialTheme.colorScheme.primary
            )
            Spacer(Modifier.height(2.dp))
            Text(
                detail,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.70f)
            )
        }
    }
}
