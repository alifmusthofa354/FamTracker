package com.example.famtracker.presentation.auth

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import cafe.adriel.voyager.core.model.ScreenModel
import cafe.adriel.voyager.core.model.screenModelScope
import com.example.famtracker.domain.repository.AuthRepository
import kotlinx.coroutines.launch
import javax.inject.Inject

class LoginViewModel @Inject constructor(
    private val authRepository: AuthRepository
) : ScreenModel {

    var email by mutableStateOf("")
    var password by mutableStateOf("")
    var isLoading by mutableStateOf(false)
    var errorMsg by mutableStateOf<String?>(null)
    
    // Callback untuk navigasi setelah sukses
    var onLoginSuccess: (() -> Unit)? = null

    fun onLoginClick() {
        if (email.isBlank() || password.isBlank()) return
        
        isLoading = true
        errorMsg = null
        
        // Bersihkan spasi depan/belakang yang sering tidak sengaja tertulis
        val cleanEmail = email.trim()
        val cleanPassword = password.trim()
        
        screenModelScope.launch {
            authRepository.login(cleanEmail, cleanPassword)
                .onSuccess {
                    isLoading = false
                    onLoginSuccess?.invoke()
                }
                .onFailure {
                    isLoading = false
                    // Tampilkan pesan error yang lebih user-friendly jika memungkinkan
                    errorMsg = it.message ?: "Terjadi kesalahan saat login"
                }
        }
    }
}