package com.example.readify.activities

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.readify.R
import com.github.barteksc.pdfviewer.PDFView
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.BufferedInputStream
import java.io.IOException
import java.io.InputStream
import java.net.HttpURLConnection
import java.net.URL

class PdfViewerActivity : AppCompatActivity() {
    private lateinit var pdfView: PDFView

    @SuppressLint("SetJavaScriptEnabled")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_pdf_viewer)

        pdfView = findViewById(R.id.pdfView)

        val fileUrl = intent.getStringExtra("fileUrl")
        if (fileUrl != null) {
            retrievePDFStream(fileUrl)
        }
    }

    private fun retrievePDFStream(url: String) {
        CoroutineScope(Dispatchers.Main).launch {
            val inputStream = withContext(Dispatchers.IO) {
                downloadPdfStream(url)
            }
            inputStream?.let {
                pdfView.fromStream(it).load()
            }
        }
    }

    private fun downloadPdfStream(url: String): InputStream? {
        return try {
            val urlConnection = URL(url).openConnection() as HttpURLConnection
            if (urlConnection.responseCode == 200) {
                BufferedInputStream(urlConnection.inputStream)
            } else {
                null
            }
        } catch (e: IOException) {
            null
        }
    }
}
