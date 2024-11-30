package com.example.cimon_chilimonitoring.ui.chatbot

import android.util.Log
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
        apiKey =  BuildConfig.GEMINI_API_KEY,
    )

    private val _generatedContent = MutableLiveData<String>()

    val generatedContent: LiveData<String> get() = _generatedContent

    fun sendMessage(question: String) {
        viewModelScope.launch {
            try {
                val result = model.generateContent(question)
                val formattedResult = formatResponse(result.text ?: "")
                _generatedContent.postValue(formattedResult)
                Log.d("ChatbotViewModel", "Generated content: ${result.text}")
            } catch (e: Exception) {
                _generatedContent.postValue("Error: ${e.message}")
            }
        }
    }

    private fun formatResponse(response: String): String {
        return android.text.Html.fromHtml(
            response.replace("**", "<b>").replace("**", "</b>"),
            android.text.Html.FROM_HTML_MODE_LEGACY
        ).toString()
    }
}