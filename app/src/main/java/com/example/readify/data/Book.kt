package com.example.readify.data

data class Book(
    val title: String = "",
    val description: String = "",
    val category: String = "",
    val userId: String = "",
    val fileUrl: String = "",
    val fileSize: Long = 0L, // Change from size to fileSize
    val uploadDate: String = "", // Change from date to uploadDate
    val thumbnailUrl: String = ""
)
