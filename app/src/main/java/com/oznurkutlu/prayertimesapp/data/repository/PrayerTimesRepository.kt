package com.oznurkutlu.prayertimesapp.data.repository

    import android.util.Log
    import com.oznurkutlu.prayertimesapp.data.local.dao.PlaceDao
    import com.oznurkutlu.prayertimesapp.data.local.dao.PrayerTimeDao
    import com.oznurkutlu.prayertimesapp.data.local.entity.PlaceEntity
    import com.oznurkutlu.prayertimesapp.data.local.entity.PrayerTimeEntity
    import com.oznurkutlu.prayertimesapp.data.mapper.PrayerTimesMapper.toPrayerTimeEntities
    import com.oznurkutlu.prayertimesapp.data.remote.api.PrayerTimesApiService
    import com.oznurkutlu.prayertimesapp.data.remote.response.PlaceResponse
    import com.oznurkutlu.prayertimesapp.data.remote.response.PlaceSearchResponse
    import com.oznurkutlu.prayertimesapp.data.remote.response.PrayerTimesResponse
    import com.oznurkutlu.prayertimesapp.ui.model.City
    import kotlinx.coroutines.Dispatchers
    import kotlinx.coroutines.withContext
    import kotlinx.coroutines.withTimeout
    import javax.inject.Inject

class PrayerTimesRepository @Inject constructor(
        private val apiService: PrayerTimesApiService,
        private val placeDao: PlaceDao,
        private val prayerTimeDao: PrayerTimeDao
    ) {

    private fun mapPrayerTimesResponseToEntities(
        response: PrayerTimesResponse,
        placeId: Int
    ): List<PrayerTimeEntity> {
        return response.toPrayerTimeEntities(placeId)
    }
        // GPS ile namaz vakitlerini getirme
        suspend fun getPrayerTimesByGPS(
            latitude: Double,
            longitude: Double,
            date: String,
            days: Int = 1
        ): List<PrayerTimeEntity> {
            Log.d("PrayerRepo", "getPrayerTimesByGPS çağrıldı: lat=$latitude, lng=$longitude, date=$date")
            return try {
                Log.d("PrayerRepo", "API isteği gönderiliyor...")
                val response = apiService.getPrayerTimesByGPS(latitude, longitude, date, days)
                if (response.isSuccessful) {
                    Log.d("PrayerRepo", "API yanıtı alındı: $response")
                    val prayerTimesResponse = response.body()
                    if (prayerTimesResponse != null && prayerTimesResponse.placeResponse != null) {
                        Log.d("PrayerRepo", "PlaceResponse alındı: ${prayerTimesResponse.placeResponse}")
                        val placeId = savePlaceAndGetId(prayerTimesResponse.placeResponse, true)
                        Log.d("PrayerRepo", "kaydedildi: $placeId")
                        val prayerTimeEntities = mapPrayerTimesResponseToEntities(prayerTimesResponse, placeId)
                        prayerTimeDao.insertPrayerTimes(prayerTimeEntities)
                        Log.d("PrayerRepo", "kaydedildi: $prayerTimeEntities")
                        prayerTimeEntities
                    } else {
                        Log.w("PrayerRepo", "API yanıtı body'si veya placeResponse null")
                        emptyList() // veya hata yönetimi
                    }
                } else {
                    Log.e("PrayerRepo", "API isteği başarısız: ${response.errorBody()}")
                    emptyList() // veya hata yönetimi
                }
            } catch (e: Exception) {
                Log.e("PrayerRepo", "API veya veritabanı hatası: ${e.message}", e)
                emptyList() // veya hata yönetimi
            }
        }
    // Yer ID'si ile namaz vakitlerini getirme
    suspend fun getPrayerTimesByPlace(
        placeId: Int,
        date: String,
        days: Int = 1
    ): List<PrayerTimeEntity> {
        return try {
            withTimeout(5000) {
                val response = apiService.getPrayerTimesByPlace(placeId, date, days)
                if (response.isSuccessful) {
                    val prayerTimesResponse = response.body()
                        ?: throw ApiException("API yanıtı boş")
                    val prayerTimeEntities = mapPrayerTimesResponseToEntities(prayerTimesResponse, placeId)
                    prayerTimeDao.insertPrayerTimes(prayerTimeEntities)
                    prayerTimeEntities
                } else {
                    throw ApiException("API isteği başarısız: ${response.errorBody()}")
                }
            }
        } catch (e: Exception) {
            throw e
        }
    }

    // İl/ilçe araması yapma
    suspend fun searchPlaces(query: String): List<PlaceSearchResponse> {
        return try {
            withTimeout(5000) {
                val response = apiService.searchPlaces(query)
                if (response.isSuccessful) {
                    response.body() ?: emptyList()
                } else {
                    throw ApiException("API isteği başarısız: ${response.errorBody()}")
                }
            }
        } catch (e: Exception) {
            throw e
        }
    }

    suspend fun getPlaceNameById(placeId: Int): String? {
        return placeDao.getPlaceNameById(placeId)
    }


    private suspend fun savePlaceAndGetId(placeResponse: PlaceResponse, isGpsLocated: Boolean = false): Int {
        Log.d("PlaceRepo", "savePlaceAndGetId fonksiyonuna girildi: placeResponse=${placeResponse}, isGpsLocated=$isGpsLocated")
        return withContext(Dispatchers.IO) {
            val existingPlace = placeDao.getPlaceById(placeResponse.id)
            Log.d("PlaceRepo", "existingPlace: $existingPlace")
            if (existingPlace != null) {
                Log.d("PlaceRepo", "Mevcut yer bulundu, güncelleniyor: id=${existingPlace.id}, isGpsLocated=$isGpsLocated")
                placeDao.updateIsGpsLocated(existingPlace.id, isGpsLocated = true)
                existingPlace.id
            } else {
                Log.d("PlaceRepo", "Yeni yer ekleniyor: placeResponse=$placeResponse, isGpsLocated=$isGpsLocated")
                val newPlaceEntity = PlaceEntity(
                    id = placeResponse.id,
                    name = placeResponse.name,
                    country = placeResponse.country,
                    city = placeResponse.stateName,
                    region = placeResponse.stateName,
                    latitude = placeResponse.latitude,
                    longitude = placeResponse.longitude,
                    isDefault = true,
                    isGpsLocated=true
                )
                Log.d("PlaceRepo", "Yeni PlaceEntity: $newPlaceEntity")
                placeDao.resetDefaultPlaces()
                placeDao.insertPlace(newPlaceEntity).toInt()
            }
        }
    }


    suspend fun getDefaultCity(): PlaceEntity? {
        return placeDao.getDefaultPlace()
    }

    suspend fun setDefaultCity(place: PlaceEntity) {
        placeDao.resetDefaultPlaces() // Tüm şehirlerin isDefault alanını false yapar.
        placeDao.setDefaultPlace(place.id) // Seçilen şehrin isDefault alanını true yapar.
    }

    }

class ApiException(message: String) : Exception(message)
class NetworkException(message: String) : Exception(message)
