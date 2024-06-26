package com.example.readify.data

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Book(
    val title: String? = "",
    val description: String? = "",
    val category: String? = "",
    val userId: String? = "",
    val fileUrl: String? = "",
    val fileSize: Long = 0L,
    val uploadDate: String? = "",
    val thumbnailUrl: String? = ""
) : Parcelable
