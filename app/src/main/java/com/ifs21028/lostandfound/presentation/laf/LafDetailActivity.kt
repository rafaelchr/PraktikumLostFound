package com.ifs21028.lostandfound.presentation.laf

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.ifs21028.lostandfound.data.model.LostFound
import com.ifs21028.lostandfound.data.remote.MyResult
import com.ifs21028.lostandfound.data.remote.response.LostFoundDetailLafResponse
import com.ifs21028.lostandfound.databinding.ActivityLafDetailBinding
import com.ifs21028.lostandfound.helper.Utils.Companion.observeOnce
import com.ifs21028.lostandfound.presentation.ViewModelFactory

class LafDetailActivity : AppCompatActivity() {
    private lateinit var binding: ActivityLafDetailBinding
    private val viewModel by viewModels<LafViewModel> {
        ViewModelFactory.getInstance(this)
    }
    private var isChanged: Boolean = false
    private val launcher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == LafManageActivity.RESULT_CODE) {
            recreate()
        }
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLafDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setupView()
        setupAction()
    }
    private fun setupView() {
        showComponent(false)
        showLoading(false)
    }
    private fun setupAction() {
        val lafId = intent.getIntExtra(KEY_TODO_ID, 0)
        if (lafId == 0) {
            finish()
            return
        }
        observeGetLaf(lafId)
        binding.appbarTodoDetail.setNavigationOnClickListener {
            val resultIntent = Intent()
            resultIntent.putExtra(KEY_IS_CHANGED, isChanged)
            setResult(RESULT_CODE, resultIntent)
            finishAfterTransition()
        }
    }
    private fun observeGetLaf(lafId: Int) {
        viewModel.getDetailLaf(lafId).observeOnce { result ->
            when (result) {
                is MyResult.Loading -> {
                    showLoading(true)
                }
                is MyResult.Success -> {
                    showLoading(false)
                    loadLaf(result.data.data.lostFound)
                }
                is MyResult.Error -> {
                    Toast.makeText(
                        this@LafDetailActivity,
                        result.error,
                        Toast.LENGTH_SHORT
                    ).show()
                    showLoading(false)
                    finishAfterTransition()
                }
            }
        }
    }
    private fun loadLaf(laf: LostFoundDetailLafResponse) {
        showComponent(true)
        binding.apply {
            tvTodoDetailTitle.text = laf.title
            tvTodoDetailDate.text = "Dibuat pada: ${laf.createdAt}"
            tvTodoDetailDesc.text = laf.description
            tvTodoDetailStat.text = "Status: ${laf.status}"
            cbTodoDetailIsFinished.isChecked = laf.isCompleted == 1
            cbTodoDetailIsFinished.setOnCheckedChangeListener { _, isChecked ->
                viewModel.putLaf(
                    laf.id,
                    laf.title,
                    laf.description,
                    laf.status,
                    isChecked
                ).observeOnce {
                    when (it) {
                        is MyResult.Error -> {
                            if (isChecked) {
                                Toast.makeText(
                                    this@LafDetailActivity,
                                    "Gagal menyelesaikan todo: " + laf.title,
                                    Toast.LENGTH_SHORT
                                ).show()
                            } else {
                                Toast.makeText(
                                    this@LafDetailActivity,
                                    "Gagal batal menyelesaikan todo: " + laf.title,
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                        }
                        is MyResult.Success -> {
                            if (isChecked) {
                                Toast.makeText(
                                    this@LafDetailActivity,
                                    "Berhasil menyelesaikan todo: " + laf.title,
                                    Toast.LENGTH_SHORT
                                ).show()
                            } else {
                                Toast.makeText(
                                    this@LafDetailActivity,
                                    "Berhasil batal menyelesaikan todo: " + laf.title,
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                            if ((laf.isCompleted == 1) != isChecked) {
                                isChanged = true
                            }
                        }
                        else -> {}
                    }
                }
            }
            ivTodoDetailActionDelete.setOnClickListener {
                val builder = AlertDialog.Builder(this@LafDetailActivity)
                builder.setTitle("Konfirmasi Hapus Todo")
                    .setMessage("Anda yakin ingin menghapus todo ini?")
                builder.setPositiveButton("Ya") { _, _ ->
                    observeDeleteLaf(laf.id)
                }
                builder.setNegativeButton("Tidak") { dialog, _ ->
                    dialog.dismiss() // Menutup dialog
                }
                val dialog = builder.create()
                dialog.show()
            }
            ivTodoDetailActionEdit.setOnClickListener {
                val lostFound = LostFound(
                    laf.id,
                    laf.userId,
                    laf.title,
                    laf.description,
                    laf.status,
                    laf.isCompleted == 1,
                    laf.cover
                )
                val intent = Intent(
                    this@LafDetailActivity,
                    LafManageActivity::class.java
                )
                intent.putExtra(LafManageActivity.KEY_IS_ADD, false)
                intent.putExtra(LafManageActivity.KEY_TODO, lostFound)
                launcher.launch(intent)
            }
        }
    }
    private fun observeDeleteLaf(lafId: Int) {
        showComponent(false)
        showLoading(true)
        viewModel.deleteLaf(lafId).observeOnce {
            when (it) {
                is MyResult.Error -> {
                    showComponent(true)
                    showLoading(false)
                    Toast.makeText(
                        this@LafDetailActivity,
                        "Gagal menghapus todo: ${it.error}",
                        Toast.LENGTH_SHORT
                    ).show()
                }
                is MyResult.Success -> {
                    showLoading(false)
                    Toast.makeText(
                        this@LafDetailActivity,
                        "Berhasil menghapus todo",
                        Toast.LENGTH_SHORT
                    ).show()
                    val resultIntent = Intent()
                    resultIntent.putExtra(KEY_IS_CHANGED, true)
                    setResult(RESULT_CODE, resultIntent)
                    finishAfterTransition()
                }
                else -> {}
            }
        }
    }
    private fun showLoading(isLoading: Boolean) {
        binding.pbTodoDetail.visibility =
            if (isLoading) View.VISIBLE else View.GONE
    }
    private fun showComponent(status: Boolean) {
        binding.llTodoDetail.visibility =
            if (status) View.VISIBLE else View.GONE
    }
    companion object {
        const val KEY_TODO_ID = "todo_id"
        const val KEY_IS_CHANGED = "is_changed"
        const val RESULT_CODE = 1001
    }
}