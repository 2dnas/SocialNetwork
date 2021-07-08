package com.example.socialnetwork.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.RequestManager
import com.example.socialnetwork.R
import com.example.socialnetwork.data.model.Comment
import com.example.socialnetwork.data.model.User
import com.example.socialnetwork.utils.visible
import com.google.firebase.auth.FirebaseAuth
import kotlinx.android.synthetic.main.item_comment.view.*
import javax.inject.Inject

class CommentAdapter @Inject constructor(
    private val glide : RequestManager,
    private val auth: FirebaseAuth
) : RecyclerView.Adapter<CommentAdapter.CommentViewHolder>(){

    var comments:  List<Comment>
        get() = differ.currentList
        set(value) = differ.submitList(value)

     inner class CommentViewHolder(itemView : View) : RecyclerView.ViewHolder(itemView) {
         private val tvCommentUsername : TextView = itemView.tvCommentUsername
         val tvComment : TextView = itemView.tvComment
         private val ibDeleteComment : ImageButton = itemView.ibDeleteComment
         private val ivCommentUserProfilePicture:ImageView = itemView.ivCommentUserProfilePicture

         fun bind(comment: Comment) {
             glide.load(comment.profilePictureUrl).into(ivCommentUserProfilePicture)
             ibDeleteComment.visible(comment.uid == auth.uid)
             tvComment.text = comment.comment
             tvCommentUsername.text = comment.username
             tvCommentUsername.setOnClickListener{
                 onUserClickListener?.invoke(comment)
             }
             ibDeleteComment.setOnClickListener {
                 onDeleteCommentClickListener?.let { click ->
                     click(comment)
                 }
             }
         }

    }

    private val diffCallback = object : DiffUtil.ItemCallback<Comment>() {
        override fun areItemsTheSame(oldItem: Comment, newItem: Comment): Boolean {
            return oldItem.commentId == newItem.commentId
        }

        override fun areContentsTheSame(oldItem: Comment, newItem: Comment): Boolean {
            return oldItem.hashCode() == newItem.hashCode()
        }
    }

    private val differ = AsyncListDiffer(this,diffCallback)


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CommentViewHolder {
        return CommentViewHolder(
            LayoutInflater.from(parent.context).inflate(
            R.layout.item_comment,
            parent,
            false
        ))
    }

    override fun onBindViewHolder(holder: CommentViewHolder, position: Int) {
        val comment = comments[position]
        holder.bind(comment)
    }

    override fun getItemCount() = comments.size

    private var onUserClickListener : ((Comment) -> Unit)? = null
    private var onDeleteCommentClickListener : ((Comment) -> Unit)? = null

    fun setOnUserClickListener(listener : (Comment) -> Unit) {
        onUserClickListener = listener
    }

    fun setOnDeleteCommentClickListener(listener : (Comment) -> Unit) {
        onDeleteCommentClickListener = listener
    }



}