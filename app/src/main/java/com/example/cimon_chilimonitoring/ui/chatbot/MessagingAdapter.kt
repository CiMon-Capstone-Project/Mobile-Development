package com.example.cimon_chilimonitoring.ui.chatbot

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.cimon_chilimonitoring.R
import com.example.cimon_chilimonitoring.helper.chatbotUtils.Constants.LOADING_ID
import com.example.cimon_chilimonitoring.helper.chatbotUtils.Constants.RECEIVE_ID
import com.example.cimon_chilimonitoring.helper.chatbotUtils.Constants.SEND_ID

class MessagingAdapter: RecyclerView.Adapter<MessagingAdapter.MessageViewHolder>() {
    var messagesList = mutableListOf<Message>()

    inner class MessageViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val tvMessage: TextView = itemView.findViewById(R.id.tv_message)
        private val tvBotMessage: TextView = itemView.findViewById(R.id.tv_bot_message)

        fun bind(message: Message) {
            when (message.id) {
                SEND_ID -> {
                    tvMessage.text = message.message
                    tvMessage.visibility = View.VISIBLE
                    tvBotMessage.visibility = View.GONE
                }
                RECEIVE_ID -> {
                    tvBotMessage.text = message.message
                    tvBotMessage.visibility = View.VISIBLE
                    tvMessage.visibility = View.GONE
                }
                LOADING_ID -> {
                    tvMessage.text = message.message
                    tvMessage.visibility = View.VISIBLE
                    tvBotMessage.visibility = View.GONE
                }
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
        holder.bind(currentMessage)
    }

    fun addMessage(message: Message) {
        messagesList.add(message)
        notifyItemInserted(messagesList.size - 1)
    }
}