package com.wateria.revamp.bootstrap

import com.wateria.data.migration.LegacyMigration
import com.wateria.data.migration.LegacyMigrationFailureReason
import com.wateria.data.migration.LegacyMigrationResult
import com.wateria.data.preferences.LegacyMigrationMetadata
import com.wateria.revamp.MainDispatcherRule
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test

class BootstrapViewModelTest {
    @get:Rule val mainDispatcherRule = MainDispatcherRule()

    @Test
    fun `completed migration opens the application`() = runTest {
        val viewModel =
            BootstrapViewModel(FakeLegacyMigration(LegacyMigrationResult.Completed(metadata())))

        assertEquals(BootstrapUiState.Ready, viewModel.uiState.value)
    }

    @Test
    fun `failed migration holds the application in recovery`() = runTest {
        val reason = LegacyMigrationFailureReason.INVALID_WHOLE_PAYLOAD
        val viewModel =
            BootstrapViewModel(
                FakeLegacyMigration(LegacyMigrationResult.Failed(reason, metadata()))
            )

        assertEquals(BootstrapUiState.Recovery(reason), viewModel.uiState.value)
    }

    @Test
    fun `retry can recover after a transient failure`() = runTest {
        val migration =
            FakeLegacyMigration(
                LegacyMigrationResult.Failed(
                    LegacyMigrationFailureReason.INTERRUPTED,
                    metadata()
                )
            )
        val viewModel = BootstrapViewModel(migration)

        migration.result = LegacyMigrationResult.AlreadyComplete(metadata())
        viewModel.retry()

        assertEquals(BootstrapUiState.Ready, viewModel.uiState.value)
        assertEquals(2, migration.runs)
    }

    private fun metadata() = LegacyMigrationMetadata()
}

private class FakeLegacyMigration(var result: LegacyMigrationResult) : LegacyMigration {
    var runs: Int = 0

    override suspend fun run(): LegacyMigrationResult {
        runs++
        return result
    }
}
