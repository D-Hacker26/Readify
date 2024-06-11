package com.example.readify.activities

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import com.example.readify.R

class Profile : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile)

        val buttonBack: ImageView = findViewById(R.id.iv_back)
        val buttonEdit: ImageView = findViewById(R.id.iv_edit)
        val textName: TextView = findViewById(R.id.txt_name)
        val textEmail: TextView = findViewById(R.id.txt_email)
        val textAccount: TextView = findViewById(R.id.txt_account)
        val textDate: TextView = findViewById(R.id.txt_date)
        val textFavourites: TextView = findViewById(R.id.txt_favourites)
        val textStatus: TextView = findViewById(R.id.txt_status)

        buttonBack.setOnClickListener {
            val intent = Intent(this, Home::class.java)
            startActivity(intent)
        }
    }
}