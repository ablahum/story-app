package com.example.storyapp.view.signin

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.example.storyapp.data.pref.UserModel
import com.example.storyapp.data.remote.response.ErrorResponse
import com.example.storyapp.data.remote.response.SignInResponse
import com.example.storyapp.data.repository.StoryRepository
import com.google.gson.Gson
import kotlinx.coroutines.launch
import retrofit2.HttpException

sealed interface SignInState {
    data class Success(val signInResponse: SignInResponse) : SignInState
    data class Error(val errorMessage: String?) : SignInState
    object Loading : SignInState
}

class SignInViewModel(private val storyRepository: StoryRepository) : ViewModel() {
    private val _uiState = MutableLiveData<SignInState>()
    val uiState: LiveData<SignInState> = _uiState

    fun signIn(email: String, password: String) {
        viewModelScope.launch {
            _uiState.value = SignInState.Loading

            try {
                _uiState.value = SignInState.Success(storyRepository.signIn(email, password))
            } catch (e: HttpException) {
                val errorBody =
                    Gson().fromJson(e.response()?.errorBody()?.string(), ErrorResponse::class.java)
                _uiState.value = SignInState.Error(errorBody.message)
            }
        }
    }

    fun saveUser(user: UserModel) {
        viewModelScope.launch {
            storyRepository.saveUser(user)
        }
    }

    fun getUser(): LiveData<UserModel> {
        return storyRepository.getUser().asLiveData()
    }
}