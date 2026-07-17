package com.wateria.revamp

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.compose.rememberNavController
import com.wateria.revamp.feature.onboarding.OnboardingRoute
import com.wateria.revamp.navigation.WateriaNavHost

@Composable
fun WateriaApp(modifier: Modifier = Modifier) {
    Box(modifier = modifier.fillMaxSize()) {
        WateriaNavHost(navController = rememberNavController())
        OnboardingRoute()
    }
}

@Preview(showBackground = true)
@Composable
private fun WateriaAppPreview() {
    com.wateria.revamp.design.WateriaTheme { WateriaApp() }
}
