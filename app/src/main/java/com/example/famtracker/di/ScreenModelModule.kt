package com.example.famtracker.di

import cafe.adriel.voyager.core.model.ScreenModel
import cafe.adriel.voyager.hilt.ScreenModelKey
import com.example.famtracker.presentation.home.HomeViewModel
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ActivityComponent
import dagger.multibindings.IntoMap

@Module
@InstallIn(ActivityComponent::class)
abstract class ScreenModelModule {

    @Binds
    @IntoMap
    @ScreenModelKey(HomeViewModel::class)
    abstract fun bindHomeViewModel(homeViewModel: HomeViewModel): ScreenModel
}