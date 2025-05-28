package com.oznurkutlu.prayertimesapp.data.remote.response

data class PlaceSearchResponse(
    val country: String,
    val id: Int,
    val name: String,
    val countryCode: String,
    val stateName: String,
    val latitude: Double,
    val longitude: Double,
    val alternativeNames: List<String>,
    val isMatchingAlternativeName: Boolean,
    val prefixMatchCount: Int,
    val matchingString: String
)