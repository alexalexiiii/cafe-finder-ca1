package org.wit.placemark.activities

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
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

/**
 * CafeActivity
 * -------------
 * This activity allows the user to create, edit, and delete Café journal entries.
 * Each café includes name, favourite item, location, rating, and an image.
 * It demonstrates key Android app components such as:
 * - ViewBinding
 * - ActivityResultLauncher for image selection
 * - Data persistence via MainApp reference
 * - Material Design components (Snackbar, AlertDialog)
 */
class CafeActivity : AppCompatActivity() {
// viewbinding obj for layout access
    private lateinit var binding: ActivityCafeBinding
    // ref to main application (data access)
    private lateinit var app: MainApp

    // used to launch the image picker and handle returned image url

    private lateinit var imageIntentLauncher: ActivityResultLauncher<Intent>

    // this is the model that is  representing the café being created or edited
    private var cafe = CafeModel()

    // called when activity is first initiated
    // sets up viewbinding, toolbar and logic for edit mode/new entry creation
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // Inflate layout via ViewBinding and set as content view
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
            binding.location.setText(cafe.location)
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
            cafe.location = binding.location.text.toString()
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

        // Choose Image button
        binding.chooseImage.setOnClickListener {
            val intent = Intent(Intent.ACTION_PICK)
            intent.type = "image/*"
            imageIntentLauncher.launch(intent)
        }

        registerImagePickerCallback()
    }

    private fun registerImagePickerCallback() {
        imageIntentLauncher =
            registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
                if (result.resultCode == Activity.RESULT_OK && result.data != null) {
                    val imageUri = result.data!!.data
                    if (imageUri != null) {
                        i("Image selected: $imageUri")
                        cafe.image = imageUri.toString()
                        binding.cafeImage.setImageURI(imageUri)
                        binding.chooseImage.text = getString(R.string.change_image)
                    }
                }
            }
    }

    // Toolbar Menu
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
