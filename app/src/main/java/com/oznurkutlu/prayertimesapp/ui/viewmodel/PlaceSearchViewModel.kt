package com.oznurkutlu.prayertimesapp.ui.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.oznurkutlu.prayertimesapp.data.repository.PlaceRepository
import com.oznurkutlu.prayertimesapp.ui.model.City
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class PlaceSearchViewModel @Inject constructor(
    private val placeRepository: PlaceRepository
) : ViewModel() {

    val isLoading = MutableLiveData<Boolean>() // Yükleme durumu
    val errorMessage = MutableLiveData<String?>() // Hata mesajı

    private val _searchResults = MutableLiveData<List<City>>()
    val searchResults: LiveData<List<City>> get() = _searchResults

    init {
        isLoading.value = false // Başlangıçta yükleme yok
        errorMessage.value = null // Başlangıçta hata yok
    }

    fun searchCities(query: String) {
        isLoading.value = true
        viewModelScope.launch {
            _searchResults.value = placeRepository.searchCities(query)
        }
    }

    fun addCity(city: City, placeSelectionViewModel: PlaceSelectionViewModel) { // placeSelectionViewModel parametresi eklendi
        viewModelScope.launch {
            placeRepository.addCity(city)
            placeSelectionViewModel.getCities() // PlaceSelectionViewModel'deki getCities() fonksiyonunu çağır
        }
    }
}