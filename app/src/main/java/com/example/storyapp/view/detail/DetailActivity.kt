package com.example.storyapp.view.detail

import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.WindowInsets
import android.view.WindowManager
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.example.storyapp.data.pref.StoryModel
import com.example.storyapp.data.remote.response.Story
import com.example.storyapp.databinding.ActivityDetailBinding
import com.example.storyapp.view.ViewModelFactory

class DetailActivity : AppCompatActivity() {
    private val viewModel by viewModels<DetailViewModel> {
        ViewModelFactory.getInstance(this)
    }
    private lateinit var binding: ActivityDetailBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)

//        val id = intent.getStringExtra(EXTRA_ID).toString()

        setupView()
        playAnimation()

        val story = if (Build.VERSION.SDK_INT >= 33) {
            intent.getParcelableExtra<StoryModel>(EXTRA_STORY, StoryModel::class.java)
        } else {
            @Suppress("DEPRECATION")
            intent.getParcelableExtra<StoryModel>(EXTRA_STORY)
        }

        if (story != null) {
//            showStory(story.id)

            binding.tvName.text = story.name
            binding.tvDesc.text = story.description
            Glide.with(this)
                .load(story.photoUrl)
                .into(binding.ivPhoto)
        }
    }

    private fun setupView() {
        @Suppress("DEPRECATION")
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            window.insetsController?.hide(WindowInsets.Type.statusBars())
        } else {
            window.setFlags(
                WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN
            )
        }
        supportActionBar?.hide()
    }

    private fun showStory(id: String) {
        viewModel.getStory(id)

        viewModel.uiState.observe(this) { uiState ->
            when (uiState) {
                is DetailState.Loading -> {
                    binding.progressBar.visibility = View.VISIBLE
                }

                is DetailState.Success -> {
                    binding.progressBar.visibility = View.GONE

//                    val story = uiState.storyResponse.story
//                    binding.tvName.text = story?.name
//                    binding.tvDesc.text = story?.description
//                    Glide.with(this)
//                        .load(story?.photoUrl)
//                        .into(binding.ivPhoto)
                }

                is DetailState.Error -> {
                    binding.progressBar.visibility = View.GONE

                    Log.i(TAG, "error = ${uiState.errorMessage}")
                }
            }
        }
    }


    private fun playAnimation() {
        val name = ObjectAnimator.ofFloat(binding.tvName, View.ALPHA, 1f).setDuration(100)
        val desc =
            ObjectAnimator.ofFloat(binding.tvDesc, View.ALPHA, 1f).setDuration(100)

        AnimatorSet().apply {
            playSequentially(
                name,
                desc,
            )
            startDelay = 100
        }.start()
    }

    companion object {
        //        const val EXTRA_ID = "extra_id"
        const val EXTRA_STORY = "extra_story"
        private const val TAG = "DetailActivity"
    }
}