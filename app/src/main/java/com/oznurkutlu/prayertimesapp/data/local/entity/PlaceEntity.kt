package com.oznurkutlu.prayertimesapp.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = PlaceEntity.TABLE_NAME)
data class PlaceEntity(
    @PrimaryKey val id: Int,
    val name: String,
    val country: String,
    val city: String,
    val region: String,
    val latitude: Double,
    val longitude: Double,
    val isDefault: Boolean = false,
    val isGpsLocated: Boolean = false
) {
    companion object {
        const val TABLE_NAME = "place"
    }
}