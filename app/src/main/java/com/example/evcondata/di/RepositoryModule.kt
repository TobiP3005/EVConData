package com.example.evcondata.di

import com.example.evcondata.data.ConsumptionDatabase
import com.example.evcondata.data.consumption.ConsumptionRepository
import com.example.evcondata.data.consumption.ConsumptionRepositoryDb
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object RepositoryModule {

    @Singleton
    @Provides
    fun provideConsumptionRepository(
        consumptionDatabase: ConsumptionDatabase
    ): ConsumptionRepository {
        val repository = ConsumptionRepositoryDb(
            consumptionDatabase = consumptionDatabase
        )
        repository.initializeDatabase()
        return repository
    }
}