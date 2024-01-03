package com.example.storyapp.view

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.storyapp.data.repository.StoryRepository
import com.example.storyapp.di.Injection
import com.example.storyapp.view.add.AddViewModel
import com.example.storyapp.view.detail.DetailViewModel
import com.example.storyapp.view.main.MainViewModel
import com.example.storyapp.view.maps.MapsViewModel
import com.example.storyapp.view.signin.SignInViewModel
import com.example.storyapp.view.signup.SignUpViewModel

class ViewModelFactory(
    private val storyRepository: StoryRepository
) :
    ViewModelProvider.NewInstanceFactory() {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return when {
            modelClass.isAssignableFrom(MainViewModel::class.java) -> {
//                MainViewModel(userRepository, storyRepository) as T
                MainViewModel(storyRepository) as T
            }

            modelClass.isAssignableFrom(DetailViewModel::class.java) -> {
                DetailViewModel(storyRepository) as T
            }

            modelClass.isAssignableFrom(AddViewModel::class.java) -> {
                AddViewModel(storyRepository) as T
            }

            modelClass.isAssignableFrom(MapsViewModel::class.java) -> {
                MapsViewModel(storyRepository) as T
            }

            modelClass.isAssignableFrom(SignInViewModel::class.java) -> {
                SignInViewModel(storyRepository) as T
            }

            modelClass.isAssignableFrom(SignUpViewModel::class.java) -> {
                SignUpViewModel(storyRepository) as T
            }

            else -> throw IllegalArgumentException("Unknown ViewModel class: " + modelClass.name)
        }
    }

    companion object {
        //        @Volatile
        private var INSTANCE: ViewModelFactory? = null

        //        @JvmStatic
        fun getInstance(context: Context): ViewModelFactory {
//            if (INSTANCE == null) {
//                synchronized(ViewModelFactory::class.java) {
            INSTANCE = ViewModelFactory(Injection.provideRepositoryStory(context))
//                }

            return INSTANCE as ViewModelFactory

        }
    }
}