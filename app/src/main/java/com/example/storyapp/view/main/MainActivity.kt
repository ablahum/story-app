package com.example.storyapp.view.main

import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.WindowInsets
import android.view.WindowManager
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.storyapp.R
import com.example.storyapp.data.pref.StoryModel
import com.example.storyapp.data.remote.response.ListStoryItem
import com.example.storyapp.databinding.ActivityMainBinding
import com.example.storyapp.view.LoadingStateAdapter
import com.example.storyapp.view.StoryAdapter
import com.example.storyapp.view.ViewModelFactory
import com.example.storyapp.view.add.AddActivity
import com.example.storyapp.view.detail.DetailActivity
import com.example.storyapp.view.maps.MapsActivity
import com.example.storyapp.view.welcome.WelcomeActivity

class MainActivity : AppCompatActivity() {
    private val viewModel by viewModels<MainViewModel> {
        ViewModelFactory.getInstance(this)
    }
    private lateinit var binding: ActivityMainBinding
    private val adapter: StoryAdapter = StoryAdapter()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        viewModel.getUser().observe(this) { user ->
            if (!user.isLogin) {
                startActivity(Intent(this, WelcomeActivity::class.java))

                finish()
            }
        }

        setupView()
        setupAction()
        playAnimation()
        showStories()
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

    private fun setupAction() {
        binding.fab.setOnClickListener {
            val intent = Intent(this@MainActivity, AddActivity::class.java)

            startActivity(intent)
        }

        binding.topAppBar.setOnMenuItemClickListener { menuItem ->
            when (menuItem.itemId) {
                R.id.menu1 -> {
                    val intent = Intent(this, MapsActivity::class.java)
                    startActivity(intent)

                    true
                }

                R.id.menu2 -> {
                    viewModel.logout()

                    true
                }

                else -> false
            }
        }
    }

    private fun showStories() {
        viewModel.getStories()

        viewModel.uiState.observe(this) { uiState ->
            when (uiState) {
                is StoryState.Loading -> {
                    binding.progressBar.visibility = View.VISIBLE
                }

                is StoryState.Success -> {
                    binding.progressBar.visibility = View.GONE

                    setupRecycleView()
                }

                is StoryState.Error -> {
                    binding.progressBar.visibility = View.GONE

                    Log.i(TAG, "error = ${uiState.errorMessage}")
                }
            }
        }
    }

    private fun setupRecycleView() {
        binding.rvItems.layoutManager = LinearLayoutManager(this)
        binding.rvItems.setHasFixedSize(true)

        binding.rvItems.adapter = adapter

        adapter.withLoadStateFooter(
            footer = LoadingStateAdapter {
                adapter.retry()
            }
        )

        viewModel.listStory.observe(this, {
            adapter.submitData(lifecycle, it)
        })

        adapter.setOnItemClickCallback(
            object : StoryAdapter.OnItemClickCallback {
                override fun onItemClicked(data: ListStoryItem) {
//                    val intent = Intent(this@MainActivity, DetailActivity::class.java)
//                    intent.putExtra(DetailActivity.EXTRA_ID, data.id)
//
//                    startActivity(intent)

                    val story = StoryModel(
                        data.id.toString(),
                        data.name.toString(),
                        data.description.toString(),
                        data.photoUrl.toString()
                    )

                    val moveWithObjectIntent = Intent(this@MainActivity, DetailActivity::class.java)
                    moveWithObjectIntent.putExtra(DetailActivity.EXTRA_STORY, story)
                    startActivity(moveWithObjectIntent)
                }
            })
    }

    private fun playAnimation() {
        val fab =
            ObjectAnimator.ofFloat(binding.fab, View.ALPHA, 1f).setDuration(100)

        AnimatorSet().apply {
            playSequentially(fab)
            start()
        }
    }

    companion object {
        private const val TAG = "MainActivity"
    }
}
