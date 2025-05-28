package com.oznurkutlu.prayertimesapp.data.local.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = PrayerTimeEntity.TABLE_NAME,
    foreignKeys = [
        ForeignKey(
            entity = PlaceEntity::class,
            parentColumns = ["id"],
            childColumns = ["placeId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index("placeId")]
)
data class PrayerTimeEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val placeId: Int,
    val date: String,
    val fajr: String,
    val sunrise: String,
    val dhuhr: String,
    val asr: String,
    val maghrib: String,
    val isha: String
) {
    companion object {
        const val TABLE_NAME = "prayer_times"
    }
}