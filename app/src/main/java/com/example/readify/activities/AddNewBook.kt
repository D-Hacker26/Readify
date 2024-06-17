package com.example.readify.activities

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.*
import androidx.appcompat.widget.AppCompatButton
import androidx.appcompat.widget.AppCompatEditText
import com.example.readify.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class AddNewBook : AppCompatActivity() {

    private lateinit var firestore: FirebaseFirestore
    private lateinit var auth: FirebaseAuth
    private lateinit var spinner: Spinner
    private lateinit var editTextBookTitle: AppCompatEditText
    private lateinit var editTextBookDescription: AppCompatEditText
    private lateinit var buttonUpload: AppCompatButton
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_new_book)

        firestore = FirebaseFirestore.getInstance()
        auth = FirebaseAuth.getInstance()

        editTextBookTitle = findViewById(R.id.et_book_title)
        editTextBookDescription = findViewById(R.id.et_book_description)
        buttonUpload = findViewById(R.id.btn_upload)
        spinner = findViewById(R.id.my_spinner)
        val buttonBack: AppCompatButton = findViewById(R.id.btn_back)

        fetchCategories()

        buttonUpload.setOnClickListener {
            uploadBook()
        }

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


        buttonBack.setOnClickListener {
            val intent = Intent(this, Home::class.java)
            startActivity(intent)
            finish()
        }
    }

    private fun uploadBook() {
        val currentUser = auth.currentUser
        if (currentUser != null) {
            val userId = currentUser.uid
            val bookTitle = editTextBookTitle.text.toString().trim()
            val bookDescription = editTextBookDescription.text.toString().trim()
            val selectedCategory = spinner.selectedItem.toString()

            if (bookTitle.isNotEmpty() && bookDescription.isNotEmpty() && selectedCategory.isNotEmpty()) {
                val bookData = hashMapOf(
                    "title" to bookTitle,
                    "description" to bookDescription,
                    "category" to selectedCategory,
                    "userId" to userId
                )

                firestore.collection("books")
                    .add(bookData)
                    .addOnSuccessListener {
                        Toast.makeText(this, "Book uploaded successfully", Toast.LENGTH_LONG).show()
                        clearFields()
                    }
                    .addOnFailureListener { e ->
                        Toast.makeText(this, "Failed to upload book: ${e.message}", Toast.LENGTH_LONG).show()
                    }
            } else {
                Toast.makeText(this, "All fields are required", Toast.LENGTH_LONG).show()
            }
        } else {
            Toast.makeText(this, "User not logged in", Toast.LENGTH_LONG).show()
        }
    }

    private fun clearFields() {
        editTextBookTitle.text?.clear()
        editTextBookDescription.text?.clear()
        spinner.setSelection(0)
    }

    private fun fetchCategories() {
        val currentUser = auth.currentUser
        if (currentUser != null) {
            val userId = currentUser.uid
            firestore.collection("categories")
                .whereEqualTo("userId", userId)
                .get()
                .addOnSuccessListener { documents ->
                    val categories = mutableListOf<String>()
                    for (document in documents) {
                        val category = document.getString("name")
                        if (category != null) {
                            categories.add(category)
                        }
                    }
                    setUpSpinner(categories)
                }
                .addOnFailureListener { e ->
                    Toast.makeText(
                        this,
                        "Failed to fetch categories: ${e.message}",
                        Toast.LENGTH_LONG
                    ).show()
                }
        } else {
            Toast.makeText(this, "User not logged in", Toast.LENGTH_LONG).show()
        }
    }

    private fun setUpSpinner(categories: List<String>) {
        val spinner: Spinner = findViewById(R.id.my_spinner)
        val adapter = ArrayAdapter(
            this,
            android.R.layout.simple_spinner_item,
            categories
        )
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinner.adapter = adapter
    }
}