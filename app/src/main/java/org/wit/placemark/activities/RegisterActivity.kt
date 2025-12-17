package org.wit.placemark.activities

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import org.wit.placemark.databinding.ActivityRegisterBinding
import org.wit.placemark.main.MainApp
import org.wit.placemark.main.SessionManager
import org.wit.placemark.models.UserModel

private val ActivityRegisterBinding.btnRegister: Any

class RegisterActivity : AppCompatActivity() {

    private lateinit var binding: ActivityRegisterBinding
    private lateinit var app: MainApp

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityRegisterBinding.inflate(layoutInflater)
        setContentView(binding.root)

        app = application as MainApp

        binding.btnRegister.setOnClickListener {
            val email = binding.registerEmail.text.toString()
            val password = binding.registerPassword.text.toString()

            if (email.isBlank() || password.isBlank()) {
                Toast.makeText(this, "Please enter email and password", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val success = app.users.register(
                UserModel(email = email, password = password)
            )

            if (success) {
                val user = app.users.login(email, password)!!
                SessionManager.login(this, user.id)
                startActivity(Intent(this, CafeListActivity::class.java))
                finish()
            } else {
                Toast.makeText(this, "User already exists", Toast.LENGTH_SHORT).show()
            }
        }

        binding.goLogin.setOnClickListener {
            finish()
        }
    }
}
