package com.example.readify.activities

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.ImageView
import com.example.readify.R

class Home : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)

        val buttonProfile: ImageView = findViewById(R.id.btn_profile)
        buttonProfile.setOnClickListener {
            val intent = Intent(this, Profile::class.java)
            startActivity(intent)
            finish()
        }

        val buttonAddCategory: Button = findViewById(R.id.btn_add_category)
        buttonAddCategory.setOnClickListener {
            val intent = Intent(this, NewCategory::class.java)
            startActivity(intent)
            finish()
        }

        val buttonAddBook: Button = findViewById(R.id.btn_add_book)
        buttonAddBook.setOnClickListener {
            val intent = Intent(this, AddNewBook::class.java)
            startActivity(intent)
            finish()
        }
    }
}