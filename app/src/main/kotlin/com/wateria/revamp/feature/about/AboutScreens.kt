@file:OptIn(androidx.compose.material3.ExperimentalMaterial3Api::class)

package com.wateria.revamp.feature.about

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.wateria.R
import com.wateria.revamp.design.WateriaBackButton

@Composable
fun AboutRoute(onNavigateBack: () -> Unit) {
    val context = LocalContext.current
    val version = remember(context) {
        context.packageManager.getPackageInfo(context.packageName, 0).versionName ?: "—"
    }
    InformationScaffold(stringResource(R.string.settings_about_text), onNavigateBack) { modifier ->
        Column(
            modifier = modifier.fillMaxSize().verticalScroll(rememberScrollState()).padding(24.dp),
            verticalArrangement = Arrangement.spacedBy(18.dp)
        ) {
            Text(
                stringResource(R.string.app_name),
                style = MaterialTheme.typography.headlineLarge,
                color = MaterialTheme.colorScheme.primary,
                fontWeight = FontWeight.Bold
            )
            Text(
                stringResource(R.string.revamp_version, version),
                color = MaterialTheme.colorScheme.tertiary,
                style = MaterialTheme.typography.titleMedium
            )
            Text(
                stringResource(R.string.revamp_about_body),
                style = MaterialTheme.typography.bodyLarge
            )
            Text(
                stringResource(R.string.revamp_about_thanks),
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.primary
            )
        }
    }
}

@Composable
fun LicensesRoute(onNavigateBack: () -> Unit) {
    InformationScaffold(
        stringResource(R.string.settings_license_text),
        onNavigateBack
    ) { modifier ->
        val uriHandler = LocalUriHandler.current
        Column(
            modifier = modifier.fillMaxSize().verticalScroll(rememberScrollState()).padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            Text(
                stringResource(R.string.revamp_licenses_intro),
                style = MaterialTheme.typography.bodyLarge
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
            Spacer(Modifier.height(24.dp))
        }
    }
}

@Composable
private fun InformationScaffold(
    title: String,
    onNavigateBack: () -> Unit,
    content: @Composable (Modifier) -> Unit
) {
    Scaffold(
        topBar = {
            TopAppBar(
                navigationIcon = {
                    WateriaBackButton(onNavigateBack)
                },
                title = { Text(title) },
                colors =
                    TopAppBarDefaults.topAppBarColors(
                        containerColor = MaterialTheme.colorScheme.primary,
                        titleContentColor = MaterialTheme.colorScheme.onPrimary
                    )
            )
        }
    ) { padding -> content(Modifier.padding(padding)) }
}

@Composable
private fun LicenseCard(title: String, detail: String, onOpen: () -> Unit) {
    Card(modifier = Modifier.fillMaxWidth(), onClick = onOpen) {
        Column(Modifier.padding(16.dp)) {
            Text(
                title,
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.primary
            )
            Spacer(Modifier.height(4.dp))
            Text(
                detail,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}
