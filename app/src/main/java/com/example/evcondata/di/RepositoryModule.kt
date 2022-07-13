package com.example.evcondata.di

import com.example.evcondata.data.DatabaseManager
import com.example.evcondata.data.auth.LoginDataSource
import com.example.evcondata.data.auth.LoginRepository
import com.example.evcondata.data.auth.UserPreferencesRepository
import com.example.evcondata.data.car.CarRepository
import com.example.evcondata.data.car.CarRepositoryDb
import com.example.evcondata.data.consumption.ConsumptionRepository
import com.example.evcondata.data.consumption.ConsumptionRepositoryDb
import com.example.evcondata.data.location.LocationRepository
import com.example.evcondata.data.location.LocationRepositoryDb
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
        return ConsumptionRepositoryDb(
            databaseManager = databaseManager,
            userPreferencesRepository = userPreferencesDataStore
        )
    }

    @Singleton
    @Provides
    fun provideCarRepository(
        databaseManager: DatabaseManager,
        userPreferencesDataStore: UserPreferencesRepository
    ): CarRepository {
        return CarRepositoryDb(
            databaseManager = databaseManager,
            userPreferencesRepository = userPreferencesDataStore
        )
    }

    @Singleton
    @Provides
    fun provideLocationRepository(
        databaseManager: DatabaseManager,
        userPreferencesDataStore: UserPreferencesRepository
    ): LocationRepository {
        return LocationRepositoryDb(
            databaseManager = databaseManager,
            userPreferencesRepository = userPreferencesDataStore
        )
    }

    @Singleton
    @Provides
    fun providesLoginRepository(loginDataSource: LoginDataSource)
            = LoginRepository(loginDataSource)
}