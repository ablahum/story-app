package com.example.storyapp.view.maps

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.storyapp.data.remote.response.ErrorResponse
import com.example.storyapp.data.remote.response.StoriesResponse
import com.example.storyapp.data.repository.StoryRepository
import com.google.gson.Gson
import kotlinx.coroutines.launch
import retrofit2.HttpException

sealed interface MapsState {
    data class Success(val storiesResponse: StoriesResponse) : MapsState
    data class Error(val errorMessage: String?) : MapsState
    object Loading : MapsState
}

class MapsViewModel(
    private val storyRepository: StoryRepository
) : ViewModel() {
    private val _uiState = MutableLiveData<MapsState>()
    val uiState: LiveData<MapsState> = _uiState

    fun getStoriesWithLocation() {
        viewModelScope.launch {
            _uiState.value = MapsState.Loading

            try {
                _uiState.value = MapsState.Success(storyRepository.getStoriesWithLocation())
            } catch (e: HttpException) {
                val errorBody =
                    Gson().fromJson(e.response()?.errorBody()?.string(), ErrorResponse::class.java)
                _uiState.value = MapsState.Error(errorBody.message)
            }
        }
    }
}