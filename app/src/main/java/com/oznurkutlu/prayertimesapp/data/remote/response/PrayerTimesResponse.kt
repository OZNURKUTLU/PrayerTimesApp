package com.oznurkutlu.prayertimesapp.data.remote.response

import com.google.gson.annotations.SerializedName

data class PrayerTimesResponse(
    @SerializedName("place") val placeResponse: PlaceResponse,
    @SerializedName("times") val times: Map<String, List<String>>
)