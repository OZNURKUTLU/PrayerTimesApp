package com.oznurkutlu.prayertimesapp.ui.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.oznurkutlu.prayertimesapp.data.local.entity.PlaceEntity
import com.oznurkutlu.prayertimesapp.data.local.entity.PrayerTimeEntity
import com.oznurkutlu.prayertimesapp.data.repository.PrayerTimesRepository
import com.oznurkutlu.prayertimesapp.data.remote.response.PlaceSearchResponse
import com.oznurkutlu.prayertimesapp.ui.model.PrayerTimeItem
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class PrayerTimesViewModel @Inject constructor(
    private val repository: PrayerTimesRepository
) : ViewModel() {

    private val _prayerTimes = MutableLiveData<List<PrayerTimeItem>>()
    val prayerTimes: LiveData<List<PrayerTimeItem>> get() = _prayerTimes

    private val _searchResults = MutableLiveData<List<PlaceSearchResponse>>()
    val searchResults: LiveData<List<PlaceSearchResponse>> get() = _searchResults

    private val _error = MutableLiveData<String>()
    val error: LiveData<String> get() = _error

    private val _defaultLocation = MutableLiveData<PlaceEntity?>()
    val defaultLocation: LiveData<PlaceEntity?> get() = _defaultLocation

    init {
        getDefaultLocation()
    }

    fun getDefaultLocation() {
        viewModelScope.launch {
            _defaultLocation.value = repository.getDefaultCity()
        }
    }

    // GPS ile namaz vakitlerini getirme
    fun fetchPrayerTimesByGPS(latitude: Double, longitude: Double, date: String, days: Int = 1) {
        viewModelScope.launch {
            try {
                val prayerTimeEntities = repository.getPrayerTimesByGPS(latitude, longitude, date, days)
                val prayerTimeItems = convertResponseToPrayerTimeItems(prayerTimeEntities) // Güncellendi
                _prayerTimes.value = prayerTimeItems
            } catch (e: Exception) {
                _error.value = e.message
                _prayerTimes.value = emptyList()
            }
        }
    }

    // Yer ID'si ile namaz vakitlerini getirme
    fun fetchPrayerTimesByPlace(placeId: Int, date: String, days: Int = 1) {
        viewModelScope.launch {
            try {
                val response = repository.getPrayerTimesByPlace(placeId, date, days)
                val prayerTimeItems = convertResponseToPrayerTimeItems(response)
                _prayerTimes.value = prayerTimeItems
            } catch (e: Exception) {
                _error.value = e.message
                _prayerTimes.value = emptyList()
            }
        }
    }

    // İl/ilçe araması yapma
    fun searchPlaces(query: String) {
        viewModelScope.launch {
            try {
                val results = repository.searchPlaces(query)
                _searchResults.value = results
            } catch (e: Exception) {
                _error.value = e.message
                _searchResults.value = emptyList()
            }
        }
    }

    private suspend fun convertResponseToPrayerTimeItems(prayerTimeEntities: List<PrayerTimeEntity>): List<PrayerTimeItem> {
        return prayerTimeEntities.flatMap { entity ->
            val placeName = repository.getPlaceNameById(entity.placeId) ?: ""
            val sunriseTime = entity.sunrise // Güneşin doğuşu vaktini al
            val sabahEzanıTime = calculateSabahEzanı(sunriseTime) // Sabah ezanı vaktini hesapla

            listOf(
                PrayerTimeItem("İmsak", entity.fajr, entity.date, placeName),
                PrayerTimeItem("Sabah", sabahEzanıTime, entity.date, placeName),
                PrayerTimeItem("Güneş", entity.sunrise, entity.date, placeName),
                PrayerTimeItem("Öğle", entity.dhuhr, entity.date, placeName),
                PrayerTimeItem("İkindi", entity.asr, entity.date, placeName),
                PrayerTimeItem("Akşam", entity.maghrib, entity.date, placeName),
                PrayerTimeItem("Yatsı", entity.isha, entity.date, placeName)
            )
        }
    }

    private fun calculateSabahEzanı(sunriseTime: String): String {
        val format = java.text.SimpleDateFormat("HH:mm", java.util.Locale.getDefault())
        val sunriseDate = format.parse(sunriseTime)
        val calendar = java.util.Calendar.getInstance()
        calendar.time = sunriseDate
        calendar.add(java.util.Calendar.HOUR_OF_DAY, -1) // 1 saat çıkar

        return format.format(calendar.time)
    }
}