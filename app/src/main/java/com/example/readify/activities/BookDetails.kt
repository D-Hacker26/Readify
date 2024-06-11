package com.example.readify.activities

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.ImageView
import com.example.readify.R

class BookDetails : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_book_details)

        val buttonRead: Button = findViewById(R.id.btn_read)
        val buttonLike: Button = findViewById(R.id.btn_like)
        val buttonDownload: Button = findViewById(R.id.btn_download)

        val buttonBack: Button = findViewById(R.id.btn_back)
        buttonBack.setOnClickListener {
            val intent = Intent(this, Home::class.java)
            startActivity(intent)
            finish()
        }
    }
}