package com.example.atelierapp.ktor

class AuthModels {
    data class LoginRequest(
        val email: String,
        val password: String
    )

    data class RegisterRequest(
        val email: String,
        val password: String,
        val name: String,
        val surname: String,
        val phone: String
        )

    data class AuthResponse(
        val token: String
    )
}