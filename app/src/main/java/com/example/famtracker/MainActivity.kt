package com.example.famtracker

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.Modifier
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import cafe.adriel.voyager.navigator.Navigator
import cafe.adriel.voyager.transitions.SlideTransition
import com.example.famtracker.data.preferences.OnboardingPreferences
import com.example.famtracker.presentation.MainScreen
import com.example.famtracker.presentation.auth.LoginScreen
import com.example.famtracker.presentation.home.HomeScreen
import com.example.famtracker.presentation.onboarding.OnboardingScreen
import com.example.famtracker.ui.theme.FamTrackerTheme
import com.google.firebase.auth.FirebaseAuth
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    @Inject
    lateinit var onboardingPreferences: OnboardingPreferences
    
    @Inject
    lateinit var auth: FirebaseAuth

    private val keepOnSplash = mutableStateOf(true)

    override fun onCreate(savedInstanceState: Bundle?) {
        val splashScreen = installSplashScreen()
        super.onCreate(savedInstanceState)

        splashScreen.setKeepOnScreenCondition {
            keepOnSplash.value
        }

        setContent {
            MaterialTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    val isOnboardingCompleted by onboardingPreferences
                        .isOnboardingCompleted
                        .collectAsState(initial = null)

                    LaunchedEffect(isOnboardingCompleted) {
                        if (isOnboardingCompleted != null) {
                            keepOnSplash.value = false
                        }
                    }

                    // Tentukan start screen berdasarkan logic:
                    // 1. Belum Onboarding -> OnboardingScreen
                    // 2. Belum Login -> LoginScreen
                    // 3. Sudah Login -> HomeScreen (atau MainScreen)
                    
                    if (isOnboardingCompleted != null) {
                        val startScreen = if (isOnboardingCompleted == false) {
                             OnboardingScreen()
                        } else if (auth.currentUser == null) {
                             LoginScreen()
                        } else {
                             HomeScreen() // Sesuai request, redirect ke HomeScreen
                        }

                        Navigator(startScreen) { navigator ->
                            SlideTransition(navigator)
                        }
                    }
                }
            }
        }
    }
}