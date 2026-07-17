package com.wateria.revamp

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.compose.rememberNavController
import com.wateria.revamp.design.WateriaTheme
import com.wateria.revamp.navigation.WateriaNavHost

@Composable
fun WateriaApp(modifier: Modifier = Modifier) {
    WateriaTheme {
        WateriaNavHost(
            navController = rememberNavController(),
            modifier = modifier
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun WateriaAppPreview() {
    WateriaApp()
}
