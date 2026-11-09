package com.example.gamevault.domain.model

data class UserSession(
    val token: String,
    val user: User
)