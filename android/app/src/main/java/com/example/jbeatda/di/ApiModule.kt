package com.example.jbeatda.di

import com.example.jbeatda.data.remote.SampleService
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object ApiModule {

    @Provides
    @Singleton
    fun sampleService(retrofit: Retrofit): SampleService {
        return retrofit.create(SampleService::class.java)
    }
}