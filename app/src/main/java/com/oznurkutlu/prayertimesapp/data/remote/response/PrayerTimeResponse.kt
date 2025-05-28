package com.oznurkutlu.prayertimesapp.data.remote.response

data class PrayerTimeResponse(
    val fajr: String,
    val sunrise: String,
    val dhuhr: String,
    val asr: String,
    val maghrib: String,
    val isha: String
)