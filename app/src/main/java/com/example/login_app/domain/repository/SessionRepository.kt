package com.example.login_app.domain.repository

interface SessionRepository {
    fun saveUserSession(userId: String, name: String)
    fun isSessionActive(): Boolean
    fun clearSession()
}