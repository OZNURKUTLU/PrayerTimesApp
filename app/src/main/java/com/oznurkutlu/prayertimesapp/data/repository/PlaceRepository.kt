package com.oznurkutlu.prayertimesapp.data.repository

import android.content.Context
import com.oznurkutlu.prayertimesapp.data.local.dao.PlaceDao
import com.oznurkutlu.prayertimesapp.data.local.entity.PlaceEntity
import com.oznurkutlu.prayertimesapp.data.remote.api.PrayerTimesApiService
import com.oznurkutlu.prayertimesapp.ui.model.City
import javax.inject.Inject

class PlaceRepository @Inject constructor(
    private val placeDao: PlaceDao,
    private val apiService: PrayerTimesApiService,
    private val context: Context
) {

    suspend fun getCities(): List<City> {
        return placeDao.getAllPlaces().map { City(it.id, it.name, it.country, it.city, it.region, it.latitude, it.longitude, it.isDefault, it.isGpsLocated) }
    }

    suspend fun searchCities(query: String): List<City> {
        val response = apiService.searchPlaces(query)
        if (response.isSuccessful) {
            return response.body()?.map { placeSearchResponse ->
                City(
                    id = placeSearchResponse.id,
                    name = placeSearchResponse.name,
                    country = placeSearchResponse.country,
                    city = placeSearchResponse.stateName, // stateName, City modelinde city olarak kullanılıyor.
                    region = "", // API'den region gelmiyor.
                    latitude = placeSearchResponse.latitude,
                    longitude = placeSearchResponse.longitude,
                    isDefault = false,
                    isGpsLocated = false
                )
            } ?: emptyList()
        } else {
            // API hatası
            return emptyList()
        }
    }

    suspend fun addCity(city: City) {
        val placeEntity = PlaceEntity(
            id= city.id,
            name = city.name,
            country = city.country,
            city = city.city,
            region = city.region,
            latitude = city.latitude,
            longitude = city.longitude,
            isDefault = city.isDefault,
            isGpsLocated = city.isGpsLocated
        )
        placeDao.insertPlace(placeEntity)
    }

    suspend fun deleteCity(city: City) {
        placeDao.deletePlaceById(city.id)
    }

    suspend fun setDefaultCity(city: City) {
        placeDao.resetDefaultPlaces() // Tüm şehirlerin isDefault alanını false yapar.
        placeDao.setDefaultPlace(city.id) // Seçilen şehrin isDefault alanını true yapar.
    }

    suspend fun getDefaultCity(): City? {
        val placeEntity = placeDao.getDefaultPlace()
        return placeEntity?.let { City(it.id, it.name, it.country, it.city, it.region, it.latitude, it.longitude, it.isDefault) }
    }


}