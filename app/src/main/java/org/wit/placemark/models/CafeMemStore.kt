package org.wit.placemark.models

import timber.log.Timber.i

var lastId = 0L

internal fun getId(): Long {
    return lastId++
}

class CafeMemStore : CafeStore {

    val cafes = ArrayList<CafeModel>()

    override fun findAll(): List<CafeModel> {
        return cafes
    }

    override fun create(cafe: CafeModel) {
        cafe.id = getId()
        cafes.add(cafe)
        logAll()
    }

    override fun update(cafe: CafeModel) {
        var foundCafe: CafeModel? = cafes.find { p -> p.id == cafe.id }
        if (foundCafe != null) {
            foundCafe.name = cafe.name
            foundCafe.favouriteMenuItem = cafe.favouriteMenuItem
            foundCafe.id = cafe.id
            foundCafe.rating = cafe.rating
            foundCafe.location = cafe.location
            foundCafe.image = cafe.location
            logAll()
        }
    }

    private fun logAll() {
        cafes.forEach { i("$it") }
    }

    override fun delete(cafe: CafeModel) {
        cafes.remove(cafe)
    }

}