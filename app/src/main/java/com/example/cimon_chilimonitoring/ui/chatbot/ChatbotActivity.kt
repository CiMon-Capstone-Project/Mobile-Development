package com.example.cimon_chilimonitoring.ui.chatbot

import android.os.Bundle
import android.util.Log
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.cimon_chilimonitoring.BuildConfig
import com.example.cimon_chilimonitoring.R
import com.example.cimon_chilimonitoring.databinding.ActivityChatbotBinding
import com.example.cimon_chilimonitoring.helper.chatbotUtils.Constants.LOADING_ID
import com.example.cimon_chilimonitoring.helper.chatbotUtils.Constants.RECEIVE_ID
import com.example.cimon_chilimonitoring.helper.chatbotUtils.Constants.SEND_ID
import com.google.ai.client.generativeai.Chat
import com.google.ai.client.generativeai.GenerativeModel
import com.google.ai.client.generativeai.type.content
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class ChatbotActivity : AppCompatActivity() {
    private lateinit var binding: ActivityChatbotBinding
    private lateinit var chatAdapter: MessagingAdapter
    private val LOADING_MESSAGE = "..."

    //viewmodel
    private lateinit var viewModel: ChatbotViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        binding = ActivityChatbotBinding.inflate(layoutInflater)
        setContentView(binding.root)

        window.setSoftInputMode(android.view.WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN)

        viewModel = ViewModelProvider(this).get(ChatbotViewModel::class.java)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        binding.btnSend.setOnClickListener {
            val prompt = binding.edQuestion.text.toString()
            val message = Message(prompt, SEND_ID)

            chatAdapter.addMessage(message)
            binding.edQuestion.text?.clear()
            binding.recyclerView.scrollToPosition(chatAdapter.itemCount - 1)

            lifecycleScope.launch {
                delay(400)
                val loadingMessage = Message(LOADING_MESSAGE, LOADING_ID)
                chatAdapter.addMessage(loadingMessage)
                viewModel.sendMessage(prompt)
            }
        }

        binding.topAppBar.setNavigationOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }

        observeViewModel()
        setupRecyclerView()
        sendInitialMessage()
    }

    private fun setupRecyclerView() {
        chatAdapter = MessagingAdapter()
        binding.recyclerView.apply {
            adapter = chatAdapter
            layoutManager = LinearLayoutManager(this@ChatbotActivity)
        }
    }

    private fun observeViewModel() {
        viewModel.generatedContent.observe(this) { response ->
            val loadingIndex = chatAdapter.messagesList.indexOfFirst { it.id == LOADING_ID }
            if (loadingIndex != -1) {
                chatAdapter.messagesList.removeAt(loadingIndex)
                chatAdapter.notifyItemRemoved(loadingIndex)
            }
            val message = Message(response, RECEIVE_ID)
            chatAdapter.addMessage(message)
            binding.recyclerView.scrollToPosition(chatAdapter.itemCount - 1)
        }
    }

    private fun sendInitialMessage() {
        val initialPrompt = "Halo, bisa saya bertanya sesuatu?"
        val message = Message(initialPrompt, SEND_ID)
        chatAdapter.addMessage(message)
        binding.recyclerView.scrollToPosition(chatAdapter.itemCount - 1)

        val loadingMessage = Message(LOADING_MESSAGE, LOADING_ID)
        chatAdapter.addMessage(loadingMessage)
        viewModel.sendMessage(initialPrompt)
    }
}