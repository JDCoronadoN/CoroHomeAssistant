package com.example.login_app.data.local
import android.content.Context
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKey
import com.example.login_app.domain.repository.SessionRepository

class EncryptedSessionManager(context: Context): SessionRepository {
    private val masterKey = MasterKey.Builder(context)
        .setKeyScheme(MasterKey.KeyScheme.AES256_GCM)
        .build()
    private val sharedPreferences = EncryptedSharedPreferences.create(
        context,
        "secure_user_session",
        masterKey,
        EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
        EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
    )

    override fun saveUserSession(userId: String, name: String) {
        sharedPreferences.edit()
            .putString("USER_ID", userId)
            .putString("USER_NAME", name)
            .putBoolean("IS_LOGGED_IN", true)
            .apply()
    }
    override fun isSessionActive(): Boolean{
        return sharedPreferences.getBoolean("IS_LOGGED_IN", false)
    }

    override fun clearSession() {
        sharedPreferences.edit().clear().apply()
    }
}