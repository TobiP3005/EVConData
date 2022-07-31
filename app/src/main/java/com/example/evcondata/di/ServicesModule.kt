package com.example.evcondata.di

import com.example.evcondata.data.network.ServiceBuilder
import com.example.evcondata.data.network.UserServices
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object ServicesModule {

    @Provides
    @Singleton
    fun provideUserService(serviceBuilder: ServiceBuilder): UserServices = serviceBuilder.buildService(UserServices::class.java)
}