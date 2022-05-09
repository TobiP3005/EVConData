package com.example.evcondata.di

import com.example.evcondata.data.DatabaseManager
import com.example.evcondata.data.auth.LoginDataSource
import com.example.evcondata.data.auth.LoginRepository
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
        databaseManager: DatabaseManager
    ): ConsumptionRepository {
        val repository = ConsumptionRepositoryDb(
            databaseManager = databaseManager
        )
        repository.initializeDatabase()
        return repository
    }

    @Singleton
    @Provides
    fun providesLoginRepository(loginDataSource: LoginDataSource)
            = LoginRepository(loginDataSource)
}