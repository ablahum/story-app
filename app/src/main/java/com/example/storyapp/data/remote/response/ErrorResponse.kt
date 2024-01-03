package com.example.storyapp.data.remote.response

import com.google.gson.annotations.SerializedName

data class ErrorResponse(
    @field:SerializedName("message")
    val message: String,
)