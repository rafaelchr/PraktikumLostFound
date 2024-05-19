package com.ifs21028.lostandfound.presentation.profile


import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.ifs21028.lostandfound.R
import com.ifs21028.lostandfound.data.remote.MyResult
import com.ifs21028.lostandfound.data.remote.response.DataGetMeResponse
import com.ifs21028.lostandfound.databinding.ActivityProfileBinding
import com.ifs21028.lostandfound.databinding.ActivityProfileEditBinding
import com.ifs21028.lostandfound.helper.Utils.Companion.observeOnce
import com.ifs21028.lostandfound.helper.getImageUri
import com.ifs21028.lostandfound.helper.reduceFileImage
import com.ifs21028.lostandfound.helper.uriToFile
import com.ifs21028.lostandfound.presentation.ViewModelFactory
import com.ifs21028.lostandfound.presentation.laf.LafManageActivity
import com.ifs21028.lostandfound.presentation.login.LoginActivity
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody

class ProfileEditActivity : AppCompatActivity() {
    private lateinit var binding: ActivityProfileEditBinding
    private var currentImageUri: Uri? = null

    private val viewModel by viewModels<ProfileViewModel> {
        ViewModelFactory.getInstance(this)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityProfileEditBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setupView()
        setupAction()
    }

    private fun setupView(){
        showLoading(true)
        observeGetMe()
    }

    private fun setupAction(){
        binding.apply {
            ivProfileBack.setOnClickListener {
                startActivity(Intent(this@ProfileEditActivity, ProfileActivity::class.java))
                finish()
            }

            btnTodoManageCamera.setOnClickListener {
                startCamera()
            }

            btnTodoManageGallery.setOnClickListener {
                startGallery()
            }

            btnSave.setOnClickListener {
                observeAddPhoto()
            }
        }
    }

    private fun observeAddPhoto() {
        val imageFile =
            uriToFile(currentImageUri!!, this).reduceFileImage()
        val requestImageFile =
            imageFile.asRequestBody("image/jpeg".toMediaType())
        val reqPhoto =
            MultipartBody.Part.createFormData(
                "photo",
                imageFile.name,
                requestImageFile
            )
        viewModel.addPhoto(
            reqPhoto
        ).observeOnce { result ->
            when (result) {
                is MyResult.Loading -> {
                    showLoading(true)
                }
                is MyResult.Success -> {
                    showLoading(false)
                    intent = Intent(this, ProfileActivity::class.java)
                    startActivity(intent)
                    finish()
                }
                is MyResult.Error -> {
                    showLoading(false)
                    AlertDialog.Builder(this@ProfileEditActivity).apply {
                        setTitle("Oh No!")
                        setMessage(result.error)
                        setPositiveButton("Oke") { _, _ ->
                            val resultIntent = Intent()
                            setResult(LafManageActivity.RESULT_CODE, resultIntent)
                            finishAfterTransition()
                        }
                        setCancelable(false)
                        create()
                        show()
                    }
                }
            }
        }
    }

    private fun startGallery() {
        launcherGallery.launch(
            PickVisualMediaRequest(
                ActivityResultContracts.PickVisualMedia.ImageOnly
            )
        )
    }
    private val launcherGallery = registerForActivityResult(
        ActivityResultContracts.PickVisualMedia()
    ) { uri: Uri? ->
        if (uri != null) {
            currentImageUri = uri
            showImage()
        } else {
            Toast.makeText(
                applicationContext,
                "Tidak ada media yang dipilih!",
                Toast.LENGTH_SHORT
            ).show()
        }
    }

    private fun showImage() {
        currentImageUri?.let {
            binding.ivEditProfile.setImageURI(it)
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

    private fun showLoading(isLoading: Boolean) {
        binding.pbProfile.visibility = if (isLoading) View.VISIBLE else View.GONE
        binding.layoutEditProfile.visibility = if (isLoading) View.GONE else View.VISIBLE
    }

    private fun observeGetMe(){
        viewModel.getMe().observe(this){ result ->
            if (result != null) {
                when (result) {
                    is MyResult.Loading -> {
                        showLoading(true)
                    }
                    is MyResult.Success -> {
                        showLoading(false)
                        loadProfileData(result.data)
                    }
                    is MyResult.Error -> {
                        showLoading(false)
                        Toast.makeText(
                            applicationContext, result.error, Toast.LENGTH_LONG
                        ).show()
                        viewModel.logout()
                        openLoginActivity()
                    }
                }
            }
        }
    }

    private fun loadProfileData(profile: DataGetMeResponse){
        binding.apply {
            if(profile.user.photo != null){
                val urlImg = "https://public-api.delcom.org/${profile.user.photo}"
                Glide.with(this@ProfileEditActivity)
                    .load(urlImg)
                    .placeholder(R.drawable.ic_person)
                    .into(ivEditProfile)
            }
        }
    }

    private fun openLoginActivity() {
        val intent = Intent(applicationContext, LoginActivity::class.java)
        intent.flags =
            Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
        startActivity(intent)
        finish()
    }
}

