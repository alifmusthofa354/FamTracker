package com.example.famtracker.presentation

import androidx.compose.runtime.Composable
import cafe.adriel.voyager.core.screen.Screen

class MainScreenWrapper : Screen {
    @Composable
    override fun Content() {
        MainScreen()
    }
}