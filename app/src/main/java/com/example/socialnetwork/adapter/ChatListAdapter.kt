package com.example.socialnetwork.adapter

import android.app.Application
import android.content.res.ColorStateList
import android.graphics.Color
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.example.socialnetwork.R
import com.example.socialnetwork.data.model.Message
import com.google.api.LabelDescriptor
import com.google.firebase.auth.FirebaseAuth
import javax.inject.Inject


class ChatListAdapter @Inject constructor(
    private val auth: FirebaseAuth,
    private val application : Application
) :
    RecyclerView.Adapter<ChatListAdapter.MessageViewHolder>() {

    var messages: MutableList<Message>
        get() = differ.currentList
        set(value) {
            differ.submitList(value)
            notifyDataSetChanged()
        }

    inner class MessageViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val tvChatMessage = itemView.findViewById<TextView>(R.id.tvChatItem)
        private val cardView = itemView.findViewById<CardView>(R.id.chatMessageCard)
        fun bind(message: Message) {
            if(message.sender != auth.uid!!){
                val layoutParams = FrameLayout.LayoutParams(
                    FrameLayout.LayoutParams.WRAP_CONTENT,
                    FrameLayout.LayoutParams.WRAP_CONTENT
                )
                layoutParams.gravity = Gravity.START
                layoutParams.bottomMargin = 8
                layoutParams.marginStart = 8
                layoutParams.topMargin = 8
                cardView.layoutParams = layoutParams
                cardView.setCardBackgroundColor(application.getColor(R.color.chatCardBackground))
            }
            tvChatMessage.text = message.message
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MessageViewHolder {
        return MessageViewHolder(
            LayoutInflater.from(parent.context).inflate(
                R.layout.item_chat_message,
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: MessageViewHolder, position: Int) {
        val message = messages[position]
        holder.bind(message)
    }

    override fun getItemCount() = messages.size


    private val diffCallback = object : DiffUtil.ItemCallback<Message>() {
        override fun areItemsTheSame(oldItem: Message, newItem: Message): Boolean {
            return oldItem == newItem
        }

        override fun areContentsTheSame(oldItem: Message, newItem: Message): Boolean {
            return oldItem.hashCode() == newItem.hashCode()
        }
    }

    private val differ = AsyncListDiffer(this, diffCallback)


}

