package com.example.storyapp.data.pref

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class StoryModel(
    val id: String,
    val name: String,
    val description: String,
    val photoUrl: String,
    val lon: Double? = null,
    val lat: Double? = null,
    val createdAt: String? = null,
) : Parcelable