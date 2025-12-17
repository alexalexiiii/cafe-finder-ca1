package org.wit.placemark.activities

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.view.Menu
import android.view.MenuItem
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.FileProvider
import androidx.core.net.toUri
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.snackbar.Snackbar
import org.wit.placemark.R
import org.wit.placemark.databinding.ActivityCafeBinding
import org.wit.placemark.main.MainApp
import org.wit.placemark.models.CafeModel
import timber.log.Timber.i
import java.io.File

class CafeActivity : AppCompatActivity() {

    private lateinit var binding: ActivityCafeBinding
    private lateinit var app: MainApp

    private lateinit var galleryLauncher: ActivityResultLauncher<Intent>
    private lateinit var cameraLauncher: ActivityResultLauncher<Intent>
    private lateinit var mapIntentLauncher: ActivityResultLauncher<Intent>

    private var imageUri: Uri? = null
    private var cafe = CafeModel()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityCafeBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setSupportActionBar(binding.toolbarAdd)

        app = application as MainApp
        i("CafeActivity started")

        // Edit mode
        if (intent.hasExtra("cafe_edit")) {
            cafe = intent.extras?.getParcelable("cafe_edit")!!
            binding.cafeName.setText(cafe.name)
            binding.favouriteItem.setText(cafe.favouriteMenuItem)
            binding.locationText.setText(cafe.location)
            binding.ratingBar.rating = cafe.rating.toFloat()

            if (cafe.image.isNotEmpty()) {
                binding.cafeImage.setImageURI(cafe.image.toUri())
                binding.chooseImage.text = getString(R.string.change_image)
            }
        }

        // Save Café
        binding.btnAdd.setOnClickListener { view ->
            cafe.name = binding.cafeName.text.toString()
            cafe.favouriteMenuItem = binding.favouriteItem.text.toString()
            cafe.location = binding.locationText.text.toString()
            cafe.rating = binding.ratingBar.rating.toInt()

            if (cafe.name.isNotEmpty()) {
                if (intent.hasExtra("cafe_edit")) app.cafes.update(cafe.copy())
                else app.cafes.create(cafe.copy())

                setResult(RESULT_OK)
                finish()
            } else {
                Snackbar.make(view, R.string.please_enter_name, Snackbar.LENGTH_LONG).show()
            }
        }

        binding.chooseImage.setOnClickListener {
            showImageSourceDialog()
        }

        // open map on location button click
        binding.setLocationBtn.setOnClickListener {
            val intent = Intent(this, LocationActivity::class.java)
            mapIntentLauncher.launch(intent)
        }

        registerGalleryCallback()
        registerCameraCallback()
        registerMapCallback()
    }

    // ask if the user wants to choose from gallery or take a photo
    private fun showImageSourceDialog() {
        val options = arrayOf("Take Photo", "Choose from Gallery")

        MaterialAlertDialogBuilder(this)
            .setTitle("Add Café Image")
            .setItems(options) { _, which ->
                when (which) {
                    0 -> openCamera()
                    1 -> openGallery()
                }
            }
            .show()
    }

    // choosing from gallery functionality
    private fun openGallery() {
        val intent = Intent(Intent.ACTION_PICK)
        intent.type = "image/*"
        galleryLauncher.launch(intent)
    }

    private fun registerGalleryCallback() {
        galleryLauncher =
            registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
                if (result.resultCode == Activity.RESULT_OK && result.data != null) {
                    val uri = result.data!!.data
                    if (uri != null) {
                        cafe.image = uri.toString()
                        binding.cafeImage.setImageURI(uri)
                        binding.chooseImage.text = getString(R.string.change_image)
                    }
                }
            }
    }

    // camera functionality
    private fun openCamera() {
        val photoFile = File.createTempFile(
            "cafe_image_",
            ".jpg",
            cacheDir
        )

        imageUri = FileProvider.getUriForFile(
            this,
            "${packageName}.provider",
            photoFile
        )

        val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        intent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri)
        cameraLauncher.launch(intent)
    }

    private fun registerCameraCallback() {
        cameraLauncher =
            registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
                if (result.resultCode == Activity.RESULT_OK && imageUri != null) {
                    cafe.image = imageUri.toString()
                    binding.cafeImage.setImageURI(imageUri)
                    binding.chooseImage.text = getString(R.string.change_image)
                }
            }
    }

    // receive location from map
    private fun registerMapCallback() {
        mapIntentLauncher =
            registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
                if (result.resultCode == Activity.RESULT_OK && result.data != null) {
                    val locationName = result.data!!.getStringExtra("location_name")
                    if (!locationName.isNullOrBlank()) {
                        cafe.location = locationName
                        binding.locationText.setText(locationName)
                    }
                }
            }
    }

    // ---------------- MENU ----------------

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.item_cancel -> {
                finish()
                true
            }
            R.id.item_delete -> {
                if (intent.hasExtra("cafe_edit")) {
                    MaterialAlertDialogBuilder(this)
                        .setTitle("Delete Café")
                        .setMessage("You will now delete '${cafe.name}'. Are you sure?")
                        .setPositiveButton("Delete") { _, _ ->
                            app.cafes.delete(cafe)
                            setResult(RESULT_OK)
                            finish()
                        }
                        .setNegativeButton("Cancel", null)
                        .show()
                }
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }
}
