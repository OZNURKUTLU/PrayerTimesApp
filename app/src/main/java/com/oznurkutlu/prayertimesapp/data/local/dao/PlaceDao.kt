package com.oznurkutlu.prayertimesapp.data.local.dao

import androidx.room.*
import com.oznurkutlu.prayertimesapp.data.local.entity.PlaceEntity

@Dao
interface PlaceDao {

    companion object {
        private const val TABLE_NAME = "place"
    }


    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertPlace(place: PlaceEntity): Long

    @Delete
    suspend fun deletePlace(place: PlaceEntity)

    @Query("SELECT * FROM $TABLE_NAME")
    suspend fun getAllPlaces(): List<PlaceEntity>

    @Query("SELECT * FROM $TABLE_NAME WHERE isDefault = 1")
    suspend fun getDefaultPlace(): PlaceEntity?

    @Query("UPDATE $TABLE_NAME SET isDefault = (id = :placeId)")
    suspend fun setDefaultPlace(placeId: Int)

    @Query("SELECT * FROM place WHERE id = :id")
    suspend fun getPlaceById(id: Int): PlaceEntity?

    @Query("UPDATE place SET isGpsLocated = :isGpsLocated WHERE id = :id")
    suspend fun updateIsGpsLocated(id: Int, isGpsLocated: Boolean)

    @Query("SELECT name FROM $TABLE_NAME WHERE id = :placeId")
    suspend fun getPlaceNameById(placeId: Int): String?

    @Query("DELETE FROM $TABLE_NAME WHERE id = :cityId")
    suspend fun deletePlaceById(cityId: Int)

    @Query("UPDATE $TABLE_NAME SET isDefault = 0")
    suspend fun resetDefaultPlaces()
}