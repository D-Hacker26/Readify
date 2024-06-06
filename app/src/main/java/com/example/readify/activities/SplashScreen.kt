package com.example.readify.activities

import android.annotation.SuppressLint
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.readify.R
import java.util.concurrent.Executors
import java.util.concurrent.TimeUnit

@SuppressLint("CustomSplashScreen")
class SplashScreen : AppCompatActivity() {
    private val splashTimeOut: Long = 3000 // 3 seconds

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash_screen)

        val executor = Executors.newSingleThreadScheduledExecutor()
        executor.schedule({
            runOnUiThread {
                // Start the main activity
                val intent = Intent(this, IntroductionActivity::class.java)
                startActivity(intent)
                finish()
            }
        }, splashTimeOut, TimeUnit.MILLISECONDS)
    }
}