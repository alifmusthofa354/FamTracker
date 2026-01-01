package com.example.famtracker.di

import android.app.Application
import com.example.famtracker.data.repository.AuthRepositoryImpl
import com.example.famtracker.data.repository.LocationRepositoryImpl
import com.example.famtracker.data.repository.UserRepositoryImpl
import com.example.famtracker.domain.repository.AuthRepository
import com.example.famtracker.domain.repository.LocationRepository
import com.example.famtracker.domain.repository.UserRepository
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.firebase.auth.FirebaseAuth
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
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

    @Binds
    @Singleton
    abstract fun bindAuthRepository(
        impl: AuthRepositoryImpl
    ): AuthRepository

    @Binds
    @Singleton
    abstract fun bindLocationRepository(
        impl: LocationRepositoryImpl
    ): LocationRepository

    companion object {
        @Provides
        @Singleton
        fun provideFirebaseAuth(): FirebaseAuth {
            return FirebaseAuth.getInstance()
        }

        @Provides
        @Singleton
        fun provideFusedLocationProviderClient(
            app: Application
        ): FusedLocationProviderClient {
            return LocationServices.getFusedLocationProviderClient(app)
        }
    }
}