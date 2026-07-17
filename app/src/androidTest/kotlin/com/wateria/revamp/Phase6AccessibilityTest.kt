package com.wateria.revamp

import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.test.assertHasClickAction
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performScrollTo
import androidx.compose.ui.unit.Density
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import com.wateria.R
import com.wateria.revamp.design.WateriaBackButton
import com.wateria.revamp.design.WateriaTheme
import com.wateria.revamp.feature.onboarding.OnboardingScreen
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class Phase6AccessibilityTest {
    @get:Rule val compose = createComposeRule()

    @Test
    fun backButtonExposesAReadableAction() {
        val context = InstrumentationRegistry.getInstrumentation().targetContext
        compose.setContent {
            WateriaTheme { WateriaBackButton(onClick = {}) }
        }

        compose
            .onNodeWithContentDescription(context.getString(R.string.revamp_navigate_back))
            .assertIsDisplayed()
            .assertHasClickAction()
    }

    @Test
    fun onboardingRemainsReachableAtTwoHundredPercentTextScale() {
        val context = InstrumentationRegistry.getInstrumentation().targetContext
        compose.setContent {
            val density = LocalDensity.current
            CompositionLocalProvider(
                LocalDensity provides Density(density = density.density, fontScale = 2f)
            ) {
                WateriaTheme {
                    OnboardingScreen(
                        page = 0,
                        isCompleting = false,
                        onNext = {},
                        onComplete = {}
                    )
                }
            }
        }

        compose
            .onNodeWithText(context.getString(R.string.onboarding_dialog_1_button))
            .performScrollTo()
            .assertIsDisplayed()
            .assertHasClickAction()
    }
}
