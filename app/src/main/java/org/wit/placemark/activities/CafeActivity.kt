package org.wit.placemark.activities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import com.google.android.material.snackbar.Snackbar
import org.wit.placemark.R
import org.wit.placemark.databinding.ActivityPlacemarkBinding
import org.wit.placemark.main.MainApp
import timber.log.Timber.i

class CafeActivity : AppCompatActivity() {

    private lateinit var binding: ActivityCafeBinding
    var cafe = CafeModel()

    //val placemarks = ArrayList<PlacemarkModel>()
    lateinit var app: MainApp

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityCafeBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.toolbarAdd.title = title
        setSupportActionBar(binding.toolbarAdd)

        app = application as MainApp

        i("Creating new Cafe entry...")

        if (intent.hasExtra("cafe_edit")) {
            cafe = intent.extras?.getParcelable("cafe_edit")!!
            binding.cafeName.setText(cafe.name)
            binding.favouriteItem.setText(cafe.favouriteMenuItem)
            binding.location.setText(cafe.location)
            binding.ratingBar.rating = cafe.rating.toFloat()
            if (cafe.image.isNotEmpty()) binding.cafeImage.setImageURI(Uri.parse(cafe.image))
        }

        binding.btnAdd.setOnClickListener
        {
            cafe.name = binding.cafeName.text.toString()
            cafe.favouriteMenuItem = binding.favouriteItem.text.toString()
            cafe.location = binding.location.text.toString()
            cafe.rating = binding.ratingBar.rating.toInt()
            if (cafe.name.isNotEmpty()) {
                if (intent.hasExtra("cafe_edit")) {
                    app.cafes.update(cafe.copy())
                } else {
                    app.cafes.create(cafe.copy())
                }

                i("Add/Edit Button Pressed: $cafe")
                setResult(RESULT_OK)
                finish()

            } else {
                Snackbar
                    .make(it, "Please enter a café name", Snackbar.LENGTH_LONG)
                    .show()
            }
        }

        override fun onCreateOptionsMenu(menu: Menu): Boolean {
            menuInflater.inflate(R.menu.menu_cafetracker, menu)
            return super.onCreateOptionsMenu(menu)
        }

        override fun onOptionsItemSelected(item: MenuItem): Boolean {
            when (item.itemId) {
                R.id.item_cancel -> {
                    finish()
                }
            }
            return super.onOptionsItemSelected(item)
        }

    }
}