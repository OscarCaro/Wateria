package com.wateria.revamp.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.wateria.revamp.feature.about.AboutRoute as AboutScreenRoute
import com.wateria.revamp.feature.about.LicensesRoute as LicensesScreenRoute
import com.wateria.revamp.feature.editor.PlantEditorRoute
import com.wateria.revamp.feature.plants.PlantsRoute as PlantsScreenRoute
import com.wateria.revamp.feature.settings.SettingsRoute as SettingsScreenRoute
import com.wateria.revamp.feature.tips.TipRoute as TipScreenRoute

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
                onOpenSettings = { navController.navigate(SettingsRoute) },
                onShowTip = { navController.navigate(TipRoute) }
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
            SettingsScreenRoute(
                onNavigateBack = navController::popBackStack,
                onOpenAbout = { navController.navigate(AboutRoute) },
                onOpenLicenses = { navController.navigate(LicensesRoute) }
            )
        }
        composable<AboutRoute> {
            AboutScreenRoute(onNavigateBack = navController::popBackStack)
        }
        composable<LicensesRoute> {
            LicensesScreenRoute(onNavigateBack = navController::popBackStack)
        }
        composable<TipRoute> {
            TipScreenRoute(onNavigateBack = navController::popBackStack)
        }
    }
}
