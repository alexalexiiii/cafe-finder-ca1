package org.wit.placemark.models

import android.content.Context
import com.google.gson.GsonBuilder
import com.google.gson.reflect.TypeToken

class UserJsonStore(private val context: Context) : UserStore {

    private val gson = GsonBuilder().setPrettyPrinting().create()
    private val listType = object : TypeToken<ArrayList<UserModel>>() {}.type
    private val file = "users.json"

    private var users = ArrayList<UserModel>()

    init {
        if (fileExists(context, file)) {
            deserialize()
        }
    }

    override fun register(user: UserModel): Boolean {
        if (users.any { it.email == user.email }) return false
        users.add(user)
        serialize()
        return true
    }

    override fun login(email: String, password: String): UserModel? {
        return users.find { it.email == email && it.password == password }
    }

    private fun serialize() {
        write(context, file, gson.toJson(users))
    }

    private fun deserialize() {
        val json = read(context, file)
        if (json != null) {
            users = gson.fromJson(json, listType)
        }
    }
}
