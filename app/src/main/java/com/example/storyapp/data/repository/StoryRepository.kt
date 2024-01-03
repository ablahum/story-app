package com.example.storyapp.data.repository

import androidx.lifecycle.LiveData
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.liveData
import com.example.storyapp.data.StoryPagingSource
import com.example.storyapp.data.pref.UserModel
import com.example.storyapp.data.pref.UserPreference
import com.example.storyapp.data.remote.response.AddResponse
import com.example.storyapp.data.remote.response.ListStoryItem
import com.example.storyapp.data.remote.response.SignInResponse
import com.example.storyapp.data.remote.response.SignUpResponse
import com.example.storyapp.data.remote.response.StoriesResponse
import com.example.storyapp.data.remote.response.StoryResponse
import com.example.storyapp.data.remote.retrofit.ApiConfig
import com.example.storyapp.data.remote.retrofit.ApiService
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import okhttp3.MultipartBody
import okhttp3.RequestBody

class StoryRepository private constructor(
    private val userPreference: UserPreference,
    private val apiService: ApiService,
) {
    fun getListStories(): LiveData<PagingData<ListStoryItem>> {
        val user = runBlocking { userPreference.getUser().first() }
        val apiService = ApiConfig.getApiService(user.token)

        return Pager(
            config = PagingConfig(
                pageSize = 3
            ),
            pagingSourceFactory = {
                StoryPagingSource(apiService)
            }
        ).liveData
    }

    suspend fun getStoriesWithLocation(): StoriesResponse {
        val user = runBlocking { userPreference.getUser().first() }
        val apiService = ApiConfig.getApiService(user.token)

        return apiService.getStoriesWithLocation()
    }

    suspend fun getStory(id: String): StoryResponse {
        val user = runBlocking { userPreference.getUser().first() }
        val apiService = ApiConfig.getApiService(user.token)

        return apiService.getStory(id)
    }

    suspend fun addStory(imageFile: MultipartBody.Part, description: RequestBody): AddResponse {
        val user = runBlocking { userPreference.getUser().first() }
        val apiService = ApiConfig.getApiService(user.token)

        return apiService.addStory(imageFile, description)
    }

    suspend fun signIn(email: String, password: String): SignInResponse {
        return apiService.signIn(email, password)
    }

    suspend fun signUp(name: String, email: String, password: String): SignUpResponse {
        return apiService.signUp(name, email, password)
    }

    suspend fun saveUser(user: UserModel) {
        userPreference.saveUser(user)
    }

    fun getUser(): Flow<UserModel> {
        return userPreference.getUser()
    }

    suspend fun logout() {
        userPreference.logout()
    }

    companion object {
        @Volatile
        private var instance: StoryRepository? = null
        fun getInstance(
            userPreference: UserPreference,
            apiService: ApiService,
        ): StoryRepository =
            instance ?: synchronized(this) {
                instance ?: StoryRepository(userPreference, apiService)
            }.also { instance = it }
    }
}