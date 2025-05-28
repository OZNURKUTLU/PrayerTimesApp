package com.oznurkutlu.prayertimesapp.ui.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject
import android.content.Context
import com.oznurkutlu.prayertimesapp.data.settings.SettingsManager
import dagger.hilt.android.qualifiers.ApplicationContext


@HiltViewModel // Hilt kullanıyorsanız ekleyin
class SettingsViewModel @Inject constructor(
    @ApplicationContext private val context: Context // SettingsManager'ı enjekte edin
) : ViewModel() {

    private val settingsManager = SettingsManager(context)

    private val _alarmSound = MutableLiveData<String>()
    val alarmSound: LiveData<String> get() = _alarmSound


    init {
        getAlarmSound()

    }

    private fun getAlarmSound() {
        viewModelScope.launch {
            _alarmSound.value = settingsManager.getAlarmSound()
        }
    }

    fun setAlarmSound(sound: String) {
        viewModelScope.launch {
            settingsManager.setAlarmSound(sound)
            _alarmSound.value = sound
        }
    }

}