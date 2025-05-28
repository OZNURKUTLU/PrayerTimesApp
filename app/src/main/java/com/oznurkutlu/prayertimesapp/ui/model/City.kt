package com.oznurkutlu.prayertimesapp.ui.model

data class City(
    val id: Int,
    val name: String,
    val country: String,
    val city: String,
    val region: String,
    val latitude: Double,
    val longitude: Double,
    val isDefault: Boolean = false,
    val isGpsLocated: Boolean = false
)
