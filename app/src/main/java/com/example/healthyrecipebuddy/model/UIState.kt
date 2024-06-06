package com.example.healthyrecipebuddy.model

sealed interface UiState {
    object Initial: UiState
    object Loading : UiState
    data class Success(val response: String) : UiState
    data class Error(val error: String) : UiState

}