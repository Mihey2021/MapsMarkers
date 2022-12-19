package ru.netology.mapsmarkers.repository

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import ru.netology.mapsmarkers.dao.UserMarkerDao
import ru.netology.mapsmarkers.dto.UserMarker
import ru.netology.mapsmarkers.entity.UserMarkerEntity

class UserMarkerRepositoryImpl(private val dao: UserMarkerDao) : UserMarkerRepository {

    private val _userMarkersList = MutableLiveData<List<UserMarker>>()

    override fun save(userMarker: UserMarker) {
        dao.save(UserMarkerEntity.fromDto(userMarker))
    }

    override fun getAllMarkers(): LiveData<List<UserMarker>> =
        Transformations.map(dao.getAllMarkers()) { userMarkerEntity ->
            userMarkerEntity.map { it.toDto() }
        }

    override fun removeMarkerById(id: Long) {
        dao.removeMarkerById(id)
    }
}