package com.wateria.data.di

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.core.handlers.ReplaceFileCorruptionHandler
import androidx.datastore.preferences.core.PreferenceDataStoreFactory
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.emptyPreferences
import androidx.datastore.preferences.preferencesDataStoreFile
import androidx.room.Room
import androidx.work.WorkManager
import com.wateria.data.database.WateriaDatabase
import com.wateria.data.database.dao.PlantDao
import com.wateria.data.migration.AndroidLegacyPreferencesSource
import com.wateria.data.migration.LegacyMigration
import com.wateria.data.migration.LegacyMigrationRunner
import com.wateria.data.migration.LegacyPreferencesSource
import com.wateria.data.migration.MigrationCheckpoint
import com.wateria.data.migration.NoOpMigrationCheckpoint
import com.wateria.data.reminders.AndroidReminderNotificationPublisher
import com.wateria.data.reminders.ReminderNotificationPublisher
import com.wateria.data.reminders.WorkManagerReminderScheduler
import com.wateria.data.repository.DataStoreSettingsRepository
import com.wateria.data.repository.RoomPlantRepository
import com.wateria.domain.repository.PlantRepository
import com.wateria.domain.repository.ReminderScheduler
import com.wateria.domain.repository.SettingsRepository
import com.wateria.domain.time.ClockTimeProvider
import com.wateria.domain.time.PlantIdGenerator
import com.wateria.domain.time.RandomPlantIdGenerator
import com.wateria.domain.time.TimeProvider
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import java.time.Clock
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class DataBindingsModule {
    @Binds
    @Singleton
    abstract fun bindLegacyMigration(implementation: LegacyMigrationRunner): LegacyMigration

    @Binds
    @Singleton
    abstract fun bindPlantRepository(implementation: RoomPlantRepository): PlantRepository

    @Binds
    @Singleton
    abstract fun bindSettingsRepository(
        implementation: DataStoreSettingsRepository
    ): SettingsRepository

    @Binds
    @Singleton
    abstract fun bindReminderScheduler(
        implementation: WorkManagerReminderScheduler
    ): ReminderScheduler

    @Binds
    @Singleton
    abstract fun bindReminderNotificationPublisher(
        implementation: AndroidReminderNotificationPublisher
    ): ReminderNotificationPublisher

    @Binds
    @Singleton
    abstract fun bindLegacyPreferencesSource(
        implementation: AndroidLegacyPreferencesSource
    ): LegacyPreferencesSource

    @Binds
    @Singleton
    abstract fun bindMigrationCheckpoint(
        implementation: NoOpMigrationCheckpoint
    ): MigrationCheckpoint
}

@Module
@InstallIn(SingletonComponent::class)
object DataProvidesModule {
    @Provides
    @Singleton
    fun provideDatabase(@ApplicationContext context: Context): WateriaDatabase =
        Room.databaseBuilder(context, WateriaDatabase::class.java, DATABASE_NAME).build()

    @Provides fun providePlantDao(database: WateriaDatabase): PlantDao = database.plantDao()

    @Provides
    @Singleton
    fun providePreferencesDataStore(@ApplicationContext context: Context): DataStore<Preferences> =
        PreferenceDataStoreFactory.create(
            corruptionHandler = ReplaceFileCorruptionHandler { emptyPreferences() },
            produceFile = { context.preferencesDataStoreFile(PREFERENCES_FILE_NAME) }
        )

    @Provides
    @Singleton
    fun provideWorkManager(@ApplicationContext context: Context): WorkManager =
        WorkManager.getInstance(context)

    @Provides
    @Singleton
    fun provideTimeProvider(): TimeProvider = ClockTimeProvider(Clock.systemDefaultZone())

    @Provides
    @Singleton
    fun providePlantIdGenerator(): PlantIdGenerator = RandomPlantIdGenerator()

    private const val DATABASE_NAME = "wateria.db"
    private const val PREFERENCES_FILE_NAME = "wateria.preferences_pb"
}
