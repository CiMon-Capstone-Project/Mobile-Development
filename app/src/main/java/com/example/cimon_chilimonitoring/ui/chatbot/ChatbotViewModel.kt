package com.example.cimon_chilimonitoring.ui.chatbot

import android.content.SharedPreferences
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.cimon_chilimonitoring.BuildConfig
import com.example.cimon_chilimonitoring.helper.chatbotUtils.Constants.LOADING_ID
import com.example.cimon_chilimonitoring.helper.chatbotUtils.Constants.RECEIVE_ID
import com.example.cimon_chilimonitoring.helper.chatbotUtils.Constants.SEND_ID
import com.google.ai.client.generativeai.GenerativeModel
import kotlinx.coroutines.launch

class ChatbotViewModel : ViewModel() {
    private val model: GenerativeModel = GenerativeModel(
        modelName = "gemini-pro",
        apiKey =  BuildConfig.GEMINI_API_KEY,
    )

    private val _generatedContent = MutableLiveData<String>()
    val generatedContent: LiveData<String> get() = _generatedContent

    private val _messagesList = MutableLiveData<MutableList<Message>>(mutableListOf())
    val messagesList: LiveData<MutableList<Message>> get() = _messagesList

    private val _initialMessageSent = MutableLiveData(false)
    val initialMessageSent: LiveData<Boolean> get() = _initialMessageSent
    private val LOADING_MESSAGE = "..."

    fun addMessage(message: Message) {
        _messagesList.value?.add(message)
        _messagesList.postValue(_messagesList.value)
    }
    fun setMessagesList(messages: MutableList<Message>) {
        _messagesList.value = messages
    }
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

    fun sendInitialMessage(sharedPreferences: SharedPreferences) {
        if (_initialMessageSent.value == true || sharedPreferences.getBoolean("initial_message_sent", false)) {
            _initialMessageSent.value = true
            return
        }
        val initialPrompt =
            "Buat agar percakapan ini personal, Selalu gunakan emoticon agar chat terasa hidup. Pastikan respon singkat saja dan gunakan format HTML ketika merespon. Gunakan bahasa indonesia selalu pada chat ini. Asumsikan anda adalah bot sebuah aplikasi bernama CiMon. Anda akan membantu user dengan ramah seputar pertumbuhan cabai, penyakit, dan lain lain. Jawab prompt ini dengan bagaimana chatbot seharusnya menyapa pertama kali user."
        addMessage(Message(LOADING_MESSAGE, LOADING_ID))
        sendMessage(initialPrompt)
        _initialMessageSent.value = true
        sharedPreferences.edit().putBoolean("initial_message_sent", true).apply()
    }
}