package com.example.famtracker.presentation.onboarding

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.famtracker.data.preferences.OnboardingPreferences
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class OnboardingViewModel @Inject constructor(
    private val preferences: OnboardingPreferences
) : ViewModel() {

    fun completeOnboarding() {
        viewModelScope.launch {
            preferences.setOnboardingCompleted()
        }
    }
}