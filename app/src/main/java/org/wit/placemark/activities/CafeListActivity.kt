package org.wit.placemark.activities
import android.app.Activity
import android.app.Activity.RESULT_OK
import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import org.wit.placemark.R
import org.wit.placemark.adapters.CafeAdapter
import org.wit.placemark.adapters.CafeListener
import org.wit.placemark.databinding.ActivityCafeBinding
import org.wit.placemark.main.MainApp

 class CafeListActivity : AppCompatActivity(), CafeListener {
     lateinit var app: MainApp
     private lateinit var binding: ActivityCafeBinding

     override fun onCreate(savedInstanceState: Bundle?) {
         super.onCreate(savedInstanceState)
         binding = ActivityCafeBinding.inflate(layoutInflater)
         setContentView(binding.root)
         binding.toolbar.title = title
         setSupportActionBar(binding.toolbar)

         app = application as MainApp

         val layoutManager = LinearLayoutManager(this)
         binding.recyclerView.layoutManager = layoutManager
         binding.recyclerView.adapter = CafeAdapter(app.cafes.findAll(),this)
     }

     override fun onCreateOptionsMenu(menu: Menu): Boolean {
         menuInflater.inflate(R.menu.menu_main, menu)
         return super.onCreateOptionsMenu(menu)
     }}

override fun onOptionsItemSelected(item: MenuItem): Boolean {
    when (item.itemId) {
        R.id.item_add -> {
            val launcherIntent = Intent(this, CafeActivity::class.java)
            getResult.launch(launcherIntent)
        }
    }
    return super.onOptionsItemSelected(item)
}

private val getResult =
    registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) {
        if (it.resultCode == RESULT_OK) {
            (binding.recyclerView.adapter)?.
            notifyItemRangeChanged(0,app.placemarks.findAll().size)
        }
    }

