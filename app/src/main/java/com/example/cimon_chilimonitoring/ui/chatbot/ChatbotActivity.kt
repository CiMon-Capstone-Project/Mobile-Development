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
import com.example.cimon_chilimonitoring.helper.chatbotUtils.Constants.RECEIVE_ID
import com.example.cimon_chilimonitoring.helper.chatbotUtils.Constants.SEND_ID
import com.google.ai.client.generativeai.Chat
import com.google.ai.client.generativeai.GenerativeModel
import com.google.ai.client.generativeai.type.content
import kotlinx.coroutines.launch

class ChatbotActivity : AppCompatActivity() {
    private lateinit var binding: ActivityChatbotBinding
    private lateinit var chatAdapter: MessagingAdapter

    lateinit var chat : Chat
    //viewmodel
    private lateinit var viewModel: ChatbotViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        binding = ActivityChatbotBinding.inflate(layoutInflater)
        setContentView(binding.root)

        viewModel = ViewModelProvider(this).get(ChatbotViewModel::class.java)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        binding.btnSend.setOnClickListener{
            val prompt = binding.edQuestion.text.toString()

            viewModel.sendMessage(prompt)
            binding.edQuestion.text?.clear()
        }

//        observeViewModel()
        setupRecyclerView()
    }

    private fun setupRecyclerView() {
        chatAdapter = MessagingAdapter()
        binding.recyclerView.apply {
            adapter = chatAdapter
            layoutManager = LinearLayoutManager(this@ChatbotActivity)
        }
    }

//    private fun observeViewModel() {
//        viewModel.generatedContent.observe(this) { response ->
//            chatAdapter.addMessage(message)
//            binding.recyclerView.scrollToPosition(chatAdapter.itemCount - 1)
//        }
//    }
}