package com.example.readify.activities

import android.annotation.SuppressLint
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.widget.AppCompatButton
import androidx.appcompat.widget.AppCompatEditText
import com.example.readify.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class NewCategory : AppCompatActivity() {
    private lateinit var firestore: FirebaseFirestore
    private lateinit var auth: FirebaseAuth

    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_new_category)

        firestore = FirebaseFirestore.getInstance()
        auth = FirebaseAuth.getInstance()

        val editTextCategory: AppCompatEditText = findViewById(R.id.et_category)
        val buttonSubmit: AppCompatButton = findViewById(R.id.btn_submit)
        val buttonBack: AppCompatButton = findViewById(R.id.btn_back)
        buttonBack.setOnClickListener {
            val intent = Intent(this, Home::class.java)
            startActivity(intent)
            finish()
        }

        buttonSubmit.setOnClickListener {
            val category = editTextCategory.text.toString().trim()
            if (category.isNotEmpty()) {
                addCategoryToFirestore(category)
            } else {
                editTextCategory.error = "Category name is required"
                editTextCategory.requestFocus()
            }
        }
    }

    private fun addCategoryToFirestore(category: String) {
        val currentUser = auth.currentUser
        if (currentUser != null) {
            val userId = currentUser.uid
            val categoryData = hashMapOf(
                "name" to category,
                "userId" to userId
            )
            firestore.collection("categories")
                .add(categoryData)
                .addOnSuccessListener {
                    Toast.makeText(this, "Category added successfully", Toast.LENGTH_LONG).show()
                    val intent = Intent(this, Home::class.java)
                    startActivity(intent)
                    finish()
                }
                .addOnFailureListener { e ->
                    Toast.makeText(this, "Failed to add category: ${e.message}", Toast.LENGTH_LONG).show()
                }
        } else {
            Toast.makeText(this, "User not logged in", Toast.LENGTH_LONG).show()
        }
    }
}