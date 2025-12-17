package org.wit.placemark.models

import java.util.UUID

data class UserModel(
    val id: String = UUID.randomUUID().toString(),
    val email: String,
    val password: String
)
