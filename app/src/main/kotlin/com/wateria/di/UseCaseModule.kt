package com.wateria.di

import com.wateria.domain.repository.PlantRepository
import com.wateria.domain.repository.ReminderScheduler
import com.wateria.domain.time.PlantIdGenerator
import com.wateria.domain.time.TimeProvider
import com.wateria.domain.usecase.CreatePlantUseCase
import com.wateria.domain.usecase.DeletePlantUseCase
import com.wateria.domain.usecase.GetPlantUseCase
import com.wateria.domain.usecase.ObservePlantsUseCase
import com.wateria.domain.usecase.UpdatePlantUseCase
import com.wateria.domain.usecase.WaterPlantUseCase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
object UseCaseModule {
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
}
