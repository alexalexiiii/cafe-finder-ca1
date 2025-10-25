package org.wit.placemark.models

interface CafeStore {
    fun findAll(): List<CafeModel>
    fun create(cafe: CafeModel)
    fun update(cafe: CafeModel)
    fun delete(cafe: CafeModel)
}