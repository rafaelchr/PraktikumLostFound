package com.ifs21028.lostandfound.presentation.laf

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.view.View
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.ifs21028.lostandfound.data.model.LostFound
import com.ifs21028.lostandfound.data.remote.MyResult
import com.ifs21028.lostandfound.databinding.ActivityLafManageBinding
import com.ifs21028.lostandfound.helper.Utils.Companion.observeOnce
import com.ifs21028.lostandfound.presentation.ViewModelFactory

class LafManageActivity : AppCompatActivity() {
    private lateinit var binding: ActivityLafManageBinding
    private val viewModel by viewModels<LafViewModel> {
        ViewModelFactory.getInstance(this)
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLafManageBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setupView()
        setupAtion()
    }
    private fun setupView() {
        showLoading(false)
    }
    private fun setupAtion() {
        val isAddLaf = intent.getBooleanExtra(KEY_IS_ADD, true)
        if (isAddLaf) {
            manageAddLaf()
        } else {
            val lostFound = when {
                Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU -> {
                    intent.getParcelableExtra(KEY_TODO, LostFound::class.java)
                }
                else -> {
                    @Suppress("DEPRECATION")
                    intent.getParcelableExtra<LostFound>(KEY_TODO)
                }
            }
            if (lostFound == null) {
                finishAfterTransition()
                return
            }
            manageEditLaf(lostFound)
        }
        binding.appbarLafManage.setNavigationOnClickListener {
            finishAfterTransition()
        }
    }
    private fun manageAddLaf() {
        binding.apply {
            appbarLafManage.title = "Tambah Lost and Found"
            btnLafManageSave.setOnClickListener {
                val title = etLafManageTitle.text.toString()
                val description = etLafManageDesc.text.toString()
                val status = etLafManageStat.text.toString()
                if (title.isEmpty() || description.isEmpty() || status.isEmpty()) {
                    AlertDialog.Builder(this@LafManageActivity).apply {
                        setTitle("Oh No!")
                        setMessage("Tidak boleh ada data yang kosong!")
                        setPositiveButton("Oke") { _, _ -> }
                        create()
                        show()
                    }
                    return@setOnClickListener
                }
                observePostLaf(title, description, status)
            }
        }
    }
    private fun observePostLaf(title: String, description: String, status: String) {
        viewModel.postLaf(title, description, status).observeOnce { result ->
            when (result) {
                is MyResult.Loading -> {
                    showLoading(true)
                }
                is MyResult.Success -> {
                    showLoading(false)
                    val resultIntent = Intent()
                    setResult(RESULT_CODE, resultIntent)
                    finishAfterTransition()
                }
                is MyResult.Error -> {
                    AlertDialog.Builder(this@LafManageActivity).apply {
                        setTitle("Oh No!")
                        setMessage(result.error)
                        setPositiveButton("Oke") { _, _ -> }
                        create()
                        show()
                    }
                    showLoading(false)
                }
            }
        }
    }
    private fun manageEditLaf(laf: LostFound) {
        binding.apply {
            appbarLafManage.title = "Ubah Lost and Found"
            etLafManageTitle.setText(laf.title)
            etLafManageDesc.setText(laf.description)
            etLafManageStat.setText(laf.status)
            btnLafManageSave.setOnClickListener {
                val title = etLafManageTitle.text.toString()
                val description = etLafManageDesc.text.toString()
                val status = etLafManageStat.text.toString()
                if (title.isEmpty() || description.isEmpty() || status.isEmpty()) {
                    AlertDialog.Builder(this@LafManageActivity).apply {
                        setTitle("Oh No!")
                        setMessage("Tidak boleh ada data yang kosong!")
                        setPositiveButton("Oke") { _, _ -> }
                        create()
                        show()
                    }
                    return@setOnClickListener
                }
                observePutLaf(laf.id, title, description, status, laf.isCompleted)
            }
        }
    }
    private fun observePutLaf(
        lafId: Int,
        title: String,
        description: String,
        status: String,
        isCompleted: Boolean,
    ) {
        viewModel.putLaf(
            lafId,
            title,
            description,
            status,
            isCompleted
        ).observeOnce { result ->
            when (result) {
                is MyResult.Loading -> {
                    showLoading(true)
                }
                is MyResult.Success -> {
                    showLoading(false)
                    val resultIntent = Intent()
                    setResult(RESULT_CODE, resultIntent)
                    finishAfterTransition()
                }
                is MyResult.Error -> {
                    AlertDialog.Builder(this@LafManageActivity).apply {
                        setTitle("Oh No!")
                        setMessage(result.error)
                        setPositiveButton("Oke") { _, _ -> }
                        create()
                        show()
                    }
                    showLoading(false)
                }
            }
        }
    }
    private fun showLoading(isLoading: Boolean) {
        binding.pbLafManage.visibility =
            if (isLoading) View.VISIBLE else View.GONE

        binding.btnLafManageSave.isActivated = !isLoading

        binding.btnLafManageSave.text =
            if (isLoading) "" else "Simpan"
    }
    companion object {
        const val KEY_IS_ADD = "is_add"
        const val KEY_TODO = "todo"
        const val RESULT_CODE = 1002
    }
}
