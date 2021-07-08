package com.example.socialnetwork.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.core.view.isVisible
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.RequestManager
import com.example.socialnetwork.R
import com.example.socialnetwork.data.model.Post
import com.google.firebase.auth.FirebaseAuth
import kotlinx.android.synthetic.main.item_post.view.*
import javax.inject.Inject

class PostAdapter @Inject constructor(
    private val glide : RequestManager,
    private val auth: FirebaseAuth
) : RecyclerView.Adapter<PostAdapter.PostViewHolder>(){

    var posts:  List<Post>
        get() = differ.currentList
        set(value) = differ.submitList(value)

     inner class PostViewHolder(itemView : View) : RecyclerView.ViewHolder(itemView) {
        val ivPostImage : ImageView = itemView.ivItemPostImage
        val ivAuthorImage : ImageView = itemView.ivItemAuthorProfileImage
        val tvPostAuthor : TextView = itemView.tvItemPostAuthor
        val tvPostText : TextView = itemView.tvItemPostDescription
        val tvLikedBy : TextView = itemView.tvItemLikedBy
        val ibLike : ImageButton = itemView.ibItemLike
        val ibComments : ImageButton = itemView.ibItemComment
        val ibDelete : ImageView = itemView.ibDelete

        fun bind(post: Post) {
            glide.load(post.imageUrl).into(ivPostImage)
            glide.load(post.authorProfilePictureUrl).into(ivAuthorImage)
            tvPostAuthor.text = post.authorUsername
            tvPostText.text = post.text
            tvLikedBy.text = if(post.likedBy.size <= 0) "No Likes" else "Liked By ${post.likedBy.size} person"
            val uid = auth.uid
            ibDelete.isVisible = uid == post.authorId
            ibLike.setImageResource(
                if(post.isLiked) R.drawable.ic_like else R.drawable.ic_like_border
            )

            tvPostAuthor.setOnClickListener {
                onUserClickListener?.invoke(post.authorId)
            }
            ivAuthorImage.setOnClickListener {
                onUserClickListener?.invoke(post.authorId)
            }

            tvLikedBy.setOnClickListener {
                onLikedByClickListener?.invoke(post)
            }

            ibLike.setOnClickListener {
                if(!post.isLiking) onLikeClickListener?.invoke(post,adapterPosition)
            }
            ibDelete.setOnClickListener {
                onDeletePostClickListener?.invoke(post)
            }
            ibComments.setOnClickListener {
                onCommentsClickListener?.invoke(post)
            }
        }
    }

    private val diffCallback = object : DiffUtil.ItemCallback<Post>() {
        override fun areItemsTheSame(oldItem: Post, newItem: Post): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: Post, newItem: Post): Boolean {
            return oldItem.hashCode() == newItem.hashCode()
        }
    }

    private val differ = AsyncListDiffer(this,diffCallback)


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PostViewHolder {
        return PostViewHolder(
            LayoutInflater.from(parent.context).inflate(
            R.layout.item_post,
            parent,
            false
        ))
    }

    override fun onBindViewHolder(holder: PostViewHolder, position: Int) {
        val post = posts[position]
        holder.bind(post)
    }

    override fun getItemCount() = posts.size


    private var onLikeClickListener : ((Post,Int) -> Unit)? = null
    private var onUserClickListener : ((String) -> Unit)? = null
    private var onDeletePostClickListener : ((Post) -> Unit)? = null
    private var onLikedByClickListener : ((Post) -> Unit)? = null
    private var onCommentsClickListener : ((Post) -> Unit)? = null

    fun setOnLikeClickListener(listener : (Post,Int) -> Unit) {
        onLikeClickListener = listener
    }

    fun setOnUserClickListener(listener : (String) -> Unit) {
        onUserClickListener = listener
    }

    fun setOnDeleteClickListener(listener: (Post) -> Unit) {
        onDeletePostClickListener = listener
    }


    fun setOnLikedByClickListener(listener: (Post) -> Unit) {
        onLikedByClickListener = listener
    }

    fun setOnCommentsClickListener(listener : (Post) -> Unit) {
        onCommentsClickListener = listener
    }




}