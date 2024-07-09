package com.example.readify.activities

import android.app.DownloadManager
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Environment
import androidx.annotation.RequiresApi
import androidx.appcompat.widget.AppCompatImageView
import com.bumptech.glide.Glide
import com.example.readify.R
import com.example.readify.data.Book
import android.Manifest
import android.app.AlertDialog
import android.content.pm.PackageManager
import android.view.View
import android.widget.*
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.readify.adapters.CommentAdapter
import com.example.readify.data.Comment
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class BookDetails : AppCompatActivity() {
    private var book: Book? = null

    private lateinit var auth: FirebaseAuth
    private lateinit var firestore: FirebaseFirestore
    private lateinit var commentAdapter: CommentAdapter
    private val commentList = mutableListOf<Comment>()
    private lateinit var commentRecyclerView: RecyclerView
    private lateinit var addCommentButton: ImageButton

    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_book_details)



        val title: TextView = findViewById(R.id.titleTv)
        val size: TextView = findViewById(R.id.sizeTv)
        val date: TextView = findViewById(R.id.date)
        val category: TextView = findViewById(R.id.categoryTv)
        val thumbnail: ImageView = findViewById(R.id.iv_thumbnail)

        auth = FirebaseAuth.getInstance()
        firestore = FirebaseFirestore.getInstance()

        commentRecyclerView = findViewById(R.id.comment_list)
        addCommentButton = findViewById(R.id.ib_click_comment)

        commentAdapter = CommentAdapter(commentList)
        commentRecyclerView.layoutManager = LinearLayoutManager(this)
        commentRecyclerView.adapter = commentAdapter

        addCommentButton.setOnClickListener {
          //  addComment()
            showAddCommentDialog()
        }

        book?.let { fetchComments(it) }

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
                .placeholder(R.drawable.baseline_insert_drive_file_24)  // Add a placeholder image
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

        val fromProfile = intent.getBooleanExtra("fromProfile", false)
        // Conditionally hide the like button
        if (fromProfile) {
            buttonLike.visibility = View.GONE
        }

        buttonLike.setOnClickListener {
            book?.let { likeBook(it) }
        }

        val buttonBack: AppCompatImageView = findViewById(R.id.btn_back)
        buttonBack.setOnClickListener {
            val intent = Intent(this, Home::class.java)
            startActivity(intent)
            finish()
        }
    }

    private fun showAddCommentDialog() {
        val dialogView = layoutInflater.inflate(R.layout.comment_popup, null)
        val dialogBuilder = AlertDialog.Builder(this)
            .setView(dialogView)
            .setCancelable(true)

        val alertDialog = dialogBuilder.create()
        alertDialog.show()

        val commentEditText = dialogView.findViewById<EditText>(R.id.et_comment)
        val submitButton = dialogView.findViewById<Button>(R.id.btn_submit_comment)

        submitButton.setOnClickListener {
            val commentText = commentEditText.text.toString()
            if (commentText.isNotEmpty()) {
                addComment(commentText)
                alertDialog.dismiss()
            } else {
                Toast.makeText(this, "Comment cannot be empty", Toast.LENGTH_SHORT).show()
            }
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

    private fun addComment(commentText: String) {

        val currentUser = auth.currentUser
       // val commentText = commentEditText.text.toString()

        if (currentUser != null && commentText.isNotEmpty()) {
            val comment = Comment(
                userId = currentUser.uid,
                userName = currentUser.displayName ?: "Anonymous",
                text = commentText
            )

            book?.let { book ->
                firestore.collection("books").document(book.id)
                    .collection("comments")
                    .add(comment)
                    .addOnSuccessListener {
                        commentList.add(comment)
                        commentAdapter.notifyDataSetChanged()
                    }
                    .addOnFailureListener { e ->
                        Toast.makeText(this, "Failed to add comment: ${e.message}", Toast.LENGTH_LONG).show()
                    }
            }
        }
    }

    private fun fetchComments(book: Book) {
        firestore.collection("books").document(book.id)
            .collection("comments")
            .orderBy("timestamp")
            .get()
            .addOnSuccessListener { documents ->
                commentList.clear()
                for (document in documents) {
                    val comment = document.toObject(Comment::class.java)
                    commentList.add(comment)
                }
                commentAdapter.notifyDataSetChanged()
            }
            .addOnFailureListener { e ->
                Toast.makeText(this, "Failed to fetch comments: ${e.message}", Toast.LENGTH_LONG).show()
            }
    }

    private fun likeBook(book: Book) {
        val currentUser = auth.currentUser
        if (currentUser != null) {
            firestore.collection("users").document(currentUser.uid)
                .collection("likedBooks")
                .document(book.id)
                .set(book)
                .addOnSuccessListener {
                    Toast.makeText(this, "Book liked", Toast.LENGTH_SHORT).show()
                }
                .addOnFailureListener { e ->
                    Toast.makeText(this, "Failed to like book: ${e.message}", Toast.LENGTH_LONG).show()
                }
        }
    }

    override fun onResume() {
        super.onResume()
        book?.let { fetchComments(it) }
    }

}
