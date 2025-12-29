package com.example.famtracker.di

import android.content.Context
import com.example.famtracker.data.preferences.OnboardingPreferences
import com.example.famtracker.data.repository.UserRepositoryImpl
import com.example.famtracker.domain.repository.UserRepository
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class AppModule {

    @Binds
    @Singleton
    abstract fun bindUserRepository(
        impl: UserRepositoryImpl
    ): UserRepository

    companion object {
        // @Provides digunakan untuk kelas yang memerlukan logika pembuatan (seperti Context)
        @Provides
        @Singleton
        fun provideOnboardingPreferences(
            @ApplicationContext context: Context
        ): OnboardingPreferences {
            return OnboardingPreferences(context)
        }
    }

}