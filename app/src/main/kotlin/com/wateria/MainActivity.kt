package com.wateria

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import com.wateria.revamp.bootstrap.WateriaBootstrapRoute
import com.wateria.revamp.design.WateriaTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            WateriaTheme {
                WateriaBootstrapRoute()
            }
        }
    }
}
