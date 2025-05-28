package com.oznurkutlu.prayertimesapp.data.settings

import android.content.Context
import android.content.SharedPreferences
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject

class SettingsManager @Inject constructor(@ApplicationContext context: Context) {

    private val sharedPreferences: SharedPreferences =
        context.getSharedPreferences("settings", Context.MODE_PRIVATE)

    companion object {
        private const val ALARM_SOUND_KEY = "alarm_sound"
        private const val USE_GPS_KEY = "use_gps"
    }

    suspend fun getAlarmSound(): String = withContext(Dispatchers.IO) {
        sharedPreferences.getString(ALARM_SOUND_KEY, "default") ?: "default"
    }

    suspend fun setAlarmSound(sound: String) = withContext(Dispatchers.IO) {
        sharedPreferences.edit().putString(ALARM_SOUND_KEY, sound).apply()
    }

    suspend fun getUseGps(): Boolean = withContext(Dispatchers.IO) {
        sharedPreferences.getBoolean(USE_GPS_KEY, false)
    }

    suspend fun setUseGps(useGps: Boolean) = withContext(Dispatchers.IO) {
        sharedPreferences.edit().putBoolean(USE_GPS_KEY, useGps).apply()
    }

}