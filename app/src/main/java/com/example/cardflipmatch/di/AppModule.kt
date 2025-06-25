package com.example.cardflipmatch.di

import android.content.Context
import com.example.cardflipmatch.module.GameLogic
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    @Singleton
    fun provideGameLogic(): GameLogic {
        return GameLogic()
    }

}