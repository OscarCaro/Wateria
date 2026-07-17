package com.wateria.di

import com.wateria.domain.repository.PlantRepository
import com.wateria.domain.repository.ReminderScheduler
import com.wateria.domain.repository.SettingsRepository
import com.wateria.domain.time.PlantIdGenerator
import com.wateria.domain.time.TimeProvider
import com.wateria.domain.usecase.CompleteOnboardingUseCase
import com.wateria.domain.usecase.CreatePlantUseCase
import com.wateria.domain.usecase.DeleteAllPlantsUseCase
import com.wateria.domain.usecase.DeletePlantUseCase
import com.wateria.domain.usecase.GetDailyTipUseCase
import com.wateria.domain.usecase.GetDuePlantsUseCase
import com.wateria.domain.usecase.GetPlantUseCase
import com.wateria.domain.usecase.ObserveOnboardingVersionUseCase
import com.wateria.domain.usecase.ObservePlantsUseCase
import com.wateria.domain.usecase.ObserveReminderSettingsUseCase
import com.wateria.domain.usecase.ObserveTipProgressUseCase
import com.wateria.domain.usecase.ShouldShowDailyTipUseCase
import com.wateria.domain.usecase.UpdatePlantUseCase
import com.wateria.domain.usecase.UpdateReminderSettingsUseCase
import com.wateria.domain.usecase.UpdateTipProgressUseCase
import com.wateria.domain.usecase.WaterPlantUseCase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
object PlantUseCaseModule {
    @Provides
    fun provideObservePlantsUseCase(repository: PlantRepository): ObservePlantsUseCase =
        ObservePlantsUseCase(repository)

    @Provides
    fun provideGetPlantUseCase(repository: PlantRepository): GetPlantUseCase =
        GetPlantUseCase(repository)

    @Provides
    fun provideCreatePlantUseCase(
        repository: PlantRepository,
        idGenerator: PlantIdGenerator,
        reminderScheduler: ReminderScheduler
    ): CreatePlantUseCase = CreatePlantUseCase(repository, idGenerator, reminderScheduler)

    @Provides
    fun provideUpdatePlantUseCase(
        repository: PlantRepository,
        reminderScheduler: ReminderScheduler
    ): UpdatePlantUseCase = UpdatePlantUseCase(repository, reminderScheduler)

    @Provides
    fun provideWaterPlantUseCase(
        repository: PlantRepository,
        timeProvider: TimeProvider,
        reminderScheduler: ReminderScheduler
    ): WaterPlantUseCase = WaterPlantUseCase(repository, timeProvider, reminderScheduler)

    @Provides
    fun provideDeletePlantUseCase(
        repository: PlantRepository,
        reminderScheduler: ReminderScheduler
    ): DeletePlantUseCase = DeletePlantUseCase(repository, reminderScheduler)

    @Provides
    fun provideDeleteAllPlantsUseCase(
        repository: PlantRepository,
        reminderScheduler: ReminderScheduler
    ): DeleteAllPlantsUseCase = DeleteAllPlantsUseCase(repository, reminderScheduler)

    @Provides
    fun provideGetDuePlantsUseCase(
        repository: PlantRepository,
        timeProvider: TimeProvider
    ): GetDuePlantsUseCase = GetDuePlantsUseCase(repository, timeProvider)
}

@Module
@InstallIn(SingletonComponent::class)
object SettingsUseCaseModule {
    @Provides
    fun provideObserveReminderSettingsUseCase(
        repository: SettingsRepository
    ): ObserveReminderSettingsUseCase = ObserveReminderSettingsUseCase(repository)

    @Provides
    fun provideUpdateReminderSettingsUseCase(
        repository: SettingsRepository,
        reminderScheduler: ReminderScheduler
    ): UpdateReminderSettingsUseCase = UpdateReminderSettingsUseCase(repository, reminderScheduler)

    @Provides
    fun provideObserveOnboardingVersionUseCase(
        repository: SettingsRepository
    ): ObserveOnboardingVersionUseCase = ObserveOnboardingVersionUseCase(repository)

    @Provides
    fun provideCompleteOnboardingUseCase(
        repository: SettingsRepository
    ): CompleteOnboardingUseCase = CompleteOnboardingUseCase(repository)

    @Provides
    fun provideObserveTipProgressUseCase(
        repository: SettingsRepository
    ): ObserveTipProgressUseCase = ObserveTipProgressUseCase(repository)

    @Provides
    fun provideUpdateTipProgressUseCase(repository: SettingsRepository): UpdateTipProgressUseCase =
        UpdateTipProgressUseCase(repository)

    @Provides
    fun provideShouldShowDailyTipUseCase(
        repository: SettingsRepository,
        timeProvider: TimeProvider
    ): ShouldShowDailyTipUseCase = ShouldShowDailyTipUseCase(repository, timeProvider)

    @Provides
    fun provideGetDailyTipUseCase(
        repository: SettingsRepository,
        timeProvider: TimeProvider
    ): GetDailyTipUseCase = GetDailyTipUseCase(repository, timeProvider)
}
