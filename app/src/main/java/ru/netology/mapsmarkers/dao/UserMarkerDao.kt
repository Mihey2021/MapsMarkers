package ru.netology.mapsmarkers.dao

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import ru.netology.mapsmarkers.entity.UserMarkerEntity

@Dao
interface UserMarkerDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun save(userMarker: UserMarkerEntity)

    @Query("SELECT * FROM UserMarkerEntity ORDER BY id DESC")
    fun getAllMarkers(): LiveData<List<UserMarkerEntity>>

    @Query("DELETE FROM UserMarkerEntity WHERE id = :id")
    fun removeMarkerById(id: Long)

}