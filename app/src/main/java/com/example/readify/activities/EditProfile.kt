package com.example.readify.activities

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity
import com.example.readify.R
import com.example.readify.extensions.Extensions.toast
import com.example.readify.utils.FirebaseUtils.firebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class EditProfile : AppCompatActivity() {

    private lateinit var db: FirebaseFirestore
    private lateinit var editName: EditText
    private lateinit var editEmail: EditText
    private lateinit var buttonSave: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit_profile)

        editName = findViewById(R.id.et_name)
        editEmail = findViewById(R.id.et_email)
        buttonSave = findViewById(R.id.btn_save)

        db = FirebaseFirestore.getInstance()

        val currentUser = firebaseAuth.currentUser
        currentUser?.let {
            val email = it.email
            if (email != null) {
                db.collection("users").document(email).get().addOnSuccessListener { documentSnapshot ->
                    if (documentSnapshot.exists()) {
                        val name = documentSnapshot.getString("name")
                        editName.setText(name)
                        editEmail.setText(email)
                    }
                }.addOnFailureListener { exception ->
                    toast("Failed to load user data: ${exception.message}")
                }
            }
        }

        buttonSave.setOnClickListener {
            saveProfile()
        }
    }

    private fun saveProfile() {
        val name = editName.text.toString()
        val email = editEmail.text.toString()

        val currentUser = firebaseAuth.currentUser
        currentUser?.let {
            val currentEmail = it.email
            if (currentEmail != null) {
                val userMap = mapOf(
                    "name" to name,
                    "email" to email
                )

                db.collection("users").document(currentEmail).set(userMap).addOnSuccessListener {
                    toast("Profile updated successfully!")
                    finish()
                }.addOnFailureListener { exception ->
                    toast("Failed to update profile: ${exception.message}")
                }
            }
        }
    }
}
