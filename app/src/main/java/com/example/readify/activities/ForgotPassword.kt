package com.example.readify.activities

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import androidx.appcompat.widget.AppCompatButton
import androidx.appcompat.widget.AppCompatEditText
import com.example.readify.R

class ForgotPassword : AppCompatActivity() {
    lateinit var editTextEmail: AppCompatEditText

    override fun onCreate(savedInstanceState: Bundle?) {
        editTextEmail = findViewById(R.id.et_email)

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_forgot_password)

        val buttonSubmit: AppCompatButton = findViewById(R.id.btn_submit)
        buttonSubmit.setOnClickListener {

        }

        val buttonBack: AppCompatButton = findViewById(R.id.btn_back)
        buttonBack.setOnClickListener {
            val intent = Intent(this, Signin::class.java)
            startActivity(intent)
            finish()
        }
    }
}