package com.example.readify.activities

import android.annotation.SuppressLint
import android.content.Intent
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.readify.R
import com.google.firebase.auth.FirebaseAuth
import java.util.concurrent.Executors
import java.util.concurrent.TimeUnit

@SuppressLint("CustomSplashScreen")
class SplashScreen : AppCompatActivity() {
    private val splashTimeOut: Long = 3000 // 3 seconds
    private lateinit var sharedPreferences: SharedPreferences
    private lateinit var firebaseAuth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash_screen)

        sharedPreferences = getSharedPreferences("ReadifyPrefs", MODE_PRIVATE)
        firebaseAuth = FirebaseAuth.getInstance()

        val executor = Executors.newSingleThreadScheduledExecutor()
        executor.schedule({
            runOnUiThread {
                // Start the main activity
//                val intent = Intent(this, IntroductionActivity::class.java)
//                startActivity(intent)
//                finish()
                checkFirstTimeUse()

            }
        }, splashTimeOut, TimeUnit.MILLISECONDS)
    }

    private fun checkFirstTimeUse() {
        val isFirstTime = sharedPreferences.getBoolean("isFirstTime", true)

        if (isFirstTime) {
            val editor = sharedPreferences.edit()
            editor.putBoolean("isFirstTime", false)
            editor.apply()

            // Show IntroductionActivity
            val intent = Intent(this, IntroductionActivity::class.java)
            startActivity(intent)
        }else {
            // Check if user has chosen to be remembered
            val isRemembered = sharedPreferences.getBoolean("isRemembered", false)
            if (isRemembered && firebaseAuth.currentUser != null) {
                // Show HomeActivity
                val intent = Intent(this, Home::class.java)
                startActivity(intent)
            } else {
                // Show SigninActivity
                val intent = Intent(this, Signin::class.java)
                startActivity(intent)
            }
        }
        finish()

    }
}