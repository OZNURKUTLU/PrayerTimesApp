package com.oznurkutlu.prayertimesapp.data.di

import android.content.Context
import androidx.room.Room
import com.oznurkutlu.prayertimesapp.data.local.AppDatabase
import com.oznurkutlu.prayertimesapp.data.local.dao.PlaceDao
import com.oznurkutlu.prayertimesapp.data.local.dao.PrayerTimeDao
import com.oznurkutlu.prayertimesapp.data.remote.api.PrayerTimesApiService
import com.oznurkutlu.prayertimesapp.data.repository.PlaceRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object RepositoryModule {

    private const val DATABASE_NAME = "app_database"

    @Provides
    @Singleton
    fun provideAppDatabase(@ApplicationContext context: Context): AppDatabase {
        return Room.databaseBuilder(
            context,
            AppDatabase::class.java,
            DATABASE_NAME
        ).build()
    }

    @Provides
    @Singleton
    fun providePlaceDao(appDatabase: AppDatabase): PlaceDao {
        return appDatabase.placeDao()
    }

    @Provides
    @Singleton
    fun providePrayerTimeDao(appDatabase: AppDatabase): PrayerTimeDao {
        return appDatabase.prayerTimeDao()
    }

    @Provides
    @Singleton
    fun providePlaceRepository(
        placeDao: PlaceDao,
        apiService: PrayerTimesApiService,
        @ApplicationContext context: Context
    ): PlaceRepository {
        return PlaceRepository(placeDao, apiService, context)
    }


}