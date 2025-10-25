package org.wit.placemark.models

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import org.junit.After
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import java.io.File

class CafeJsonStoreTest {

    private lateinit var context: Context
    private lateinit var store: CafeJsonStore

    @Before
    fun setUp() {
        context = ApplicationProvider.getApplicationContext()
        store = CafeJsonStore(context)
        clearTestFile()
    }

    @After
    fun tearDown() {
        clearTestFile()
    }

    private fun clearTestFile() {
        File(context.filesDir, JSON_FILE).delete()
    }

    @Test
    fun testCreateCafe() {
        val cafe = CafeModel(
            name = "Brew Lab",
            favouriteMenuItem = "Latte",
            location = "Dublin",
            rating = 5
        )
        store.create(cafe)
        val allCafes = store.findAll()
        assertEquals(1, allCafes.size)
        assertEquals("Brew Lab", allCafes[0].name)
    }

    @Test
    fun testUpdateCafe() {
        val cafe = CafeModel(name = "Cafe One", favouriteMenuItem = "Espresso", location = "Galway", rating = 3)
        store.create(cafe)
        cafe.name = "Cafe Updated"
        store.update(cafe)

        val updatedCafe = store.findAll().first()
        assertEquals("Cafe Updated", updatedCafe.name)
    }

    @Test
    fun testDeleteCafe() {
        val cafe = CafeModel(name = "Cafe Delete", favouriteMenuItem = "Mocha", location = "Cork", rating = 4)
        store.create(cafe)
        store.delete(cafe)

        val allCafes = store.findAll()
        assertTrue(allCafes.isEmpty())
    }
}


