package com.example.storyapp.view.add

import androidx.lifecycle.ViewModel
import androidx.lifecycle.liveData
import com.example.storyapp.data.remote.response.AddResponse
import com.example.storyapp.data.repository.StoryRepository
import com.google.gson.Gson
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import retrofit2.HttpException
import java.io.File

sealed class AddState<out R> private constructor() {
    data class Success<out T>(val data: T) : AddState<T>()
    data class Error(val error: String) : AddState<Nothing>()
    object Loading : AddState<Nothing>()
}

class AddViewModel(
    private val storyRepository: StoryRepository
) : ViewModel() {
    fun addStory(file: File, description: String) = liveData {
        emit(AddState.Loading)
        val requestBody = description.toRequestBody("text/plain".toMediaType())
        val requestImageFile = file.asRequestBody("image/jpeg".toMediaType())
        val multipartBody = MultipartBody.Part.createFormData(
            "photo",
            file.name,
            requestImageFile
        )

        try {
            val successResponse = storyRepository.addStory(multipartBody, requestBody)

            emit(AddState.Success(successResponse))
        } catch (e: HttpException) {
            val errorBody = e.response()?.errorBody()?.string()
            val errorResponse = Gson().fromJson(errorBody, AddResponse::class.java)

            emit(AddState.Error(errorResponse.message))
        }
    }
}