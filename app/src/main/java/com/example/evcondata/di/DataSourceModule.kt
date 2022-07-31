package com.example.evcondata.di

import com.example.evcondata.data.auth.LoginDataSource
import com.example.evcondata.data.network.UserServices
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DataSourceModule {

    @Singleton
    @Provides
    fun providesLoginDataSource(userServices: UserServices) = LoginDataSource(userServices)
}