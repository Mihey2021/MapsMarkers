package ru.netology.mapsmarkers.dto

import android.content.res.Resources

data class UserMarker(
    val id: Long = 0L,
    val latitude: Double,
    val longitude: Double,
    val description: String
) : java.io.Serializable {
    override fun toString(): String {
        val resources = Resources.getSystem()
        return "$description\n$latitude\n$longitude"
    }
}
