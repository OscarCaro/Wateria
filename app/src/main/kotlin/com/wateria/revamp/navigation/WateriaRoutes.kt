package com.wateria.revamp.navigation

import kotlinx.serialization.Serializable

@Serializable
data object PlantsRoute

@Serializable
data object AddPlantRoute

@Serializable
data class EditPlantRoute(val plantId: String)

@Serializable
data object SettingsRoute

@Serializable
data object AboutRoute

@Serializable
data object LicensesRoute

@Serializable
data object TipRoute
