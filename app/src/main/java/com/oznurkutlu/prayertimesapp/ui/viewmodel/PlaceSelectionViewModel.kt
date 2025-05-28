package com.oznurkutlu.prayertimesapp.ui.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.oznurkutlu.prayertimesapp.data.repository.PlaceRepository
import com.oznurkutlu.prayertimesapp.ui.model.City
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject
import android.content.Context
import android.preference.PreferenceManager
import android.util.Log
import com.oznurkutlu.prayertimesapp.data.repository.PrayerTimesRepository
import com.oznurkutlu.prayertimesapp.utils.DateUtils
import dagger.hilt.android.qualifiers.ApplicationContext


@HiltViewModel
class PlaceSelectionViewModel @Inject constructor(
    private val placeRepository: PlaceRepository,
    private val prayerTimesRepository: PrayerTimesRepository,
    @ApplicationContext private val context: Context
) : ViewModel() {

    private val _cities = MutableLiveData<List<City>>()
    val cities: LiveData<List<City>> get() = _cities

    private val _useGps = MutableLiveData<Boolean>()
    val useGps: LiveData<Boolean> get() = _useGps

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> get() = _isLoading

    private val _errorMessage = MutableLiveData<String?>()
    val errorMessage: LiveData<String?> get() = _errorMessage

    private val _gpsCity = MutableLiveData<City?>()
    val gpsCity: LiveData<City?> get() = _gpsCity

    init {
        getCities()
        loadGpsPreference()
    }

    fun getCities() {
        viewModelScope.launch {
            val allCities = placeRepository.getCities()
            _cities.value = allCities.filter { !it.isGpsLocated }
            _gpsCity.value = allCities.find { it.isGpsLocated }
            Log.d("PlaceSelectionVM", "GPS Şehri: ${_gpsCity.value}")
        }
    }

    fun setDefaultCity(city: City) {
        Log.d("PlaceSelectionVM", "setDefaultCity() çağrıldı: ${city.name}")
        viewModelScope.launch(Dispatchers.Main) {
            placeRepository.setDefaultCity(city)
            Log.d("PlaceSelectionVM", "setDefaultCity(): Veritabanı güncellendi")
            getCities()
            Log.d("PlaceSelectionVM", "setDefaultCity(): getCities() çağrıldı")
            onGpsSelected(false)
            Log.d("PlaceSelectionVM", "setDefaultCity(): onGpsSelected(false) çağrıldı")
        }
    }

    fun deleteCity(city: City) {
        viewModelScope.launch(Dispatchers.Main) {
            placeRepository.deleteCity(city)
            getCities()
        }
    }

    fun onGpsSelected(isSelected: Boolean) {
        _useGps.value = isSelected
        saveGpsPreference(isSelected)
        if (!isSelected) {
            clearGpsPreferences()
        }
    }

    fun clearGpsPreferences() {
        val prefs = PreferenceManager.getDefaultSharedPreferences(context)
        prefs.edit()
            .remove("use_gps")
            .remove("gps_latitude")
            .remove("gps_longitude")
            .apply()
        _useGps.value = false // ViewModel'daki değeri de güncelle
        saveGpsPreference(false)
    }

    private fun loadGpsPreference() {
        val prefs = PreferenceManager.getDefaultSharedPreferences(context)
        _useGps.value = prefs.getBoolean("use_gps", false)
    }

    private fun saveGpsPreference(useGps: Boolean) {
        val prefs = PreferenceManager.getDefaultSharedPreferences(context)
        prefs.edit().putBoolean("use_gps", useGps).apply()
    }


    fun fetchPrayerTimesForGps(latitude: Double, longitude: Double) {
        _isLoading.value = true
        viewModelScope.launch {
            try {
                prayerTimesRepository.getPrayerTimesByGPS(latitude, longitude, DateUtils.getCurrentDate(), 1)
                _errorMessage.value = "Konumunuza göre vakitler yüklendi."
                getCities() // Şehir listesini güncelle
            } catch (e: Exception) {
                _errorMessage.value = "Konumunuza göre vakitler yüklenirken bir hata oluştu: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }


}