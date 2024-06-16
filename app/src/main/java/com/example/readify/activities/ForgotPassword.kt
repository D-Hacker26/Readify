package com.example.readify.activities

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.widget.AppCompatButton
import androidx.appcompat.widget.AppCompatEditText
import com.example.readify.R
import com.example.readify.utils.FirebaseUtils.firebaseAuth

class ForgotPassword : AppCompatActivity() {
    lateinit var editTextEmail: AppCompatEditText

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_forgot_password)

        editTextEmail = findViewById(R.id.et_email)

        val buttonSubmit: AppCompatButton = findViewById(R.id.btn_submit)
        buttonSubmit.setOnClickListener {
            val email = editTextEmail.text.toString().trim()

            if (email.isNotEmpty()) {
                resetPassword(email)
            } else {
                editTextEmail.error = "Email is required"
                editTextEmail.requestFocus()
            }

        }

        val buttonBack: AppCompatButton = findViewById(R.id.btn_back)
        buttonBack.setOnClickListener {
            val intent = Intent(this, Signin::class.java)
            startActivity(intent)
            finish()
        }
    }
    private fun resetPassword(email: String) {
        firebaseAuth.sendPasswordResetEmail(email)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    Toast.makeText(this, "Password reset email sent to $email", Toast.LENGTH_LONG).show()
                } else {
                    Toast.makeText(this, "Failed to send password reset email", Toast.LENGTH_LONG).show()
                }
            }
    }
}