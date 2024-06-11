package com.example.readify.activities

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.example.readify.R

class Signin : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_signin)


        val textViewForgotPassword: TextView = findViewById(R.id.txt_forgot_password)
        val editTextEmail: EditText = findViewById(R.id.et_email)
        val editTextPassword: EditText = findViewById(R.id.et_password)
        textViewForgotPassword.setOnClickListener {
            val intent = Intent(this, ForgotPassword::class.java)
            startActivity(intent)
        }

        val buttonSignIn: Button = findViewById(R.id.btn_sign_in)
        buttonSignIn.setOnClickListener {
            val intent = Intent(this, Home::class.java)
            startActivity(intent)
        }

        val textViewSignup: TextView = findViewById(R.id.txt_sign_up)
        textViewSignup.setOnClickListener {
            val intent = Intent(this, Signup::class.java)
            startActivity(intent)
        }
    }
}