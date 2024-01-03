package com.example.storyapp.view.add

import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import com.example.storyapp.R
import com.example.storyapp.databinding.ActivityAddBinding
import com.example.storyapp.utils.getImageUri
import com.example.storyapp.utils.reduceFileImage
import com.example.storyapp.utils.uriToFile
import android.Manifest
import android.content.Intent
import androidx.activity.viewModels
import com.example.storyapp.view.ViewModelFactory
import com.example.storyapp.view.main.MainActivity

class AddActivity : AppCompatActivity() {
    private val viewModel by viewModels<AddViewModel> {
        ViewModelFactory.getInstance(this)
    }
    private lateinit var binding: ActivityAddBinding

    private var currentImageUri: Uri? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.galleryBtn.setOnClickListener { startGallery() }
        binding.cameraBtn.setOnClickListener { startCamera() }
        binding.uploadBtn.setOnClickListener { uploadStory() }
    }

    private fun startGallery() {
        launcherGallery.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
    }

    private val launcherGallery = registerForActivityResult(
        ActivityResultContracts.PickVisualMedia()
    ) { uri: Uri? ->
        if (uri != null) {
            currentImageUri = uri
            showImage()
        } else {
            Log.d("Photo Picker", "No media selected")
        }
    }

    private fun startCamera() {
        currentImageUri = getImageUri(this)
        launcherIntentCamera.launch(currentImageUri)
    }

    private val launcherIntentCamera = registerForActivityResult(
        ActivityResultContracts.TakePicture()
    ) { isSuccess ->
        if (isSuccess) {
            showImage()
        }
    }

    private fun uploadStory() {
        currentImageUri?.let { uri ->
            val imageFile = uriToFile(uri, this).reduceFileImage()
            Log.d("Image File", "showImage: ${imageFile.path}")
            val description = binding.etDesc.text.toString()

            viewModel.addStory(imageFile, description).observe(this) { result ->
                if (result != null) {
                    when (result) {
                        is AddState.Loading -> {
                            binding.progressBar.visibility = View.VISIBLE
                        }

                        is AddState.Success -> {
                            showToast(result.data.message)
                            val intent = Intent(this, MainActivity::class.java)
                            intent.flags =
                                Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK

                            startActivity(intent)
                            binding.progressBar.visibility = View.GONE
                        }

                        is AddState.Error -> {
                            showToast(result.error)
                            binding.progressBar.visibility = View.GONE
                        }
                    }
                }
            }
        } ?: showToast(getString(R.string.empty_image_warning))
    }

    private fun showImage() {
        currentImageUri?.let {
            Log.d("Image URI", "showImage: $it")
            binding.ivPreview.setImageURI(it)
        }
    }

    private fun showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }

    companion object {
        private const val REQUIRED_PERMISSION = Manifest.permission.CAMERA
    }
}