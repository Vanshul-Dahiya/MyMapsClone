package com.example.mymaps.model

import java.io.Serializable

// map which user created
// it's attribute are -> title , list of markers each representing a place

// made it serializable so that it can be passed through intent in diff activities
data class UserMap(val title: String?, val places: List<Place>) : Serializable