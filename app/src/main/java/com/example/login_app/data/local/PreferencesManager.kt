package com.example.login_app.data.local

import android.content.Context
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKey

class PreferencesManager(context: Context) {

    private val masterKey = MasterKey.Builder(context)
        .setKeyScheme(MasterKey.KeyScheme.AES256_GCM)
        .build()

    private val sharedPreferences = EncryptedSharedPreferences.create(
        context,
        "secure_user_prefs",
        masterKey,
        EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
        EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
    )

    fun saveCredentials(username: String, passwordHash: String) {
        sharedPreferences.edit()
            .putString("KEY_USERNAME", username)
            .putString("KEY_PASSWORD", passwordHash)
            .putBoolean("KEY_REMEMBER", true)
            .apply()
    }

    fun setAutoLoginEnabled(enabled: Boolean){
        sharedPreferences.edit()
            .putBoolean("KEY_AUTO_LOGIN", enabled)
            .apply()
    }
    fun isAutoLoginEnabled(): Boolean = sharedPreferences.getBoolean("KEY_AUTO_LOGIN", false)
    fun isRememberEnabled(): Boolean = sharedPreferences.getBoolean("KEY_REMEMBER",false)
    fun getSavedUsername(): String? = sharedPreferences.getString("KEY_USERNAME", null)
    fun getSavedPassword(): String? = sharedPreferences.getString("KEY_PASSWORD", null)
    fun saveToken(token: String) {
        sharedPreferences.edit()
            .putString("KEY_TOKEN", token)
            .apply()
    }

    fun getSavedToken(): String? {
        return sharedPreferences.getString("KEY_TOKEN", null)
    }
    fun clearCredentials(){
        sharedPreferences.edit().clear().apply()
    }
    fun saveUserId(userId: String) {
        sharedPreferences.edit().putString("KEY_USER_ID", userId).apply()
    }

    fun getSavedUserId(): String? {
        return sharedPreferences.getString("KEY_USER_ID", null)
    }
}