package org.wit.placemark.models

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class CafeModel( var id: Long = 0,
                      var name: String = "",
                      var favouriteMenuItem: String = "",
                      var rating: Int = 0,
                      var location: String = "",
                      var image: String = ""
) : Parcelable
