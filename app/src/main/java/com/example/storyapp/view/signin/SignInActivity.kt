package com.example.storyapp.view.signin

import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.view.View
import android.view.WindowInsets
import android.view.WindowManager
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.example.storyapp.data.pref.UserModel
import com.example.storyapp.databinding.ActivitySignInBinding
import com.example.storyapp.view.ViewModelFactory
import com.example.storyapp.view.main.MainActivity

class SignInActivity : AppCompatActivity() {
    private val viewModel by viewModels<SignInViewModel> {
        ViewModelFactory.getInstance(this)
    }
    private lateinit var binding: ActivitySignInBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySignInBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupView()
        setupAction()
        playAnimation()
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
        binding.signInBtn.setOnClickListener {
            val email = binding.etEmail.text.toString()
            val password = binding.etPassword.text.toString()

            when {
                email.isEmpty() -> {
                    binding.etLayoutEmail.error = "Masukkkan email"
                }

                password.isEmpty() -> {
                    binding.etLayoutPassword.error = "Masukkan password"
                }

                else -> {
                    viewModel.signIn(email, password)

                    viewModel.uiState.observe(this) { uiState ->
                        when (uiState) {
                            is SignInState.Loading -> {
                                binding.progressBar.visibility = View.VISIBLE
                            }

                            is SignInState.Success -> {
                                binding.progressBar.visibility = View.GONE

                                val loginResult = uiState.signInResponse.loginResult

                                viewModel.saveUser(
                                    UserModel(
                                        loginResult.name,
                                        loginResult.token
                                    )
                                )

                                AlertDialog.Builder(this).apply {
                                    setTitle("Yeah!")
                                    setMessage(uiState.signInResponse.message)
                                    setPositiveButton("Lanjut") { _, _ ->
                                        viewModel.getUser().observe(this@SignInActivity) { user ->
                                            if (user.token.isNotEmpty()) {
                                                val intent =
                                                    Intent(
                                                        this@SignInActivity,
                                                        MainActivity::class.java
                                                    )
                                                intent.flags =
                                                    Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK

                                                startActivity(intent)
                                            }
                                        }

                                        finish()
                                    }

                                    show()
                                }
                            }

                            is SignInState.Error -> {
                                binding.progressBar.visibility = View.GONE

                                AlertDialog.Builder(this).apply {
                                    setTitle("Oops!")
                                    setMessage(uiState.errorMessage)
                                    setPositiveButton("OK") { _, _ ->
                                        finish()
                                    }

                                    show()
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    private fun playAnimation() {
        ObjectAnimator.ofFloat(binding.ivPhoto, View.TRANSLATION_X, -30f, 30f).apply {
            duration = 6000
            repeatCount = ObjectAnimator.INFINITE
            repeatMode = ObjectAnimator.REVERSE
        }.start()

        val title = ObjectAnimator.ofFloat(binding.tvTitle, View.ALPHA, 1f).setDuration(100)
        val message =
            ObjectAnimator.ofFloat(binding.tvMessage, View.ALPHA, 1f).setDuration(100)
        val emailTextView =
            ObjectAnimator.ofFloat(binding.tvEmail, View.ALPHA, 1f).setDuration(100)
        val emailEditTextLayout =
            ObjectAnimator.ofFloat(binding.etLayoutEmail, View.ALPHA, 1f).setDuration(100)
        val passwordTextView =
            ObjectAnimator.ofFloat(binding.tvPassword, View.ALPHA, 1f).setDuration(100)
        val passwordEditTextLayout =
            ObjectAnimator.ofFloat(binding.etLayoutPassword, View.ALPHA, 1f).setDuration(100)
        val signInButton =
            ObjectAnimator.ofFloat(binding.signInBtn, View.ALPHA, 1f).setDuration(100)

        AnimatorSet().apply {
            playSequentially(
                title,
                message,
                emailTextView,
                emailEditTextLayout,
                passwordTextView,
                passwordEditTextLayout,
                signInButton
            )
            startDelay = 100
        }.start()
    }
}