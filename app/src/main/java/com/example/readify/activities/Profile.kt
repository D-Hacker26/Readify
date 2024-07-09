package com.example.readify.activities

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.ProgressBar
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.readify.R
import com.example.readify.adapters.LikeAdapter
import com.example.readify.data.Book
import com.example.readify.extensions.Extensions.toast
import com.example.readify.utils.FirebaseUtils.firebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class Profile : AppCompatActivity(), LikeAdapter.OnItemClickListener {

    private lateinit var db: FirebaseFirestore
    private lateinit var textName: TextView
    private lateinit var textEmail: TextView
    private lateinit var textDate: TextView
    private lateinit var likedBooksRecyclerView: RecyclerView
    private lateinit var bookAdapter: LikeAdapter
    private lateinit var progressBar: ProgressBar
    private lateinit var profileContent: LinearLayout
    private val likedBooksList = mutableListOf<Book>()

    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile)

        textName = findViewById(R.id.txt_name)
        textEmail = findViewById(R.id.txt_email)
        textDate = findViewById(R.id.txt_date)
        likedBooksRecyclerView = findViewById(R.id.liked_books_recycler_view)
        progressBar = findViewById(R.id.progress_bar)
        profileContent = findViewById(R.id.profile_content)

        db = FirebaseFirestore.getInstance()

        val buttonBack: ImageView = findViewById(R.id.iv_back)
        buttonBack.setOnClickListener {
            val intent = Intent(this, Home::class.java)
            startActivity(intent)
            finish()
        }

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
                }.addOnCompleteListener {
                    progressBar.visibility = View.GONE
                    profileContent.visibility = View.VISIBLE
                }
            }

            bookAdapter = LikeAdapter(likedBooksList, this)
            likedBooksRecyclerView.layoutManager = LinearLayoutManager(this)
            likedBooksRecyclerView.adapter = bookAdapter

            fetchLikedBooks(it.uid)
        }

        val editProfileButton: ImageView = findViewById(R.id.iv_edit)
        editProfileButton.setOnClickListener {
            startActivity(Intent(this, EditProfile::class.java))
        }
    }

    private fun fetchLikedBooks(userId: String) {
        db.collection("users").document(userId)
            .collection("likedBooks")
            .get()
            .addOnSuccessListener { documents ->
                likedBooksList.clear()
                for (document in documents) {
                    val book = document.toObject(Book::class.java)
                    likedBooksList.add(book)
                }
                bookAdapter.notifyDataSetChanged()
            }
            .addOnFailureListener { e ->
                toast("Failed to fetch liked books: ${e.message}")
            }
    }

    override fun onItemClick(book: Book) {
        val intent = Intent(this, BookDetails::class.java)
        intent.putExtra("book", book)
        intent.putExtra("fromProfile", true)
        startActivity(intent)
    }
}
