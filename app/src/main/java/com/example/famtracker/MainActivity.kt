package com.example.famtracker

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import cafe.adriel.voyager.navigator.Navigator
import cafe.adriel.voyager.transitions.SlideTransition
import com.example.famtracker.data.preferences.OnboardingPreferences
import com.example.famtracker.presentation.MainScreen
import com.example.famtracker.presentation.onboarding.OnboardingScreen
import com.example.famtracker.ui.theme.FamTrackerTheme
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    @Inject
    lateinit var onboardingPreferences: OnboardingPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        installSplashScreen()
        super.onCreate(savedInstanceState)

        setContent {
            MaterialTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    // Cek apakah onboarding sudah selesai
                    val isOnboardingCompleted by onboardingPreferences
                        .isOnboardingCompleted
                        .collectAsState(initial = null)

                    // Tampilkan screen berdasarkan status onboarding
                    when (isOnboardingCompleted) {
                        true -> MainScreen() // Sudah pernah lihat onboarding
                        false -> {
                            // Belum pernah lihat onboarding
                            Navigator(OnboardingScreen()) { navigator ->
                                SlideTransition(navigator)
                            }
                        }
                        null -> {
                            // Loading state, bisa tampilkan splash atau kosong
                            // Splash screen akan handle ini
                        }
                    }
                }
            }
        }
    }
}