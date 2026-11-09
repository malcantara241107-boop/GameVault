package com.example.gamevault.data.local

class TokenProvider {

    @Volatile
    private var token: String? = null

    fun getToken(): String? {
        return token
    }

    fun updateToken(newToken: String?) {
        token = newToken
    }
}