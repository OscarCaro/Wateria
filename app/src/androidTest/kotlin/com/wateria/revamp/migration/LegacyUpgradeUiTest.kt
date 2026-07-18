package com.wateria.revamp.migration

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onAllNodesWithText
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import com.wateria.MainActivity
import com.wateria.R
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

/** Confirms that bootstrap consumes the migrated files and exposes the expected safe UI. */
@RunWith(AndroidJUnit4::class)
class LegacyUpgradeUiTest {
    @get:Rule val compose = createAndroidComposeRule<MainActivity>()

    @Test
    fun upgradedApplicationStartsInTheExpectedState() {
        val scenario = currentLegacyUpgradeScenario()
        val context = InstrumentationRegistry.getInstrumentation().targetContext

        when (scenario) {
            LegacyUpgradeScenario.MALFORMED -> {
                val recoveryTitle =
                    context.getString(R.string.revamp_migration_recovery_title)
                compose.waitUntil(timeoutMillis = 10_000) {
                    compose
                        .onAllNodesWithText(recoveryTitle)
                        .fetchSemanticsNodes()
                        .isNotEmpty()
                }
                compose
                    .onNodeWithText(recoveryTitle)
                    .assertIsDisplayed()
            }

            LegacyUpgradeScenario.REPRESENTATIVE -> verifyRepresentativeHome()

            else -> error("The UI upgrade smoke test is not defined for $scenario")
        }
    }

    private fun verifyRepresentativeHome() {
        val context = InstrumentationRegistry.getInstrumentation().targetContext
        val likeLabel = context.getString(R.string.tip_button)
        val migratedTipTitle = context.getString(R.string.revamp_tip_4_title)

        compose.waitUntil(timeoutMillis = 10_000) {
            compose.onAllNodesWithText(likeLabel).fetchSemanticsNodes().isNotEmpty() ||
                compose
                    .onAllNodesWithText("Living room Monstera")
                    .fetchSemanticsNodes()
                    .isNotEmpty()
        }
        if (compose.onAllNodesWithText(likeLabel).fetchSemanticsNodes().isNotEmpty()) {
            compose.onNodeWithText(migratedTipTitle).assertIsDisplayed()
            compose.onNodeWithText(likeLabel).performClick()
        }

        compose.waitUntil(timeoutMillis = 10_000) {
            compose
                .onAllNodesWithText("Living room Monstera")
                .fetchSemanticsNodes()
                .isNotEmpty()
        }
        compose.onNodeWithText("Living room Monstera").assertIsDisplayed()
        compose.onNodeWithText("Áloe").assertIsDisplayed()
        assertTrue(
            compose
                .onAllNodesWithText(context.getString(R.string.revamp_migration_recovery_title))
                .fetchSemanticsNodes()
                .isEmpty()
        )
    }
}
