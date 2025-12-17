package org.wit.placemark.activities

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import org.wit.placemark.databinding.ActivityLoginBinding
import org.wit.placemark.main.MainApp
import org.wit.placemark.main.SessionManager

class LoginActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLoginBinding
    private lateinit var app: MainApp

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        app = application as MainApp

        // LOGIN button
        binding.btnLogin.setOnClickListener {
            val email = binding.loginEmail.text.toString()
            val password = binding.loginPassword.text.toString()

            if (email.isBlank() || password.isBlank()) {
                Toast.makeText(this, "Please enter email and password", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val user = app.users.login(email, password)

            if (user != null) {
                SessionManager.login(this, user.id)
                startActivity(Intent(this, CafeListActivity::class.java))
                finish()
            } else {
                Toast.makeText(this, "Invalid login details", Toast.LENGTH_SHORT).show()
            }
        }

        // GO TO REGISTER
        binding.goRegister.setOnClickListener {
            startActivity(Intent(this, RegisterActivity::class.java))
        }
    }
}
