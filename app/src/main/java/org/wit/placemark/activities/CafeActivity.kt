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
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.snackbar.Snackbar
import org.wit.placemark.R
import org.wit.placemark.databinding.ActivityCafeBinding
import org.wit.placemark.main.MainApp
import org.wit.placemark.models.CafeModel
import timber.log.Timber.i
import androidx.core.net.toUri
import java.io.File

class CafeActivity : AppCompatActivity() {

    private lateinit var binding: ActivityCafeBinding
    private lateinit var app: MainApp

    private lateinit var cameraLauncher: ActivityResultLauncher<Uri>
    private lateinit var imageUri: Uri

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
            binding.placemarkLocation.text = cafe.location
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
            cafe.location = binding.placemarkLocation.text.toString()
            cafe.rating = binding.ratingBar.rating.toInt()

            if (cafe.name.isNotEmpty()) {
                if (intent.hasExtra("cafe_edit")) app.cafes.update(cafe.copy())
                else app.cafes.create(cafe.copy())

                i("Saved Cafe: $cafe")
                setResult(RESULT_OK)
                finish()
            } else {
                Snackbar.make(view, R.string.please_enter_name, Snackbar.LENGTH_LONG).show()
            }
        }

        // CAMERA BUTTON
        binding.chooseImage.setOnClickListener {
            val imageFile = File.createTempFile(
                "cafe_image_",
                ".jpg",
                cacheDir
            )

            imageUri = imageFile.toUri()
            cameraLauncher.launch(imageUri)
        }

        registerCameraCallback()
    }

    // Camera result handler
    private fun registerCameraCallback() {
        cameraLauncher =
            registerForActivityResult(ActivityResultContracts.TakePicture()) { success ->
                if (success) {
                    i("Image captured: $imageUri")
                    cafe.image = imageUri.toString()
                    binding.cafeImage.setImageURI(imageUri)
                    binding.chooseImage.text = getString(R.string.change_image)
                }
            }
    }

    // Toolbar menu
    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_main, menu)
        return super.onCreateOptionsMenu(menu)
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
                            i("Deleted: $cafe")
                            setResult(RESULT_OK)
                            finish()
                        }
                        .setNegativeButton("Cancel", null)
                        .show()
                } else {
                    Snackbar.make(binding.root, "Nothing to delete", Snackbar.LENGTH_SHORT).show()
                }
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }
}
