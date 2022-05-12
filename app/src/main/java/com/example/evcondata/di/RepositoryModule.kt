package com.example.evcondata.di

import com.example.evcondata.data.DatabaseManager
import com.example.evcondata.data.auth.LoginDataSource
import com.example.evcondata.data.auth.LoginRepository
import com.example.evcondata.data.auth.UserPreferencesRepository
import com.example.evcondata.data.car.CarRepository
import com.example.evcondata.data.car.CarRepositoryDb
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
        databaseManager: DatabaseManager,
        userPreferencesDataStore: UserPreferencesRepository
    ): ConsumptionRepository {
        val repository = ConsumptionRepositoryDb(
            databaseManager = databaseManager,
            userPreferencesDataStore
        )
        return repository
    }

    @Singleton
    @Provides
    fun provideCarRepository(
        databaseManager: DatabaseManager
    ): CarRepository {
        val repository = CarRepositoryDb(
            databaseManager = databaseManager
        )
        return repository
    }

    @Singleton
    @Provides
    fun providesLoginRepository(loginDataSource: LoginDataSource)
            = LoginRepository(loginDataSource)
}