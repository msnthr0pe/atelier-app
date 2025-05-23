package com.example.atelierapp.ktor

import kotlinx.serialization.Serializable

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
        val phone: String,
        val status: String
        )

    data class ClientRequest(
        val date: String,
        val phone: String,
        val name: String,
        val email: String,
        val time: String,
        val description: String,
    )

    @Serializable
    data class ClientByDTO(
        val name: String,
        val date: String,
        val mode: String
    )

    data class AuthResponse(
        val token: String
    )
}