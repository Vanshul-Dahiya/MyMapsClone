package com.example.mymaps.model

import java.io.Serializable

// place is a marker which is at particular location on map
// when it is tapped , it shows title and description and location i.e long and lat
data class Place(
    val tittle: String?,
    val description: String?,
    val longtitude: Double,
    val latitude: Double
) : Serializable {
}