package com.example.storyapp.view.main

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.example.storyapp.data.pref.UserModel
import com.example.storyapp.data.remote.response.ErrorResponse
import com.example.storyapp.data.remote.response.ListStoryItem
import com.example.storyapp.data.remote.response.StoriesResponse
import com.example.storyapp.data.remote.response.StoryResponse
import com.example.storyapp.data.repository.StoryRepository
import com.google.gson.Gson
import kotlinx.coroutines.launch
import retrofit2.HttpException

sealed interface StoryState {
    data class Success(val storiesResponse: LiveData<PagingData<ListStoryItem>>) : StoryState
    data class Error(val errorMessage: String?) : StoryState
    object Loading : StoryState
}

class MainViewModel(
    private val storyRepository: StoryRepository
) : ViewModel() {
    private val _uiState = MutableLiveData<StoryState>()
    val uiState: LiveData<StoryState> = _uiState
    val listStory: LiveData<PagingData<ListStoryItem>> =
        storyRepository.getListStories().cachedIn(viewModelScope)

    fun getStories() {
        viewModelScope.launch {
            _uiState.value = StoryState.Loading

            try {
                _uiState.value = StoryState.Success(storyRepository.getListStories())
            } catch (e: HttpException) {
                val errorBody =
                    Gson().fromJson(e.response()?.errorBody()?.string(), ErrorResponse::class.java)
                _uiState.value = StoryState.Error(errorBody.message)
            }
        }
    }

    fun getUser(): LiveData<UserModel> {
        return storyRepository.getUser().asLiveData()
    }

    fun logout() {
        viewModelScope.launch {
            storyRepository.logout()
        }
    }
}