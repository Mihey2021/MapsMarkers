package ru.netology.mapsmarkers.repository

import androidx.lifecycle.LiveData
import ru.netology.mapsmarkers.dto.UserMarker
import ru.netology.mapsmarkers.entity.UserMarkerEntity

interface UserMarkerRepository {
    fun save(userMarker: UserMarker)
    fun getAllMarkers(): LiveData<List<UserMarker>>
    fun removeMarkerById(id: Long)
}