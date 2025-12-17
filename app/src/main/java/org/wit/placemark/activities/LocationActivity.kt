package org.wit.placemark.activities

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.widget.EditText
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.libraries.places.api.Places
import com.google.android.libraries.places.api.model.Place
import com.google.android.libraries.places.widget.Autocomplete
import com.google.android.libraries.places.widget.model.AutocompleteActivityMode
import org.wit.placemark.R

class LocationActivity : AppCompatActivity(), OnMapReadyCallback {

    private lateinit var map: GoogleMap
    private lateinit var placesLauncher: ActivityResultLauncher<Intent>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_location)

        // Initialise Places API (key already in manifest/strings)
        if (!Places.isInitialized()) {
            Places.initialize(applicationContext, getString(R.string.google_maps_key))
        }

        val mapFragment =
            supportFragmentManager.findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)

        registerPlacesCallback()

        // Search bar click launches Places
        findViewById<EditText>(R.id.searchPlaces).setOnClickListener {
            launchPlacesSearch()
        }
    }

    override fun onMapReady(googleMap: GoogleMap) {
        map = googleMap

        // Default view (Ireland)
        val ireland = LatLng(53.3498, -6.2603)
        map.moveCamera(CameraUpdateFactory.newLatLngZoom(ireland, 10f))
    }

    // ---------------- PLACES SEARCH ----------------

    private fun launchPlacesSearch() {
        val fields = listOf(
            Place.Field.DISPLAY_NAME,
            Place.Field.FORMATTED_ADDRESS,
            Place.Field.LOCATION
        )

        val intent = Autocomplete.IntentBuilder(
            AutocompleteActivityMode.OVERLAY,
            fields
        ).build(this)

        placesLauncher.launch(intent)
    }

    private fun registerPlacesCallback() {
        placesLauncher =
            registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
                if (result.resultCode == Activity.RESULT_OK && result.data != null) {

                    val place = Autocomplete.getPlaceFromIntent(result.data!!)
                    val location = place.location ?: return@registerForActivityResult

                    // Drop pin
                    map.clear()
                    map.addMarker(
                        MarkerOptions()
                            .position(location)
                            .title(place.displayName)
                    )
                    map.animateCamera(
                        CameraUpdateFactory.newLatLngZoom(location, 15f)
                    )

                    // Human-readable name only
                    val locationName =
                        place.displayName
                            ?: place.formattedAddress
                            ?: "Selected location"

                    val resultIntent = Intent()
                    resultIntent.putExtra("location", locationName)
                    setResult(Activity.RESULT_OK, resultIntent)
                    finish()
                }
            }
    }
}
