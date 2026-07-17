package com.wateria.revamp.navigation

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.wateria.revamp.feature.editor.PlantEditorRoute
import com.wateria.revamp.feature.plants.PlantsRoute as PlantsScreenRoute

@Composable
fun WateriaNavHost(navController: NavHostController, modifier: Modifier = Modifier) {
    NavHost(
        navController = navController,
        startDestination = PlantsRoute,
        modifier = modifier
    ) {
        composable<PlantsRoute> {
            PlantsScreenRoute(
                onAddPlant = { navController.navigate(AddPlantRoute) },
                onEditPlant = { plantId ->
                    navController.navigate(EditPlantRoute(plantId.value))
                },
                onOpenSettings = { navController.navigate(SettingsRoute) }
            )
        }
        composable<AddPlantRoute> {
            PlantEditorRoute(
                onNavigateBack = navController::popBackStack,
                onFinished = navController::popBackStack
            )
        }
        composable<EditPlantRoute> {
            PlantEditorRoute(
                onNavigateBack = navController::popBackStack,
                onFinished = navController::popBackStack
            )
        }
        composable<SettingsRoute> {
            FoundationDestination("Settings")
        }
        composable<AboutRoute> {
            FoundationDestination("About")
        }
        composable<LicensesRoute> {
            FoundationDestination("Licenses")
        }
    }
}

@Composable
private fun FoundationDestination(title: String) {
    Column(
        modifier = Modifier.fillMaxSize().padding(24.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = title,
            style = MaterialTheme.typography.headlineMedium,
            color = MaterialTheme.colorScheme.primary
        )
        Text(
            text = "This area moves to the new architecture in the next phase.",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}
