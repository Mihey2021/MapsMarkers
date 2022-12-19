package ru.netology.mapsmarkers.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import ru.netology.mapsmarkers.db.AppDb
import ru.netology.mapsmarkers.dto.UserMarker
import ru.netology.mapsmarkers.repository.UserMarkerRepository
import ru.netology.mapsmarkers.repository.UserMarkerRepositoryImpl

class UserMarkerViewModel(application: Application) : AndroidViewModel(application) {

    private val repository: UserMarkerRepository = UserMarkerRepositoryImpl(AppDb.getInstance(context = application).userMarkerDao())

    val userMarkersList: LiveData<List<UserMarker>> = repository.getAllMarkers()

    fun save(userMarker: UserMarker) = repository.save(userMarker)

    fun removeMarkerById(id: Long) = repository.removeMarkerById(id)

}