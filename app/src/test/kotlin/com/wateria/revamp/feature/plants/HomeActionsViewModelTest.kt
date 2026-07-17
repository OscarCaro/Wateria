package com.wateria.revamp.feature.plants

import com.wateria.external.ExternalLaunchResult
import com.wateria.external.PlantIdentificationLauncher
import com.wateria.external.StoreLauncher
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class HomeActionsViewModelTest {
    @Test
    fun `unavailable Lens shows install recovery and store action dismisses it`() {
        val lens = FakeLensLauncher(ExternalLaunchResult.UNAVAILABLE)
        val store = FakeStoreLauncher(ExternalLaunchResult.LAUNCHED)
        val viewModel = HomeActionsViewModel(lens, store)

        viewModel.identifyPlant()
        assertTrue(viewModel.uiState.value.showLensInstallDialog)

        viewModel.installLens()
        assertFalse(viewModel.uiState.value.showLensInstallDialog)
        assertTrue("com.google.ar.lens" in store.openedPackages)
    }

    @Test
    fun `rating opens the stable production package`() {
        val store = FakeStoreLauncher(ExternalLaunchResult.LAUNCHED)
        val viewModel = HomeActionsViewModel(FakeLensLauncher(ExternalLaunchResult.LAUNCHED), store)

        viewModel.rateWateria()

        assertTrue("com.wateria" in store.openedPackages)
    }
}

private class FakeLensLauncher(private val result: ExternalLaunchResult) :
    PlantIdentificationLauncher {
    override fun launch(): ExternalLaunchResult = result
}

private class FakeStoreLauncher(private val result: ExternalLaunchResult) : StoreLauncher {
    val openedPackages = mutableListOf<String>()

    override fun open(packageName: String): ExternalLaunchResult {
        openedPackages += packageName
        return result
    }
}
