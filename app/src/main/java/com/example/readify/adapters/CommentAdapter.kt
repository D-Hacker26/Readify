package com.example.readify.adapters


import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.readify.R
import com.example.readify.data.Comment

class CommentAdapter(private val commentList: List<Comment>) :
    RecyclerView.Adapter<CommentAdapter.CommentViewHolder>() {

    class CommentViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val userName: TextView = view.findViewById(R.id.user_name)
        val commentText: TextView = view.findViewById(R.id.comment_text)
        val timestamp: TextView = view.findViewById(R.id.comment_timestamp)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CommentViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.comment_list_item, parent, false)
        return CommentViewHolder(view)
    }

    override fun onBindViewHolder(holder: CommentViewHolder, position: Int) {
        val comment = commentList[position]
        holder.userName.text = comment.userName
        holder.commentText.text = comment.text
        holder.timestamp.text = java.text.DateFormat.getDateTimeInstance().format(comment.timestamp)
    }

    override fun getItemCount(): Int {
        return commentList.size
    }
}
