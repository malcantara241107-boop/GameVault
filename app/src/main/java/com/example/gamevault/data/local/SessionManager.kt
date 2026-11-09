package com.example.gamevault.data.local

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.example.gamevault.domain.model.User
import com.example.gamevault.domain.model.UserSession
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val Context.dataStore by preferencesDataStore(
    name = "gamevault_session"
)

class SessionManager(
    private val context: Context
) {

    companion object {
        private val TOKEN_KEY = stringPreferencesKey("auth_token")
        private val USER_ID_KEY = intPreferencesKey("user_id")
        private val USER_NAME_KEY = stringPreferencesKey("user_name")
        private val USER_EMAIL_KEY = stringPreferencesKey("user_email")
    }

    val session: Flow<UserSession?> = context.dataStore.data.map { preferences ->

        val token = preferences[TOKEN_KEY]
        val userId = preferences[USER_ID_KEY]
        val userName = preferences[USER_NAME_KEY]
        val userEmail = preferences[USER_EMAIL_KEY]

        if (token != null && userId != null &&
            userName != null && userEmail != null
        ) {
            UserSession(
                token = token,
                user = User(
                    id = userId,
                    name = userName,
                    email = userEmail
                )
            )
        } else {
            null
        }
    }

    suspend fun saveSession(session: UserSession) {
        context.dataStore.edit { preferences ->
            preferences[TOKEN_KEY] = session.token
            preferences[USER_ID_KEY] = session.user.id
            preferences[USER_NAME_KEY] = session.user.name
            preferences[USER_EMAIL_KEY] = session.user.email
        }
    }

    suspend fun clearSession() {
        context.dataStore.edit { preferences ->
            preferences.clear()
        }
    }
}