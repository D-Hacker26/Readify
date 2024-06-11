package com.example.readify.activities

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.example.readify.R
import com.example.readify.extensions.Extensions.toast
import com.example.readify.utils.FirebaseUtils.firebaseAuth
import com.google.firebase.auth.FirebaseUser

class Signup : AppCompatActivity() {
    lateinit var userEmail: String
    lateinit var userPassword: String
    lateinit var createAccountInputsArray: Array<EditText>
    lateinit var editTextEmail: EditText
    lateinit var editTextConfirmPassword: EditText
    lateinit var editTextPassword: EditText
    lateinit var editTextFullName: EditText

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_signup)

        val buttonSignUp: Button = findViewById(R.id.btn_sign_up)
        editTextFullName= findViewById(R.id.et_full_name)
        editTextPassword= findViewById(R.id.et_password)
        editTextConfirmPassword = findViewById(R.id.et_confirm_password)
        editTextEmail = findViewById(R.id.et_email)
        createAccountInputsArray =
            arrayOf(editTextEmail, editTextPassword, editTextConfirmPassword, editTextFullName)




        buttonSignUp.setOnClickListener {
            val intent = Intent(this, Home::class.java)
            startActivity(intent)
            finish()
        }
        val textViewSignIn: TextView = findViewById(R.id.txt_sign_in)
        textViewSignIn.setOnClickListener {
            val intent = Intent(this, Signin::class.java)
            startActivity(intent)
        }
    }

    /* check if there's a signed-in user*/
    override fun onStart() {
        super.onStart()
        val user: FirebaseUser? = firebaseAuth.currentUser
        user?.let {
            startActivity(Intent(this, Home::class.java))
            toast("welcome back")
        }
    }

    private fun notEmpty(): Boolean = editTextEmail.text.toString().trim().isNotEmpty() &&
            editTextPassword.text.toString().trim().isNotEmpty() &&
            editTextConfirmPassword.text.toString().trim().isNotEmpty() 

    private fun identicalPassword(): Boolean {
        var identical = false
        if (notEmpty() &&
            editTextPassword.text.toString().trim() == editTextConfirmPassword.text.toString().trim()
        ) {
            identical = true
        } else if (!notEmpty()) {
            createAccountInputsArray.forEach { input ->
                if (input.text.toString().trim().isEmpty()) {
                    input.error = "${input.hint} is required"
                }
            }
        } else {
            toast("passwords are not matching !")
        }
        return identical
    }

}