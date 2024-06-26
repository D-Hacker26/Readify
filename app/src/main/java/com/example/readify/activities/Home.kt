package com.example.readify.activities

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.ImageView
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.readify.R
import com.example.readify.adapters.BookAdapter
import com.example.readify.data.Book
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class Home : AppCompatActivity(), BookAdapter.OnItemClickListener {

    private lateinit var firestore: FirebaseFirestore
    private lateinit var auth: FirebaseAuth
    private lateinit var recyclerView: RecyclerView
    private lateinit var bookAdapter: BookAdapter
    private val bookList = mutableListOf<Book>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)

        firestore = FirebaseFirestore.getInstance()
        auth = FirebaseAuth.getInstance()
        recyclerView = findViewById(R.id.recycler_view_books)

        bookAdapter = BookAdapter(bookList, this)
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = bookAdapter

        fetchBooks()

        val buttonProfile: ImageView = findViewById(R.id.btn_profile)
        buttonProfile.setOnClickListener {
            val intent = Intent(this, Profile::class.java)
            startActivity(intent)
            finish()
        }

        val buttonAddCategory: Button = findViewById(R.id.btn_add_category)
        buttonAddCategory.setOnClickListener {
            val intent = Intent(this, NewCategory::class.java)
            startActivity(intent)
            finish()
        }

        val buttonAddBook: Button = findViewById(R.id.btn_add_book)
        buttonAddBook.setOnClickListener {
            val intent = Intent(this, AddNewBook::class.java)
            startActivity(intent)
            finish()
        }
    }

    private fun fetchBooks() {
        val currentUser = auth.currentUser
        if (currentUser != null) {
            val userId = currentUser.uid
            firestore.collection("books")
                .whereEqualTo("userId", userId)
                .get()
                .addOnSuccessListener { documents ->
                    bookList.clear()
                    for (document in documents) {
                        val book = document.toObject(Book::class.java)
                        Log.d("BookFetched", "Fetched book: $book")
                        bookList.add(book)
                    }
                    bookAdapter.notifyDataSetChanged()
                }
                .addOnFailureListener { e ->
                    Toast.makeText(this, "Failed to fetch books: ${e.message}", Toast.LENGTH_LONG).show()
                }
        } else {
            Toast.makeText(this, "User not logged in", Toast.LENGTH_LONG).show()
        }
    }

    override fun onItemClick(book: Book) {
        val intent = Intent(this, BookDetails::class.java)
        intent.putExtra("book", book)
        startActivity(intent)
    }
}
