package com.oznurkutlu.prayertimesapp.data.mapper

import com.oznurkutlu.prayertimesapp.data.local.entity.PlaceEntity
import com.oznurkutlu.prayertimesapp.data.local.entity.PrayerTimeEntity
import com.oznurkutlu.prayertimesapp.data.remote.response.PlaceResponse
import com.oznurkutlu.prayertimesapp.data.remote.response.PrayerTimesResponse

object PrayerTimesMapper {

    fun PrayerTimesResponse.toPrayerTimeEntities(placeId: Int): List<PrayerTimeEntity> {
        return this.times.map { (date, times) ->
            PrayerTimeEntity(
                placeId = placeId,
                date = date,
                fajr = times.getOrElse(0) { "" },
                sunrise = times.getOrElse(1) { "" },
                dhuhr = times.getOrElse(2) { "" },
                asr = times.getOrElse(3) { "" },
                maghrib = times.getOrElse(4) { "" },
                isha = times.getOrElse(5) { "" }
            )
        }
    }

   fun PlaceResponse.toPlaceEntity(): PlaceEntity {
        return PlaceEntity(
            id = id, // API'den gelen placeId'yi ayarla
            name = name,
            country = country,
            city = stateName,
            region = stateName,
            latitude = latitude,
            longitude = longitude
        )
    }
}