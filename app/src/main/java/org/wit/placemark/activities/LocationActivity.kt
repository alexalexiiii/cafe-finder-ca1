package org.wit.placemark.activities

import android.app.Activity
import android.content.Intent
import android.location.Geocoder
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import org.wit.placemark.R
import java.util.Locale

class LocationActivity : AppCompatActivity(), OnMapReadyCallback {

    private lateinit var map: GoogleMap
    private var selectedLatLng: LatLng? = null
    private var selectedName: String = "Selected location"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_location)

        val mapFragment =
            supportFragmentManager.findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)

        findViewById<Button>(R.id.confirmLocationBtn).setOnClickListener {
            val resultIntent = Intent()
            resultIntent.putExtra("location_name", selectedName)
            setResult(Activity.RESULT_OK, resultIntent)
            finish()
        }
    }

    override fun onMapReady(googleMap: GoogleMap) {
        map = googleMap

        // Ireland default
        val ireland = LatLng(53.3498, -6.2603)
        map.moveCamera(CameraUpdateFactory.newLatLngZoom(ireland, 13f))

        // Tap anywhere to drop pin
        map.setOnMapClickListener { latLng ->
            map.clear()
            map.addMarker(MarkerOptions().position(latLng))
            selectedLatLng = latLng
            selectedName = reverseGeocode(latLng)
        }

        // Tap café POIs (automatic nearby cafés)
        map.setOnPoiClickListener { poi ->
            map.clear()
            map.addMarker(
                MarkerOptions()
                    .position(poi.latLng)
                    .title(poi.name)
            )
            selectedLatLng = poi.latLng
            selectedName = poi.name
        }
    }

    private fun reverseGeocode(latLng: LatLng): String {
        return try {
            val geocoder = Geocoder(this, Locale.getDefault())
            val addresses = geocoder.getFromLocation(
                latLng.latitude,
                latLng.longitude,
                1
            )

            addresses?.firstOrNull()?.featureName
                ?: addresses?.firstOrNull()?.locality
                ?: "Selected location"
        } catch (e: Exception) {
            "Selected location"
        }
    }
}
