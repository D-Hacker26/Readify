package com.example.readify.activities

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.Spinner
import com.example.readify.R

class NewBook : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_new_book)

        val spinner : Spinner= findViewById(R.id.my_spinner)
        ArrayAdapter.createFromResource(
            this,
            R.array.my_spinner_array,
            android.R.layout.simple_spinner_item
        ).also { adapter ->
            // Specify the layout to use when the list of choices appears
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            // Apply the adapter to the spinner
            spinner.adapter = adapter
        }

        val editTextBookTitle: EditText = findViewById(R.id.et_book_title)
        val editTextBookDescription: EditText = findViewById(R.id.et_book_description)
        val buttonUpload: Button = findViewById(R.id.btn_upload)

        val buttonBack: Button = findViewById(R.id.btn_back)
        buttonBack.setOnClickListener {
            val intent = Intent(this, Home::class.java)
            startActivity(intent)
            finish()
        }
    }
}