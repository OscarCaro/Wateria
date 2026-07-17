package com.wateria.revamp.feature.plants

import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import com.wateria.R
import com.wateria.revamp.design.WateriaScreenHeader

@Composable
internal fun PlantsTopBar() {
    WateriaScreenHeader(title = stringResource(R.string.app_name))
}
