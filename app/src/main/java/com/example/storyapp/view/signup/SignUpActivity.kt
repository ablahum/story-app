package com.example.storyapp.view.signup

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
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.example.storyapp.data.pref.UserModel
import com.example.storyapp.databinding.ActivitySignUpBinding
import com.example.storyapp.view.ViewModelFactory
import com.example.storyapp.view.main.MainActivity
import com.example.storyapp.view.signin.SignInActivity
import com.example.storyapp.view.signin.SignInViewModel

class SignUpActivity : AppCompatActivity() {
    private val viewModel by viewModels<SignUpViewModel> {
        ViewModelFactory.getInstance(this)
    }
    private lateinit var binding: ActivitySignUpBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySignUpBinding.inflate(layoutInflater)
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
        binding.signUpBtn.setOnClickListener {
            val name = binding.etName.text.toString()
            val email = binding.etEmail.text.toString()
            val password = binding.etPassword.text.toString()

            when {
                name.isEmpty() -> {
                    binding.etLayoutName.error = "Masukkkan nama"
                }

                email.isEmpty() -> {
                    binding.etLayoutEmail.error = "Masukkkan email"
                }

                password.isEmpty() -> {
                    binding.etLayoutPassword.error = "Masukkan password"
                }

                else -> {
                    viewModel.signUp(name, email, password)

                    viewModel.uiState.observe(this) { uiState ->
                        when (uiState) {
                            is SignUpState.Loading -> {
                                binding.progressBar.visibility = View.VISIBLE
                            }

                            is SignUpState.Success -> {
                                binding.progressBar.visibility = View.GONE

                                AlertDialog.Builder(this).apply {
                                    setTitle("Yeah!")
                                    setMessage(uiState.message)
                                    setPositiveButton("Lanjut") { _, _ ->
                                        finish()
                                    }

                                    show()
                                }
                            }

                            is SignUpState.Error -> {
                                binding.progressBar.visibility = View.GONE

                                AlertDialog.Builder(this).apply {
                                    setTitle("Oops!")
                                    setMessage(uiState.errorMessage)
                                    setPositiveButton("Coba lagi") { _, _ ->
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
        val nameTextView =
            ObjectAnimator.ofFloat(binding.tvName, View.ALPHA, 1f).setDuration(100)
        val nameEditTextLayout =
            ObjectAnimator.ofFloat(binding.etLayoutName, View.ALPHA, 1f).setDuration(100)
        val emailTextView =
            ObjectAnimator.ofFloat(binding.tvEmail, View.ALPHA, 1f).setDuration(100)
        val emailEditTextLayout =
            ObjectAnimator.ofFloat(binding.etLayoutEmail, View.ALPHA, 1f).setDuration(100)
        val passwordTextView =
            ObjectAnimator.ofFloat(binding.tvPassword, View.ALPHA, 1f).setDuration(100)
        val passwordEditTextLayout =
            ObjectAnimator.ofFloat(binding.etLayoutPassword, View.ALPHA, 1f).setDuration(100)
        val signUpButton =
            ObjectAnimator.ofFloat(binding.signUpBtn, View.ALPHA, 1f).setDuration(100)


        AnimatorSet().apply {
            playSequentially(
                title,
                nameTextView,
                nameEditTextLayout,
                emailTextView,
                emailEditTextLayout,
                passwordTextView,
                passwordEditTextLayout,
                signUpButton
            )
            startDelay = 100
        }.start()
    }
}