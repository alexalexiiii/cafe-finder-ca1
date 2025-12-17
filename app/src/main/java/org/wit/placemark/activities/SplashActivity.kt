package org.wit.placemark.activities

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import org.wit.placemark.R
import org.wit.placemark.main.SessionManager

class SplashActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)

        // Load GIF using Glide
        Glide.with(this)
            .asGif()
            .load(R.drawable.loading)
            .into(findViewById(R.id.splashGif))

        Handler(Looper.getMainLooper()).postDelayed({

            val nextActivity = if (SessionManager.isLoggedIn(this)) {
                CafeListActivity::class.java
            } else {
                LoginActivity::class.java
            }

            startActivity(Intent(this, nextActivity))
            finish()

        }, 1500)
    }
}
