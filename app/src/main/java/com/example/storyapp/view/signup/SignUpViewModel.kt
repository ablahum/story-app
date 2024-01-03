package com.example.storyapp.view.signup

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.storyapp.data.remote.response.ErrorResponse
import com.example.storyapp.data.repository.StoryRepository
import com.google.gson.Gson
import kotlinx.coroutines.launch
import retrofit2.HttpException

sealed interface SignUpState {
    data class Success(val message: String?) : SignUpState
    data class Error(val errorMessage: String?) : SignUpState
    object Loading : SignUpState
}

//class SignUpViewModel(private val userRepository: UserRepository) : ViewModel() {
class SignUpViewModel(private val storyRepository: StoryRepository) : ViewModel() {
    private val _uiState = MutableLiveData<SignUpState>()
    val uiState: LiveData<SignUpState> = _uiState

    fun signUp(name: String, email: String, password: String) {
        viewModelScope.launch {
            _uiState.value = SignUpState.Loading

            try {
                _uiState.value =
                    SignUpState.Success(storyRepository.signUp(name, email, password).message)
            } catch (e: HttpException) {
                val errorBody =
                    Gson().fromJson(e.response()?.errorBody()?.string(), ErrorResponse::class.java)
                _uiState.value = SignUpState.Error(errorBody.message)
            }
        }
    }
}