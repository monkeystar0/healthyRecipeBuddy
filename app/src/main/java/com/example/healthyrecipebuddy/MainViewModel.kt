package com.example.healthyrecipebuddy

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.healthyrecipebuddy.model.UiState
import com.google.ai.client.generativeai.GenerativeModel
import com.google.ai.client.generativeai.type.content
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class MainViewModel(context: Context): ViewModel() {
    private val _uiState: MutableStateFlow<UiState> = MutableStateFlow(UiState.Initial)
    val uiState: StateFlow<UiState> = _uiState.asStateFlow()
    var isLoading = false

    private val apiKey = context.resources.openRawResource(R.raw.secrets)
        .bufferedReader()
        .use { it.readLine().split("=")[1] }

    private val generativeModel = GenerativeModel(
        //modelName = "gemini-pro-vision",
        modelName = "gemini-1.5-pro-latest",
        apiKey = apiKey
    )

    fun sendPrompt(prompt: String) {
        println("apikey: $apiKey")
        println("prompt: $prompt")
        viewModelScope.launch(Dispatchers.IO) {
            _uiState.value = UiState.Loading
            try {
                val response = generativeModel.generateContent(
                    content {
                        text(prompt)
                    }
                )
                response.text?.let { outputContent ->
                    println(outputContent)
                    _uiState.value = UiState.Success(outputContent)
                }
            }catch (e: Exception){
                println(e.localizedMessage ?: e.message.toString())
                _uiState.value = UiState.Error(e.localizedMessage ?: e.message.toString())
            } finally {
                isLoading = false // Set loading state to false
            }
        }
    }
}