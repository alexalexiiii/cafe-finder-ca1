package org.wit.placemark.models

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class CafeModel(
    var id: Long = 0,
    var name: String = "",
    var favouriteMenuItem: String = "",
    var location: String = "",
    var lat: Double = 0.0,
    var lng: Double = 0.0,
    var rating: Int = 0,
    var returning: Boolean = false,
    var image: String = ""
) : Parcelable
