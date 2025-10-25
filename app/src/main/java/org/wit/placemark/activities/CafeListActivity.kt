package org.wit.placemark.activities
import androidx.appcompat.widget.SearchView
import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.snackbar.Snackbar
import org.wit.placemark.adapters.CafeAdapter
import org.wit.placemark.adapters.CafeListener
import org.wit.placemark.databinding.ActivityCafeListBinding
import org.wit.placemark.main.MainApp
import org.wit.placemark.models.CafeModel

/**
 * CafeListActivity
 * ----------------
 * This activity displays all café entries using a RecyclerView.
 * Users can:
 * - View all cafés in a scrollable list
 * - Search/filter cafés by name or location
 * - Add new cafés via the toolbar
 * - Edit or delete existing cafés via item click
 *
 * Implements the CafeListener interface to handle list item actions.
 */

class CafeListActivity : AppCompatActivity(), CafeListener {

    private lateinit var binding: ActivityCafeListBinding
    lateinit var app: MainApp
    private lateinit var adapter: CafeAdapter

    // called when cafe activity is initiated
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCafeListBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(binding.toolbar)
        app = application as MainApp

        // RecyclerView setup
        adapter = CafeAdapter(app.cafes.findAll(), this)
        binding.recyclerViewCafes.layoutManager = LinearLayoutManager(this)
        binding.recyclerViewCafes.adapter = adapter


        // SearchView filter
        // live filtering by cafe name or location
        binding.cafeSearch.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean = false

            override fun onQueryTextChange(newText: String?): Boolean {
                val filtered = app.cafes.findAll().filter {
                    it.name.contains(newText ?: "", ignoreCase = true) ||
                            it.location.contains(newText ?: "", ignoreCase = true)
                }
                adapter.updateList(filtered)
                return true
            }
        })
    }

    // Inflate menu
    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(org.wit.placemark.R.menu.menu_main, menu)
        return super.onCreateOptionsMenu(menu)
    }

    // Handle menu clicks
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            org.wit.placemark.R.id.item_add -> {
                val intent = Intent(this, CafeActivity::class.java)
                startActivity(intent)
            }
        }
        return super.onOptionsItemSelected(item)
    }

    // Handle clicks from RecyclerView items
    override fun onCafeClick(cafe: CafeModel) {
        val intent = Intent(this, CafeActivity::class.java)
        intent.putExtra("cafe_edit", cafe)
        startActivity(intent)
    }

    // handling cafe deletion from the cafe card
    // displays message to confirm using snackbar
    override fun onCafeDeleteClick(cafe: CafeModel) {
        MaterialAlertDialogBuilder(this)
            .setTitle("Delete Café")
            .setMessage("Are you sure you want to delete '${cafe.name}'?")
            .setPositiveButton("Delete") { _, _ ->
                (application as MainApp).cafes.delete(cafe)
                loadCafes() // refresh the RecyclerView
                Snackbar.make(findViewById(android.R.id.content), "Deleted ${cafe.name}", Snackbar.LENGTH_SHORT).show()
            }
            .setNegativeButton("Cancel", null)
            .show()
    }

    // Utility function to refresh the RecyclerView after changes (delete or edit)
    private fun loadCafes() {
        val cafes = app.cafes.findAll()
        adapter.updateList(cafes)
    }

}
