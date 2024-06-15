package com.example.readify.activities

import android.content.Intent
import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatButton
import androidx.appcompat.widget.AppCompatEditText
import com.example.readify.R
import com.example.readify.extensions.Extensions.toast
import com.example.readify.utils.FirebaseUtils.firebaseAuth

class Signin : AppCompatActivity() {
    lateinit var signInEmail: String
    lateinit var signInPassword: String
    lateinit var signInInputsArray: Array<AppCompatEditText>
    lateinit var textViewForgotPassword: TextView
    lateinit var editTextEmail: AppCompatEditText
    lateinit var editTextPassword: AppCompatEditText
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_signin)
        textViewForgotPassword = findViewById(R.id.txt_forgot_password)
        editTextEmail = findViewById(R.id.et_email)
        editTextPassword = findViewById(R.id.et_password)
        signInInputsArray = arrayOf(editTextEmail, editTextPassword)


        textViewForgotPassword.setOnClickListener {
            val intent = Intent(this, ForgotPassword::class.java)
            startActivity(intent)
        }
        val textViewSignup: TextView = findViewById(R.id.txt_sign_up)
        textViewSignup.setOnClickListener {
            val intent = Intent(this, Signup::class.java)
            startActivity(intent)
        }
        val buttonSignIn: AppCompatButton = findViewById(R.id.btn_sign_in)
        buttonSignIn.setOnClickListener {
            signInUser()
        }
    }

    private fun notEmpty(): Boolean = signInEmail.isNotEmpty() && signInPassword.isNotEmpty()

    private fun signInUser() {
        signInEmail = editTextEmail.text.toString().trim()
        signInPassword = editTextPassword.text.toString().trim()

        if (notEmpty()) {
            firebaseAuth.signInWithEmailAndPassword(signInEmail, signInPassword)
                .addOnCompleteListener { signIn ->
                    if (signIn.isSuccessful) {
                        startActivity(Intent(this, Home::class.java))
                        toast("signed in successfully")
                        finish()
                    } else {
                        toast("sign in failed")
                    }
                }
        } else {
            signInInputsArray.forEach { input ->
                if (input.text.toString().trim().isEmpty()) {
                    input.error = "${input.hint} is required"
                }
            }
        }
    }
}
