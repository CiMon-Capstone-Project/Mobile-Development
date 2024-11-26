package com.example.cimon_chilimonitoring.ui.chatbot

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.cimon_chilimonitoring.R
import com.example.cimon_chilimonitoring.helper.chatbotUtils.Constants.RECEIVE_ID
import com.example.cimon_chilimonitoring.helper.chatbotUtils.Constants.SEND_ID

class MessagingAdapter: RecyclerView.Adapter<MessagingAdapter.MessageViewHolder>() {
    private val messages: MutableList<Message> = mutableListOf()

    var messagesList = mutableListOf<Message>()

    inner class MessageViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        init {
            itemView.setOnClickListener {

                //Remove message on the item clicked
                messagesList.removeAt(adapterPosition)
                notifyItemRemoved(adapterPosition)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MessageViewHolder {
        return MessageViewHolder(
            LayoutInflater.from(parent.context).inflate(R.layout.item_chat_message_block, parent, false)
        )
    }

    override fun getItemCount(): Int {
        return messagesList.size
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: MessageViewHolder, position: Int) {
        val currentMessage = messagesList[position]
        val tv_message = holder.itemView.findViewById<TextView>(R.id.tv_message)
        val tv_bot_message = holder.itemView.findViewById<TextView>(R.id.tv_bot_message)

        when (currentMessage.id) {
            SEND_ID -> {
                tv_message.apply {
                    text = currentMessage.message
                    visibility = View.VISIBLE
                }
                tv_bot_message.visibility = View.GONE
            }
            RECEIVE_ID -> {
                tv_bot_message.apply {
                    text = currentMessage.message
                    visibility = View.VISIBLE
                }
                tv_message.visibility = View.GONE
            }
        }
    }

    fun addMessage(message: Message) {
        messages.add(message)
        notifyItemInserted(messages.size - 1)
    }
}