package org.wit.placemark.activities

import androidx.appcompat.widget.SearchView
import android.content.Intent
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
import androidx.drawerlayout.widget.DrawerLayout
import org.wit.placemark.R
import org.wit.placemark.adapters.CafeAdapter
import org.wit.placemark.adapters.CafeListener
import org.wit.placemark.databinding.ActivityCafeListBinding
import org.wit.placemark.main.MainApp
import org.wit.placemark.main.SessionManager
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

    private var showReturningOnly = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCafeListBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(binding.toolbar)
        app = application as MainApp

        val drawerLayout = binding.drawerLayout
        val navView = binding.navigationView

        val toggle = ActionBarDrawerToggle(
            this,
            drawerLayout,
            binding.toolbar,
            R.string.navigation_drawer_open,
            R.string.navigation_drawer_close
        )
        drawerLayout.addDrawerListener(toggle)
        toggle.syncState()

        navView.setNavigationItemSelectedListener { item ->
            when (item.itemId) {
                R.id.nav_add_cafe -> {
                    startActivity(Intent(this, CafeActivity::class.java))
                }
                R.id.nav_view_map -> {
                    startActivity(Intent(this, CafeMapActivity::class.java))
                }
                R.id.nav_logout -> {
                    SessionManager.logout(this)

                    val intent = Intent(this, LoginActivity::class.java)
                    intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                    startActivity(intent)
                }

            }
            drawerLayout.closeDrawer(GravityCompat.START)
            true
        }

        adapter = CafeAdapter(app.cafes.findAll(), this)
        binding.recyclerViewCafes.layoutManager = LinearLayoutManager(this)
        binding.recyclerViewCafes.adapter = adapter

        binding.cafeSearch.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?) = false

            override fun onQueryTextChange(newText: String?): Boolean {
                applyFilters(newText)
                return true
            }
        })
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_main, menu)
        menuInflater.inflate(R.menu.menu_filter, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {

            R.id.item_filter_returning -> {
                showReturningOnly = !showReturningOnly
                item.isChecked = showReturningOnly
                applyFilters(binding.cafeSearch.query.toString())
                true
            }

            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun applyFilters(query: String?) {
        val baseList = if (showReturningOnly) {
            app.cafes.findAll().filter { it.returning }
        } else {
            app.cafes.findAll()
        }

        val filtered = baseList.filter {
            it.name.contains(query ?: "", true) ||
                    it.location.contains(query ?: "", true)
        }

        adapter.updateList(filtered)
    }

    override fun onCafeClick(cafe: CafeModel) {
        val intent = Intent(this, CafeActivity::class.java)
        intent.putExtra("cafe_edit", cafe)
        startActivity(intent)
    }

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
    override fun onCafeDeleteClick(cafe: CafeModel) {
        MaterialAlertDialogBuilder(this)
            .setTitle("Delete Café")
            .setMessage("Are you sure you want to delete '${cafe.name}'?")
            .setPositiveButton("Delete") { _, _ ->
                app.cafes.delete(cafe)
                applyFilters(binding.cafeSearch.query.toString())
                Snackbar.make(binding.root, "Deleted ${cafe.name}", Snackbar.LENGTH_SHORT).show()
            }
            .setNegativeButton("Cancel", null)
            .show()
    }
}
