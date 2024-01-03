package com.example.storyapp.view.detail

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.storyapp.data.remote.response.ErrorResponse
import com.example.storyapp.data.remote.response.StoryResponse
import com.example.storyapp.data.repository.StoryRepository
import com.google.gson.Gson
import kotlinx.coroutines.launch
import retrofit2.HttpException

sealed interface DetailState {
    data class Success(val storyResponse: StoryResponse) : DetailState
    data class Error(val errorMessage: String?) : DetailState
    object Loading : DetailState
}

class DetailViewModel(
    private val storyRepository: StoryRepository,
) : ViewModel() {
    private val _uiState = MutableLiveData<DetailState>()
    val uiState: LiveData<DetailState> = _uiState

    fun getStory(id: String) {

        viewModelScope.launch {
            _uiState.value = DetailState.Loading

            try {
                _uiState.value = DetailState.Success(storyRepository.getStory(id))
            } catch (e: HttpException) {
                val errorBody =
                    Gson().fromJson(e.response()?.errorBody()?.string(), ErrorResponse::class.java)
                _uiState.value = DetailState.Error(errorBody.message)
            }
        }
    }
}