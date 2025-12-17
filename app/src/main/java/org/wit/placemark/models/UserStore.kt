package org.wit.placemark.models


interface UserStore {
    fun register(user: UserModel): Boolean
    fun login(email: String, password: String): UserModel?
}