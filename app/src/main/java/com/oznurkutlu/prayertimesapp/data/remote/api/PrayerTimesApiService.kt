package com.oznurkutlu.prayertimesapp.data.remote.api

import com.oznurkutlu.prayertimesapp.data.remote.response.PlaceResponse
import com.oznurkutlu.prayertimesapp.data.remote.response.PlaceSearchResponse
import com.oznurkutlu.prayertimesapp.data.remote.response.PrayerTimesResponse
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface PrayerTimesApiService {

    companion object {
        const val DEFAULT_LANG = "tr"
    }

    // İl/ilçe araması yapma
    @GET("api/searchPlaces")
    suspend fun searchPlaces(
        @Query("q") query: String, // Arama sorgusu (örneğin "Keçi")
        @Query("lang") language: String = DEFAULT_LANG // Dil (varsayılan: Türkçe)
    ): Response<List<PlaceSearchResponse>>

    // Namaz vakitlerini getirme (Yer ID'si ile)
    @GET("api/timesForPlace")
    suspend fun getPrayerTimesByPlace(
        @Query("id") placeId: Int,
        @Query("date") date: String,
        @Query("days") days: Int = 1,
        @Query("timezoneOffset") timezoneOffset: Int = 180,
        @Query("calculationMethod") calculationMethod: String = "Turkey"
    ):  Response<PrayerTimesResponse>

    @GET("api/timesForGPS")
    suspend fun getPrayerTimesByGPS(
        @Query("lat") latitude: Double,
        @Query("lng") longitude: Double,
        @Query("date") date: String,
        @Query("days") days: Int = 1, // Varsayılan değer: 1 gün
        @Query("timezoneOffset") timezoneOffset: Int = 180, // Varsayılan değer: UTC+3
        @Query("calculationMethod") calculationMethod: String = "Turkey", // Varsayılan değer: Turkey
        @Query("lang") lang: String = DEFAULT_LANG
    ): Response<PrayerTimesResponse>

    @GET("api/nearByPlaces")
    suspend fun getNearByPlaces(
        @Query("lat") latitude: Double,
        @Query("lng") longitude: Double,
        @Query("lang") language: String = DEFAULT_LANG
    ): Response<List<PlaceResponse>>

    @GET("api/placeById")
    suspend fun getPlaceById(
        @Query("id") placeId: Int,
        @Query("lang") language: String = DEFAULT_LANG
    ): Response<PlaceResponse>

    @GET("api/countries")
    suspend fun getCountries(
        @Query("lang") language: String = DEFAULT_LANG
    ): Response<List<String>>

    @GET("api/regions")
    suspend fun getRegions(
        @Query("country") country: String,
        @Query("lang") language: String = DEFAULT_LANG
    ): Response<List<String>>

    @GET("api/cities")
    suspend fun getCities(
        @Query("country") country: String,
        @Query("region") region: String,
        @Query("lang") language: String = DEFAULT_LANG
    ): Response<List<String>>

    @GET("api/coordinates")
    suspend fun getCoordinates(
        @Query("country") country: String,
        @Query("region") region: String,
        @Query("city") city: String,
        @Query("lang") language: String = DEFAULT_LANG
    ): Response<PlaceResponse>

    @GET("api/place")
    suspend fun getPlaceFromCoordinates(
        @Query("lat") latitude: Double,
        @Query("lng") longitude: Double,
        @Query("lang") language: String = DEFAULT_LANG
    ): Response<PlaceResponse>

    @GET("api/ip")
    suspend fun getPlaceFromIP(
        @Query("lang") language: String = DEFAULT_LANG
    ): Response<PlaceResponse>
}