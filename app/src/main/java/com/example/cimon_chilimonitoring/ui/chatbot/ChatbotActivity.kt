package com.example.cimon_chilimonitoring.ui.chatbot

import android.content.SharedPreferences
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
import com.google.gson.Gson
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class ChatbotActivity : AppCompatActivity() {
    private lateinit var binding: ActivityChatbotBinding
    private lateinit var chatAdapter: MessagingAdapter
    private val LOADING_MESSAGE = "..."

    private lateinit var sharedPreferences: SharedPreferences
    private val MESSAGES_KEY = "messages_key"

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
        sharedPreferences = getSharedPreferences("chatbot_prefs", MODE_PRIVATE)

        binding.btnSend.setOnClickListener {
            val prompt = binding.edQuestion.text.toString()
            val message = Message(prompt, SEND_ID)

            viewModel.addMessage(message)
            saveMessagesToPreferences()
            binding.edQuestion.text?.clear()
            binding.edQuestion.text?.clear()
            binding.recyclerView.scrollToPosition(chatAdapter.itemCount - 1)

            lifecycleScope.launch {
                delay(400)
                val loadingMessage = Message(LOADING_MESSAGE, LOADING_ID)
                viewModel.addMessage(loadingMessage)
                viewModel.sendMessage(prompt)
            }
        }

        binding.topAppBar.setNavigationOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }

        observeViewModel()
        setupRecyclerView()
        restoreMessages()
    }

    private fun restoreMessages() {
        val savedMessages = sharedPreferences.getString(MESSAGES_KEY, null)
        if (savedMessages != null) {
            val messages = Gson().fromJson(savedMessages, Array<Message>::class.java).toMutableList()
            viewModel.setMessagesList(messages)
        }
        viewModel.messagesList.observe(this) { messages ->
            chatAdapter.messagesList = messages
            chatAdapter.notifyDataSetChanged()
            binding.recyclerView.scrollToPosition(chatAdapter.itemCount - 1)
        }
    }

    private fun saveMessagesToPreferences() {
        val messagesJson = Gson().toJson(viewModel.messagesList.value)
        sharedPreferences.edit().putString(MESSAGES_KEY, messagesJson).apply()
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
            if (viewModel.initialMessageSent.value == true) {
                val loadingIndex = chatAdapter.messagesList.indexOfFirst { it.id == LOADING_ID }
                if (loadingIndex != -1) {
                    chatAdapter.messagesList.removeAt(loadingIndex)
                    chatAdapter.notifyItemRemoved(loadingIndex)
                }
                val message = Message(response, RECEIVE_ID)
                viewModel.addMessage(message)
                saveMessagesToPreferences()
                binding.recyclerView.scrollToPosition(chatAdapter.itemCount - 1)
            }
        }
        viewModel.initialMessageSent.observe(this) { initialMessageSent ->
            if (!initialMessageSent) {
                viewModel.sendInitialMessage()
            }
        }
    }

    override fun onStop() {
        super.onStop()
        if (!isFinishing) {
            sharedPreferences.edit().clear().apply()
            Log.d("ChatbotActivityqwe", "onStop: Cleared shared preferences")
        }
    }

//    private fun sendInitialMessage() {
//        val initialPrompt = "Halo, bisa saya bertanya sesuatu?"
//        val message = Message(initialPrompt, SEND_ID)
//        viewModel.addMessage(message)
//        binding.recyclerView.scrollToPosition(chatAdapter.itemCount - 1)
//
//        val loadingMessage = Message(LOADING_MESSAGE, LOADING_ID)
//        viewModel.addMessage(loadingMessage)
//        viewModel.sendMessage(initialPrompt)
//    }
}