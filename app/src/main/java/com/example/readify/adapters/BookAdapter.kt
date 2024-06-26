package com.example.readify.adapters

import android.annotation.SuppressLint
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.readify.R
import com.example.readify.data.Book

class BookAdapter(
    private val books: List<Book>,
    private val itemClickListener: OnItemClickListener
) : RecyclerView.Adapter<BookAdapter.BookViewHolder>() {

    interface OnItemClickListener {
        fun onItemClick(book: Book)
    }

    class BookViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val title: TextView = itemView.findViewById(R.id.txt_list_head)
        val description: TextView = itemView.findViewById(R.id.txt_list_subhead)
        val size: TextView = itemView.findViewById(R.id.txt_list_size)
        val date: TextView = itemView.findViewById(R.id.txt_list_date)
        val category: TextView = itemView.findViewById(R.id.txt_list_category)
        val thumbnail: ImageView = itemView.findViewById(R.id.img_list)

        fun bind(book: Book, clickListener: OnItemClickListener) {
            itemView.setOnClickListener { clickListener.onItemClick(book) }
        }
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

        // Load the thumbnail using Glide
        Glide.with(holder.itemView.context)
            .load(book.thumbnailUrl)
            .placeholder(R.drawable.goole_logo)  // Add a placeholder image
            .into(holder.thumbnail)

        holder.bind(book, itemClickListener)
    }

    override fun getItemCount() = books.size
}
