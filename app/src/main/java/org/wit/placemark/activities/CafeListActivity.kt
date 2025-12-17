package org.wit.placemark.activities

import androidx.appcompat.widget.SearchView
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.navigation.NavigationView
import com.google.android.material.snackbar.Snackbar
import org.wit.placemark.R
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
 * - Delete ALL cafés via toolbar button
 * - Edit or delete individual cafés
 *
 * Implements the CafeListener interface to handle list item actions.
 */

class CafeListActivity : AppCompatActivity(), CafeListener {

    private lateinit var binding: ActivityCafeListBinding
    lateinit var app: MainApp
    private lateinit var adapter: CafeAdapter

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

        // Search filter
        binding.cafeSearch.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?) = false

            override fun onQueryTextChange(newText: String?): Boolean {
                val filtered = app.cafes.findAll().filter {
                    it.name.contains(newText ?: "", true) ||
                            it.location.contains(newText ?: "", true)
                }
                adapter.updateList(filtered)
                return true
            }
        })
    }

    // Inflate toolbar menu
    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }

    // Handle toolbar actions
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {

            R.id.item_delete_all -> {
                confirmDeleteAll()
                true
            }

            else -> super.onOptionsItemSelected(item)
        }
    }

    // Confirm delete ALL cafés
    private fun confirmDeleteAll() {
        if (app.cafes.findAll().isEmpty()) {
            Snackbar.make(binding.root, "No cafés to delete", Snackbar.LENGTH_SHORT).show()
            return
        }

        MaterialAlertDialogBuilder(this)
            .setTitle("Delete All Cafés")
            .setMessage("This will permanently delete ALL cafés. Are you sure?")
            .setPositiveButton("Delete All") { _, _ ->
                app.cafes.findAll().toList().forEach {
                    app.cafes.delete(it)
                }
                adapter.updateList(emptyList())
                Snackbar.make(binding.root, "All cafés deleted", Snackbar.LENGTH_SHORT).show()
            }
            .setNegativeButton("Cancel", null)
            .show()
    }

    // Recycler item click → edit café
    override fun onCafeClick(cafe: CafeModel) {
        val intent = android.content.Intent(this, CafeActivity::class.java)
        intent.putExtra("cafe_edit", cafe)
        startActivity(intent)
    }

    // Individual delete
    override fun onCafeDeleteClick(cafe: CafeModel) {
        MaterialAlertDialogBuilder(this)
            .setTitle("Delete Café")
            .setMessage("Delete '${cafe.name}'?")
            .setPositiveButton("Delete") { _, _ ->
                app.cafes.delete(cafe)
                adapter.updateList(app.cafes.findAll())
                Snackbar.make(binding.root, "Deleted ${cafe.name}", Snackbar.LENGTH_SHORT).show()
            }
            .setNegativeButton("Cancel", null)
            .show()
    }
}
