package org.wit.placemark.activities

import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.location.Geocoder
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.BitmapDescriptor
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions
import org.wit.placemark.R
import org.wit.placemark.main.MainApp
import org.wit.placemark.models.CafeModel
import java.io.File
import java.util.Locale

class CafeMapActivity : AppCompatActivity(), OnMapReadyCallback {

    private lateinit var map: GoogleMap
    private lateinit var app: MainApp

    private val markerCafeMap = mutableMapOf<Marker, CafeModel>()
    private lateinit var geocoder: Geocoder

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_cafe_map)

        app = application as MainApp
        geocoder = Geocoder(this, Locale.getDefault())

        // Toolbar
        val toolbar = findViewById<androidx.appcompat.widget.Toolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        toolbar.setNavigationOnClickListener { finish() }

        // Search
        val searchView = findViewById<SearchView>(R.id.mapSearch)
        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean = false

            override fun onQueryTextChange(newText: String?): Boolean {
                updateMarkers(newText.orEmpty())
                return true
            }
        })

        // Map
        val mapFragment =
            supportFragmentManager.findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)
    }

    override fun onMapReady(googleMap: GoogleMap) {
        map = googleMap

        updateMarkers("")

        // Default camera (Ireland)
        map.moveCamera(
            CameraUpdateFactory.newLatLngZoom(
                LatLng(53.3498, -6.2603), 10f
            )
        )

        // Marker click → open café
        map.setOnInfoWindowClickListener { marker ->
            markerCafeMap[marker]?.let { cafe ->
                val intent = Intent(this, CafeActivity::class.java)
                intent.putExtra("cafe_edit", cafe)
                startActivity(intent)
            }
        }
    }

    // ---------------- MARKER LOGIC ----------------

    private fun updateMarkers(filter: String) {
        map.clear()
        markerCafeMap.clear()

        val cafes = app.cafes.findAll().filter {
            it.name.contains(filter, ignoreCase = true)
        }

        cafes.forEach { cafe ->
            try {
                val results = geocoder.getFromLocationName(cafe.location, 1)
                if (!results.isNullOrEmpty()) {
                    val latLng = LatLng(
                        results[0].latitude,
                        results[0].longitude
                    )

                    val marker = map.addMarker(
                        MarkerOptions()
                            .position(latLng)
                            .title(cafe.name)
                            .icon(getCafeMarkerIcon(cafe.image))
                    )

                    if (marker != null) {
                        markerCafeMap[marker] = cafe
                    }
                }
            } catch (_: Exception) {
            }
        }
    }

    // Convert café image into marker icon
    private fun getCafeMarkerIcon(imagePath: String?): BitmapDescriptor {
        if (imagePath.isNullOrBlank()) {
            return BitmapDescriptorFactory.defaultMarker(
                BitmapDescriptorFactory.HUE_ORANGE
            )
        }

        val file = File(imagePath)
        if (!file.exists()) {
            return BitmapDescriptorFactory.defaultMarker(
                BitmapDescriptorFactory.HUE_ORANGE
            )
        }

        val bitmap = BitmapFactory.decodeFile(file.absolutePath)
        val scaled = Bitmap.createScaledBitmap(bitmap, 120, 120, false)

        return BitmapDescriptorFactory.fromBitmap(scaled)
    }
}
