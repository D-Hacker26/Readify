package com.example.readify.activities

import android.app.DownloadManager
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Environment
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.appcompat.widget.AppCompatImageView
import com.bumptech.glide.Glide
import com.example.readify.R
import com.example.readify.data.Book
import android.Manifest
import android.content.pm.PackageManager
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat

class BookDetails : AppCompatActivity() {
    private var book: Book? = null

    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_book_details)
        val title: TextView = findViewById(R.id.titleTv)
        val size: TextView = findViewById(R.id.sizeTv)
        val date: TextView = findViewById(R.id.date)
        val category: TextView = findViewById(R.id.categoryTv)
        val thumbnail: ImageView = findViewById(R.id.iv_thumbnail)

        book = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            intent.getParcelableExtra("book", Book::class.java)
        } else {
            @Suppress("DEPRECATION")
            intent.getParcelableExtra("book")
        }
        // Use the book object
        if (book != null) {
            // Populate UI with book details
            title.text = book!!.title
            //description.text = book.description
            category.text = book!!.category
            size.text = "${book!!.fileSize / 1024} KB"
            date.text = book!!.uploadDate

            Glide.with(this)
                .load(book!!.thumbnailUrl)
                .placeholder(R.drawable.goole_logo)  // Add a placeholder image
                .into(thumbnail)
        }
        val buttonRead: Button = findViewById(R.id.btn_read)
        val buttonLike: Button = findViewById(R.id.btn_like)
        val buttonDownload: Button = findViewById(R.id.btn_download)

        buttonDownload.setOnClickListener {
            if (checkPermission()) {
                book?.let { downloadBook(it) }
            } else {
                requestPermission()
            }
        }


        val buttonBack: AppCompatImageView = findViewById(R.id.btn_back)
        buttonBack.setOnClickListener {
            val intent = Intent(this, Home::class.java)
            startActivity(intent)
            finish()
        }
    }

    private fun downloadBook(book: Book) {
        val request = DownloadManager.Request(Uri.parse(book.fileUrl))  // Assuming `pdfUrl` is a property of Book
            .setTitle(book.title)
            .setDescription("Downloading ${book.title}")
            .setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED)
            .setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, "${book.title}.pdf")
            .setAllowedOverMetered(true)
            .setAllowedOverRoaming(true)

        val downloadManager = getSystemService(Context.DOWNLOAD_SERVICE) as DownloadManager
        downloadManager.enqueue(request)
    }



    private fun checkPermission(): Boolean {
        val permission = ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
        return permission == PackageManager.PERMISSION_GRANTED
    }

    private fun requestPermission() {
        ActivityCompat.requestPermissions(
            this,
            arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE),
            1
        )
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == 1 && grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            // Permission granted
            book?.let { downloadBook(it) }
        } else {
            // Permission denied
            Toast.makeText(this, "Permission denied to write to storage", Toast.LENGTH_SHORT).show()
        }
    }

}
