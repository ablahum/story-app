package com.example.storyapp.data.remote.retrofit

import com.example.storyapp.data.remote.response.AddResponse
import com.example.storyapp.data.remote.response.SignInResponse
import com.example.storyapp.data.remote.response.SignUpResponse
import com.example.storyapp.data.remote.response.StoriesResponse
import com.example.storyapp.data.remote.response.StoryResponse
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.GET
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part
import retrofit2.http.Path
import retrofit2.http.Query

interface ApiService {
    @Multipart
    @POST("stories")
    suspend fun addStory(
        @Part file: MultipartBody.Part,
        @Part("description") description: RequestBody,
    ): AddResponse

    @FormUrlEncoded
    @POST("login")
    suspend fun signIn(
        @Field("email") email: String,
        @Field("password") password: String
    ): SignInResponse

    @FormUrlEncoded
    @POST("register")
    suspend fun signUp(
        @Field("name") name: String,
        @Field("email") email: String,
        @Field("password") password: String
    ): SignUpResponse

    @GET("stories")
    suspend fun getStories(
        @Query("page") page: Int? = null,
        @Query("size") size: Int? = null
    ): StoriesResponse

    @GET("stories/{id}")
    suspend fun getStory(
        @Path("id") id: String
    ): StoryResponse

    @GET("stories")
    suspend fun getStoriesWithLocation(
        @Query("location") location: Int = 1,
    ): StoriesResponse
}