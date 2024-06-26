package com.example.readify.activities

import android.content.Intent
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import androidx.annotation.RequiresApi
import com.example.readify.R
import com.example.readify.data.Book

class BookDetails : AppCompatActivity() {
    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_book_details)

        val book: Book? = intent.getParcelableExtra("book", Book::class.java)

        // Use the book object
        if (book != null) {
            // Populate UI with book details
        }
        val buttonRead: Button = findViewById(R.id.btn_read)
        val buttonLike: Button = findViewById(R.id.btn_like)
        val buttonDownload: Button = findViewById(R.id.btn_download)

        val buttonBack: Button = findViewById(R.id.btn_back)
        buttonBack.setOnClickListener {
            val intent = Intent(this, Home::class.java)
            startActivity(intent)
            finish()
        }
    }
}
