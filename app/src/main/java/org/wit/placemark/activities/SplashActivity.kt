package org.wit.placemark.activities
import android.os.Looper
import androidx.appcompat.app.AppCompatActivity
import com.google.android.gms.cast.framework.SessionManager

class SplashActivity : AppCompatActivity() {
        override fun onCreate(savedInstanceState: Bundle?) {
            super.onCreate(savedInstanceState)
            setContentView(R.layout.activity_splash)

            Handler(Looper.getMainLooper()).postDelayed({
                if (SessionManager.isLoggedIn(this)) {
                    startActivity(Intent(this, CafeListActivity::class.java))
                } else {
                    startActivity(Intent(this, LoginActivity::class.java))
                }
                finish()
            }, 1500)
        }
    }

}