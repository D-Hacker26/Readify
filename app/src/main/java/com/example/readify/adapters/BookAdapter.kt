package com.example.readify.adapters

import android.annotation.SuppressLint
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.readify.R
import com.example.readify.data.Book

class BookAdapter(private val books: List<Book>) : RecyclerView.Adapter<BookAdapter.BookViewHolder>() {

    class BookViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val title: TextView = itemView.findViewById(R.id.txt_list_head)
        val description: TextView = itemView.findViewById(R.id.txt_list_subhead)
        val size: TextView = itemView.findViewById(R.id.txt_list_size)
        val date: TextView = itemView.findViewById(R.id.txt_list_date)
        val category: TextView = itemView.findViewById(R.id.txt_list_category)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BookViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.list_item, parent, false)
        return BookViewHolder(view)
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: BookViewHolder, position: Int) {
        val book = books[position]
        holder.title.text = book.title
        holder.description.text = book.description
        holder.category.text = book.category
        holder.size.text = "${book.fileSize / 1024} KB"
        Log.d("BookSize", "Binding book size: ${holder.size.text}")
        holder.date.text = book.uploadDate
        Log.d("BookDate", "Binding book date: ${holder.date.text}")
    }




    override fun getItemCount() = books.size
}
