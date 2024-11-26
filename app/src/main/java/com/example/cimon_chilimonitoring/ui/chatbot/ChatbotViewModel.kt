package com.example.cimon_chilimonitoring.ui.chatbot

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.cimon_chilimonitoring.BuildConfig
import com.google.ai.client.generativeai.GenerativeModel
import kotlinx.coroutines.launch

class ChatbotViewModel : ViewModel() {
    private val model: GenerativeModel = GenerativeModel(
        modelName = "gemini-pro",
        apiKey =  BuildConfig.GEMINI_API_KEY
    )

    private val _generatedContent = MutableLiveData<String>()

    val generatedContent: LiveData<String> get() = _generatedContent

    fun sendMessage(question: String) {
        viewModelScope.launch {
            try {
                val result = model.generateContent(question)
                _generatedContent.postValue(result.text)
            } catch (e: Exception) {
                _generatedContent.postValue("Error: ${e.message}")
            }
        }
    }
}