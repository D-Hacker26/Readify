package com.example.readify.activities

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.example.readify.R
import com.example.readify.extensions.Extensions.toast
import com.example.readify.utils.FirebaseUtils.firebaseAuth
import com.example.readify.utils.FirebaseUtils.firebaseUser
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.FirebaseAuthUserCollisionException
import com.google.firebase.auth.FirebaseAuthWeakPasswordException
import com.google.firebase.auth.FirebaseUser

class Signup : AppCompatActivity() {
    private lateinit var userEmail: String
    private lateinit var userPassword: String
    private lateinit var createAccountInputsArray: Array<EditText>
    private lateinit var editTextEmail: EditText
    private lateinit var editTextConfirmPassword: EditText
    private lateinit var editTextPassword: EditText
    private lateinit var editTextFullName: EditText

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_signup)

        val buttonSignUp: Button = findViewById(R.id.btn_sign_up)
        editTextFullName = findViewById(R.id.et_full_name)
        editTextPassword = findViewById(R.id.et_password)
        editTextConfirmPassword = findViewById(R.id.et_confirm_password)
        editTextEmail = findViewById(R.id.et_email)
        createAccountInputsArray =
            arrayOf(editTextEmail, editTextPassword, editTextConfirmPassword, editTextFullName)

        buttonSignUp.setOnClickListener {
            signIn()
        }

        val textViewSignIn: TextView = findViewById(R.id.txt_sign_in)
        textViewSignIn.setOnClickListener {
            val intent = Intent(this, Signin::class.java)
            startActivity(intent)
        }
    }

    override fun onStart() {
        super.onStart()
        val user: FirebaseUser? = firebaseAuth.currentUser
        user?.let {
            startActivity(Intent(this, Home::class.java))
            toast("Welcome back")
        }
    }

    private fun notEmpty(): Boolean = editTextEmail.text.toString().trim().isNotEmpty() &&
            editTextPassword.text.toString().trim().isNotEmpty() &&
            editTextConfirmPassword.text.toString().trim().isNotEmpty() &&
            editTextFullName.text.toString().trim().isNotEmpty()

    private fun identicalPassword(): Boolean {
        return if (notEmpty() &&
            editTextPassword.text.toString().trim() == editTextConfirmPassword.text.toString().trim()
        ) {
            true
        } else {
            if (!notEmpty()) {
                createAccountInputsArray.forEach { input ->
                    if (input.text.toString().trim().isEmpty()) {
                        input.error = "${input.hint} is required"
                    }
                }
            } else {
                toast("Passwords do not match!")
            }
            false
        }
    }

    private fun signIn() {
        if (identicalPassword()) {
            userEmail = editTextEmail.text.toString().trim()
            userPassword = editTextPassword.text.toString().trim()

            firebaseAuth.createUserWithEmailAndPassword(userEmail, userPassword)
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        toast("Created account successfully!")
                        sendEmailVerification()
                        startActivity(Intent(this, Home::class.java))
                        finish()
                    } else {
                        val exception = task.exception
                        Log.e("Signup", "Authentication failed", exception)
                        if (exception != null) {
                            handleSignUpError(exception)
                        } else {
                            toast("Failed to authenticate!")
                        }
                    }
                }
        }
    }

    private fun handleSignUpError(exception: Exception) {
        when (exception) {
            is FirebaseAuthWeakPasswordException -> {
                editTextPassword.error = "Password is too weak. Use at least 6 characters."
                editTextPassword.requestFocus()
            }
            is FirebaseAuthInvalidCredentialsException -> {
                editTextEmail.error = "Invalid email format."
                editTextEmail.requestFocus()
            }
            is FirebaseAuthUserCollisionException -> {
                editTextEmail.error = "This email is already in use."
                editTextEmail.requestFocus()
            }
            else -> {
                toast(exception.localizedMessage ?: "Authentication failed.")
            }
        }
    }

    private fun sendEmailVerification() {
        firebaseUser?.let {
            it.sendEmailVerification().addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    toast("Email sent to $userEmail")
                }
            }
        }
    }
}
