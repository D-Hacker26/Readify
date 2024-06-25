package com.example.readify.activities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ImageView
import android.widget.TextView
import com.example.readify.R

class ListItem : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.list_item)

        val imageViewList: ImageView = findViewById(R.id.img_list)
        val textViewListHead: TextView = findViewById(R.id.txt_list_head)
        val textViewListSubhead: TextView = findViewById(R.id.txt_list_subhead)
        val textViewListCategory: TextView = findViewById(R.id.txt_list_category)
        val textViewListDate: TextView = findViewById(R.id.txt_list_date)
        val textViewListSize: TextView = findViewById(R.id.txt_list_size)



    }
}