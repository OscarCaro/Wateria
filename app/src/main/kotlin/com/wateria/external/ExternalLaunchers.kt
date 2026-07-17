package com.wateria.external

import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Inject
import javax.inject.Singleton

enum class ExternalLaunchResult {
    LAUNCHED,
    UNAVAILABLE,
    FAILED
}

interface PlantIdentificationLauncher {
    fun launch(): ExternalLaunchResult
}

interface StoreLauncher {
    fun open(packageName: String): ExternalLaunchResult
}

@Singleton
class AndroidPlantIdentificationLauncher
@Inject
constructor(
    @param:ApplicationContext private val context: Context
) : PlantIdentificationLauncher {
    override fun launch(): ExternalLaunchResult {
        val intent = context.packageManager.getLaunchIntentForPackage(GOOGLE_LENS_PACKAGE)
        return if (Build.VERSION.SDK_INT < Build.VERSION_CODES.N || intent == null) {
            ExternalLaunchResult.UNAVAILABLE
        } else {
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            try {
                context.startActivity(intent)
                ExternalLaunchResult.LAUNCHED
            } catch (_: ActivityNotFoundException) {
                ExternalLaunchResult.UNAVAILABLE
            } catch (_: RuntimeException) {
                ExternalLaunchResult.FAILED
            }
        }
    }

    private companion object {
        const val GOOGLE_LENS_PACKAGE = "com.google.ar.lens"
    }
}

@Singleton
class AndroidStoreLauncher
@Inject
constructor(
    @param:ApplicationContext private val context: Context
) : StoreLauncher {
    override fun open(packageName: String): ExternalLaunchResult {
        val marketIntent = storeIntent(Uri.parse("market://details?id=$packageName"))
        val webIntent =
            storeIntent(Uri.parse("https://play.google.com/store/apps/details?id=$packageName"))
        return when {
            launch(marketIntent) -> ExternalLaunchResult.LAUNCHED
            launch(webIntent) -> ExternalLaunchResult.LAUNCHED
            else -> ExternalLaunchResult.FAILED
        }
    }

    private fun storeIntent(uri: Uri): Intent =
        Intent(Intent.ACTION_VIEW, uri).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)

    private fun launch(intent: Intent): Boolean = try {
        context.startActivity(intent)
        true
    } catch (_: ActivityNotFoundException) {
        false
    } catch (_: RuntimeException) {
        false
    }
}

@Module
@InstallIn(SingletonComponent::class)
abstract class ExternalLaunchersModule {
    @Binds
    abstract fun bindPlantIdentificationLauncher(
        implementation: AndroidPlantIdentificationLauncher
    ): PlantIdentificationLauncher

    @Binds abstract fun bindStoreLauncher(implementation: AndroidStoreLauncher): StoreLauncher
}
