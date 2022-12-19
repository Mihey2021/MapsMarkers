package ru.netology.mapsmarkers.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import ru.netology.mapsmarkers.dto.UserMarker

@Entity
data class UserMarkerEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long,
    val latitude: Double,
    val longitude: Double,
    val description: String,
) {
    fun toDto() = UserMarker(id, latitude, longitude, description)

    companion object {
        fun fromDto(dto: UserMarker) =
            UserMarkerEntity(dto.id, dto.latitude, dto.longitude, dto.description)
    }
}
