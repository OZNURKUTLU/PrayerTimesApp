package com.oznurkutlu.prayertimesapp.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.oznurkutlu.prayertimesapp.data.local.dao.PlaceDao
import com.oznurkutlu.prayertimesapp.data.local.dao.PrayerTimeDao
import com.oznurkutlu.prayertimesapp.data.local.entity.PlaceEntity
import com.oznurkutlu.prayertimesapp.data.local.entity.PrayerTimeEntity

@Database(entities = [PrayerTimeEntity::class, PlaceEntity::class], version = 1, exportSchema = false)
abstract class AppDatabase : RoomDatabase() {

    abstract fun prayerTimeDao(): PrayerTimeDao
    abstract fun placeDao(): PlaceDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null
        private const val DATABASE_NAME = "prayer_time_database"

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                INSTANCE ?: Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    DATABASE_NAME
                )
                    .fallbackToDestructiveMigration()
                    .build()
                    .also { INSTANCE = it }
            }
        }
    }
}