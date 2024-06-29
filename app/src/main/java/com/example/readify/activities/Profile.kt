package com.example.readify.activities

import android.content.Intent
import android.os.Bundle
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.example.readify.R
import com.example.readify.extensions.Extensions.toast
import com.example.readify.utils.FirebaseUtils.firebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class Profile : AppCompatActivity() {

    private lateinit var db: FirebaseFirestore
    private lateinit var textName: TextView
    private lateinit var textEmail: TextView
    private lateinit var textDate: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile)

        textName = findViewById(R.id.txt_name)
        textEmail = findViewById(R.id.txt_email)
        textDate = findViewById(R.id.txt_date)

        db = FirebaseFirestore.getInstance()

        val currentUser = firebaseAuth.currentUser
        currentUser?.let {
            val email = it.email
            if (email != null) {
                db.collection("users").document(email).get().addOnSuccessListener { documentSnapshot ->
                    if (documentSnapshot.exists()) {
                        val name = documentSnapshot.getString("name")
                        val creationDate = documentSnapshot.getString("creation_date")

                        textName.text = name
                        textEmail.text = email
                        textDate.text = creationDate
                    }
                }.addOnFailureListener { exception ->
                    toast("Failed to load user data: ${exception.message}")
                }
            }
        }

        val editProfileButton: ImageView = findViewById(R.id.iv_edit)
        editProfileButton.setOnClickListener {
            startActivity(Intent(this, EditProfile::class.java))
        }
    }
}
