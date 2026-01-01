package com.example.famtracker.presentation.auth

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.hilt.getScreenModel
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import com.example.famtracker.presentation.home.HomeScreen

class LoginScreen : Screen {
    
    @Composable
    override fun Content() {
        val navigator = LocalNavigator.currentOrThrow
        val viewModel = getScreenModel<LoginViewModel>()

        // Effect untuk pindah layar jika sukses
        LaunchedEffect(Unit) {
            viewModel.onLoginSuccess = {
                navigator.replaceAll(HomeScreen()) // Ganti semua layar dengan Home
            }
        }

        Column(
            modifier = Modifier.fillMaxSize().padding(16.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text("Login FamTracker", style = MaterialTheme.typography.headlineMedium)
            
            Spacer(modifier = Modifier.height(16.dp))
            
            OutlinedTextField(
                value = viewModel.email,
                onValueChange = { viewModel.email = it },
                label = { Text("Email") },
                modifier = Modifier.fillMaxWidth()
            )
            
            Spacer(modifier = Modifier.height(8.dp))
            
            OutlinedTextField(
                value = viewModel.password,
                onValueChange = { viewModel.password = it },
                label = { Text("Password") },
                modifier = Modifier.fillMaxWidth()
            )
            
            Spacer(modifier = Modifier.height(16.dp))
            
            if (viewModel.errorMsg != null) {
                Text(viewModel.errorMsg!!, color = MaterialTheme.colorScheme.error)
                Spacer(modifier = Modifier.height(8.dp))
            }
            
            Button(
                onClick = { viewModel.onLoginClick() },
                enabled = !viewModel.isLoading,
                modifier = Modifier.fillMaxWidth()
            ) {
                if (viewModel.isLoading) {
                    CircularProgressIndicator(color = MaterialTheme.colorScheme.onPrimary)
                } else {
                    Text("Masuk")
                }
            }
        }
    }
}