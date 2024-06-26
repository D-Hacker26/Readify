package com.example.readify.activities

import android.content.ContentResolver
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.pdf.PdfRenderer
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.*
import androidx.appcompat.widget.AppCompatButton
import androidx.appcompat.widget.AppCompatEditText
import androidx.appcompat.widget.AppCompatTextView
import com.example.readify.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import java.io.File
import java.io.FileOutputStream
import java.text.SimpleDateFormat
import java.util.*

class AddNewBook : AppCompatActivity() {

    private lateinit var firestore: FirebaseFirestore
    private lateinit var auth: FirebaseAuth
    private lateinit var spinner: Spinner
    private lateinit var editTextBookTitle: AppCompatEditText
    private lateinit var textViewUploadedFile: AppCompatTextView
    private lateinit var buttonBrowseFile: AppCompatButton
    private lateinit var editTextBookDescription: AppCompatEditText
    private lateinit var buttonUpload: AppCompatButton

    companion object {
        private const val REQUEST_CODE_SELECT_PDF = 1001
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_new_book)

        firestore = FirebaseFirestore.getInstance()
        auth = FirebaseAuth.getInstance()

        editTextBookTitle = findViewById(R.id.et_book_title)
        editTextBookDescription = findViewById(R.id.et_book_description)
        buttonUpload = findViewById(R.id.btn_upload)
        textViewUploadedFile = findViewById(R.id.tv_uploaded_file)
        buttonBrowseFile = findViewById(R.id.btn_browse_file)
        spinner = findViewById(R.id.my_spinner)
        val buttonBack: AppCompatButton = findViewById(R.id.btn_back)

        fetchCategories()

//        buttonUpload.setOnClickListener {
//            val fileUrl = textViewUploadedFile.text.toString()
//         //   uploadBook(fileUrl)
//        }
        buttonBrowseFile.setOnClickListener {
            val intent = Intent(Intent.ACTION_GET_CONTENT)
            intent.type = "application/pdf"
            startActivityForResult(intent, REQUEST_CODE_SELECT_PDF)
        }

        ArrayAdapter.createFromResource(
            this,
            R.array.my_spinner_array,
            android.R.layout.simple_spinner_item
        ).also { adapter ->
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            spinner.adapter = adapter
        }

        buttonBack.setOnClickListener {
            val intent = Intent(this, Home::class.java)
            startActivity(intent)
            finish()
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_CODE_SELECT_PDF && resultCode == RESULT_OK) {
            data?.data?.let { uri ->
                val fileSize = uri.getFileSize(contentResolver)
                Log.d(
                    "FileSize",
                    "File size fetched: $fileSize bytes"
                )  // Detailed log for file size
                val currentDate = SimpleDateFormat("dd/MM/yy", Locale.getDefault()).format(Date())
                Log.d("CurrentDate", "Current date fetched: $currentDate")  // Detailed log for date
                uploadFileToFirebase(uri, fileSize, currentDate)
            }
        }
    }


    // Upload PDF file to Firebase Storage and generate/upload thumbnail
    private fun uploadFileToFirebase(fileUri: Uri, fileSize: Long, uploadDate: String) {
        val storageReference = FirebaseStorage.getInstance().reference
        val fileReference = storageReference.child("uploads/${System.currentTimeMillis()}.pdf")

        fileReference.putFile(fileUri)
            .addOnSuccessListener {
                fileReference.downloadUrl.addOnSuccessListener { uri ->
                    val thumbnailUri = generateThumbnail(fileUri)
                    if (thumbnailUri != null) {
                        uploadThumbnailToFirebase(thumbnailUri) { thumbnailUrl ->
                            textViewUploadedFile.text = uri.toString()
                            uploadBook(uri.toString(), fileSize, uploadDate, thumbnailUrl)
                        }
                    } else {
                        uploadBook(uri.toString(), fileSize, uploadDate, "")
                    }
                }
            }
            .addOnFailureListener { e ->
                Toast.makeText(this, "Failed to upload file: ${e.message}", Toast.LENGTH_LONG)
                    .show()
            }
    }

    // Upload thumbnail to Firebase Storage
    private fun uploadThumbnailToFirebase(thumbnailUri: Uri, onComplete: (String) -> Unit) {
        val storageReference = FirebaseStorage.getInstance().reference
        val thumbnailReference =
            storageReference.child("thumbnails/${System.currentTimeMillis()}.jpg")

        thumbnailReference.putFile(thumbnailUri)
            .addOnSuccessListener {
                thumbnailReference.downloadUrl.addOnSuccessListener { uri ->
                    onComplete(uri.toString())
                }
            }
            .addOnFailureListener { e ->
                Log.e("ThumbnailUploadError", "Failed to upload thumbnail: ${e.message}")
                onComplete("")
            }
    }

    private fun generateThumbnail(pdfUri: Uri): Uri? {
        return try {
            val fileDescriptor = contentResolver.openFileDescriptor(pdfUri, "r")
            val pdfRenderer = PdfRenderer(fileDescriptor!!)

            // Open the first page of the PDF
            val page = pdfRenderer.openPage(0)
            val width = page.width
            val height = page.height
            val bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)

            // Render the page to the bitmap
            page.render(bitmap, null, null, PdfRenderer.Page.RENDER_MODE_FOR_DISPLAY)
            page.close()
            pdfRenderer.close()

            // Save the bitmap to a file
            val file = File(cacheDir, "thumbnail_${System.currentTimeMillis()}.jpg")
            val outputStream = FileOutputStream(file)
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, outputStream)
            outputStream.close()

            Uri.fromFile(file)
        } catch (e: Exception) {
            Log.e("ThumbnailError", "Error generating thumbnail: ${e.message}")
            null
        }
    }


    private fun Uri.getFileSize(contentResolver: ContentResolver): Long {
        return try {
            contentResolver.openFileDescriptor(this, "r")?.use {
                it.statSize
            } ?: 0L
        } catch (e: Exception) {
            Log.e("FileSizeError", "Error fetching file size: ${e.message}")
            0L
        }
    }

    private fun uploadBook(
        fileUrl: String,
        fileSize: Long,
        uploadDate: String,
        thumbnailUrl: String
    ) {
        val currentUser = auth.currentUser
        if (currentUser != null) {
            val userId = currentUser.uid
            val bookTitle = editTextBookTitle.text.toString().trim()
            val bookDescription = editTextBookDescription.text.toString().trim()
            val selectedCategory = spinner.selectedItem.toString()

            if (bookTitle.isNotEmpty() && bookDescription.isNotEmpty() && selectedCategory.isNotEmpty() && fileUrl.isNotEmpty()) {
                val bookData = hashMapOf(
                    "title" to bookTitle,
                    "description" to bookDescription,
                    "category" to selectedCategory,
                    "userId" to userId,
                    "fileUrl" to fileUrl,
                    "fileSize" to fileSize,
                    "uploadDate" to uploadDate,
                    "thumbnailUrl" to thumbnailUrl  // Add this line
                )

                firestore.collection("books")
                    .add(bookData)
                    .addOnSuccessListener {
                        Toast.makeText(this, "Book uploaded successfully", Toast.LENGTH_LONG).show()
                        clearFields()
                    }
                    .addOnFailureListener { e ->
                        Toast.makeText(
                            this,
                            "Failed to upload book: ${e.message}",
                            Toast.LENGTH_LONG
                        ).show()
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
        textViewUploadedFile.text = ""
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
