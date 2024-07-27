package com.example.readify.activities

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.view.View
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.ProgressBar
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.readify.R
import com.example.readify.adapters.LikeAdapter
import com.example.readify.data.Book
import com.example.readify.extensions.Extensions.toast
import com.example.readify.utils.FirebaseUtils.firebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference

class Profile : AppCompatActivity(), LikeAdapter.OnItemClickListener {

    private lateinit var db: FirebaseFirestore
    private lateinit var textName: TextView
    private lateinit var ivProfilePic: ImageView
    private lateinit var textEmail: TextView
    private lateinit var textDate: TextView
    private lateinit var likedBooksRecyclerView: RecyclerView
    private lateinit var bookAdapter: LikeAdapter
    private lateinit var progressBar: ProgressBar
    private lateinit var profileContent: LinearLayout
    private lateinit var profilePicUploadProgress: ProgressBar
    private val likedBooksList = mutableListOf<Book>()

    private val PICK_IMAGE_REQUEST = 71
    private lateinit var storageReference: StorageReference

    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile)

        textName = findViewById(R.id.txt_name)
        textEmail = findViewById(R.id.txt_email)
        textDate = findViewById(R.id.txt_date)
        ivProfilePic = findViewById(R.id.iv_profile_pic)
        likedBooksRecyclerView = findViewById(R.id.liked_books_recycler_view)
        progressBar = findViewById(R.id.progress_bar)
        profileContent = findViewById(R.id.profile_content)
        profilePicUploadProgress = findViewById(R.id.profile_pic_upload_progress)

        db = FirebaseFirestore.getInstance()
        storageReference = FirebaseStorage.getInstance().reference

        val buttonBack: ImageView = findViewById(R.id.iv_back)
        buttonBack.setOnClickListener {
            val intent = Intent(this, Home::class.java)
            startActivity(intent)
            finish()
        }

        ivProfilePic.setOnClickListener {
            val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
            startActivityForResult(intent, PICK_IMAGE_REQUEST)
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

    private fun loadProfilePicture(userId: String) {
        val profilePicRef = storageReference.child("profile_pics/$userId.jpg")
        profilePicRef.downloadUrl.addOnSuccessListener { uri ->
            Glide.with(this).load(uri).into(ivProfilePic)
        }.addOnFailureListener {
            // Handle the case where the profile picture is not available
            ivProfilePic.setImageResource(androidx.core.R.drawable.ic_call_answer)
        }
    }

    private fun uploadProfilePicture(uri: Uri, userId: String) {
        val profilePicRef = storageReference.child("profile_pics/$userId.jpg")
        profilePicUploadProgress.visibility = View.VISIBLE  // Show loader
        profilePicRef.putFile(uri).addOnSuccessListener {
            profilePicRef.downloadUrl.addOnSuccessListener { downloadUri ->
                Glide.with(this).load(downloadUri).into(ivProfilePic)
                toast("Profile picture updated successfully")
                profilePicUploadProgress.visibility = View.GONE  // Hide loader
            }
        }.addOnFailureListener {
            toast("Failed to upload profile picture")
            profilePicUploadProgress.visibility = View.GONE  // Hide loader
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == Activity.RESULT_OK && data != null && data.data != null) {
            val uri = data.data
            val currentUser = firebaseAuth.currentUser
            currentUser?.let {
                if (uri != null) {
                    uploadProfilePicture(uri, it.uid)
                }
            }
        }
    }
}
