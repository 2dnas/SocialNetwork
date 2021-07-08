package com.example.socialnetwork.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.RequestManager
import com.example.socialnetwork.R
import com.example.socialnetwork.data.model.User
import com.google.firebase.auth.FirebaseAuth
import kotlinx.android.synthetic.main.item_user.view.*
import javax.inject.Inject

class UserAdapter @Inject constructor(
    private val glide : RequestManager,
    private val auth: FirebaseAuth
) : RecyclerView.Adapter<UserAdapter.UserViewHolder>(){

    var Users:  List<User>
        get() = differ.currentList
        set(value) = differ.submitList(value)

     inner class UserViewHolder(itemView : View) : RecyclerView.ViewHolder(itemView) {
         val ivProfilePicture : ImageView = itemView.ivProfileImageItemUser
         val tvUsername : TextView = itemView.tvUsernameItemUser

        fun bind(user : User) {
            glide.load(user.profilePictureUrl).into(ivProfilePicture)
            tvUsername.text = user.username
            itemView.setOnClickListener {
                onUserClickListener?.invoke(user)
            }
        }
    }

    private val diffCallback = object : DiffUtil.ItemCallback<User>() {
        override fun areItemsTheSame(oldItem: User, newItem: User): Boolean {
            return oldItem.uid == newItem.uid
        }

        override fun areContentsTheSame(oldItem: User, newItem: User): Boolean {
            return oldItem.hashCode() == newItem.hashCode()
        }
    }

    private val differ = AsyncListDiffer(this,diffCallback)


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UserViewHolder {
        return UserViewHolder(
            LayoutInflater.from(parent.context).inflate(
            R.layout.item_user,
            parent,
            false
        ))
    }

    override fun onBindViewHolder(holder: UserViewHolder, position: Int) {
        val user = Users[position]
        holder.bind(user)
    }

    override fun getItemCount() = Users.size

    private var onUserClickListener : ((User) -> Unit)? = null

    fun setOnUserClickListener(listener : (User) -> Unit) {
        onUserClickListener = listener
    }



}