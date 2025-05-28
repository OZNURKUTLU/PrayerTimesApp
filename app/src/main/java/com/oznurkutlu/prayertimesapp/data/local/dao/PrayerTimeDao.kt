package com.oznurkutlu.prayertimesapp.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.oznurkutlu.prayertimesapp.data.local.entity.PrayerTimeEntity

@Dao
interface PrayerTimeDao {

    companion object {
        private const val TABLE_NAME = "prayer_times"
    }

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPrayerTimes(prayerTimes: List<PrayerTimeEntity>)

    @Query("SELECT * FROM $TABLE_NAME WHERE placeId = :placeId AND date BETWEEN :startDate AND :endDate")
    suspend fun getPrayerTimes(placeId: Int, startDate: String, endDate: String): List<PrayerTimeEntity>

    @Query("SELECT * FROM $TABLE_NAME WHERE placeId = :placeId AND date= :date")
    suspend fun getPrayerTime(placeId: Int, date: String): PrayerTimeEntity?
}