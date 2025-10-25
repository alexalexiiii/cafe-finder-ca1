package org.wit.placemark.models

import android.content.Context
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.google.gson.reflect.TypeToken
import timber.log.Timber
import java.util.UUID

const val JSON_FILE = "cafes.json"
val gsonBuilder = GsonBuilder().setPrettyPrinting().create()
val listType = object : TypeToken<ArrayList<CafeModel>>() {}.type

class CafeJSONStore(private val context: Context) : CafeStore {

    private var cafes = mutableListOf<CafeModel>()

    init {
        if (fileExists(context, JSON_FILE)) {
            deserialize()
        }
    }

    override fun findAll(): List<CafeModel> = cafes

    override fun create(cafe: CafeModel) {
        cafe.id = UUID.randomUUID().toString()
        cafes.add(cafe)
        serialize()
    }

    override fun update(cafe: CafeModel) {
        val foundCafe = cafes.find { it.id == cafe.id }
        if (foundCafe != null) {
            foundCafe.name = cafe.name
            foundCafe.favouriteMenuItem = cafe.favouriteMenuItem
            foundCafe.location = cafe.location
            foundCafe.rating = cafe.rating
            foundCafe.image = cafe.image
        }
        serialize()
    }

    override fun delete(cafe: CafeModel) {
        cafes.remove(cafe)
        serialize()
    }

    private fun serialize() {
        val jsonString = gsonBuilder.toJson(cafes, listType)
        write(context, JSON_FILE, jsonString)
        Timber.i("Saved cafés: $jsonString")
    }

    private fun deserialize() {
        val jsonString = read(context, JSON_FILE)
        cafes = if (jsonString != null) {
            Gson().fromJson(jsonString, listType)
        } else mutableListOf()
    }
}
