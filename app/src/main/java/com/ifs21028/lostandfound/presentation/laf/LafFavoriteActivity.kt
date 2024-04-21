package com.ifs21028.lostandfound.presentation.laf

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.SearchView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.ifs21028.lostandfound.R
import com.ifs21028.lostandfound.adapter.LafAdapter
import com.ifs21028.lostandfound.data.local.entity.LafEntity
import com.ifs21028.lostandfound.data.remote.MyResult
import com.ifs21028.lostandfound.data.remote.response.LostFoundsItemResponse
import com.ifs21028.lostandfound.databinding.ActivityLafFavoriteBinding
import com.ifs21028.lostandfound.helper.Utils.Companion.entitiesToResponses
import com.ifs21028.lostandfound.helper.Utils.Companion.observeOnce
import com.ifs21028.lostandfound.presentation.ViewModelFactory

class LafFavoriteActivity : AppCompatActivity() {
    private lateinit var binding: ActivityLafFavoriteBinding
    private val viewModel by viewModels<LafViewModel> {
        ViewModelFactory.getInstance(this)
    }
    private val launcher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == LafDetailActivity.RESULT_CODE) {
            result.data?.let {
                val isChanged = it.getBooleanExtra(
                    LafDetailActivity.KEY_IS_CHANGED,
                    false
                )
                if (isChanged) {
                    recreate()
                }
            }
        }
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLafFavoriteBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setupView()
        setupAction()
    }
    private fun setupAction() {
        binding.appbarTodoFavorite.setNavigationOnClickListener {
            val resultIntent = Intent()
            resultIntent.putExtra(LafDetailActivity.KEY_IS_CHANGED, true)
            setResult(LafDetailActivity.RESULT_CODE, resultIntent)
            finishAfterTransition()
        }
    }
    private fun setupView() {
        showComponentNotEmpty(false)
        showEmptyError(false)
        showLoading(true)
        binding.appbarTodoFavorite.overflowIcon =
            ContextCompat
                .getDrawable(this, R.drawable.ic_more_vert_24)
        observeGetTodos()
    }
    private fun observeGetTodos() {
        viewModel.getLocalLaf().observe(this) { todos ->
            loadTodosToLayout(todos)
        }
    }
    private fun loadTodosToLayout(lafs: List<LafEntity>?) {
        showLoading(false)
        val layoutManager = LinearLayoutManager(this)
        binding.rvTodoFavoriteTodos.layoutManager = layoutManager
        val itemDecoration = DividerItemDecoration(
            this,
            layoutManager.orientation
        )
        binding.rvTodoFavoriteTodos.addItemDecoration(itemDecoration)
        if (lafs.isNullOrEmpty()) {
            showEmptyError(true)
            binding.rvTodoFavoriteTodos.adapter = null
        } else {
            showComponentNotEmpty(true)
            showEmptyError(false)
            val adapter = LafAdapter()
            adapter.submitOriginalList(entitiesToResponses(lafs))
            binding.rvTodoFavoriteTodos.adapter = adapter
            adapter.setOnItemClickCallback(
                object : LafAdapter.OnItemClickCallback {
                    override fun onCheckedChangeListener(
                        laf: LostFoundsItemResponse,
                        isChecked: Boolean
                    ) {
                        adapter.filter(binding.svTodoFavorite.query.toString())
                        val newTodo = LafEntity(
                            id = laf.id,
                            userId = laf.userId,
                            title = laf.title,
                            description = laf.description,
                            status = laf.status,
                            isCompleted = laf.isCompleted,
                            cover = laf.cover,
                            createdAt = laf.createdAt,
                            updatedAt = laf.updatedAt,
                        )
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
                                            this@LafFavoriteActivity,
                                            "Gagal menyelesaikan todo: " + laf.title,
                                            Toast.LENGTH_SHORT
                                        ).show()
                                    } else {
                                        Toast.makeText(
                                            this@LafFavoriteActivity,
                                            "Gagal batal menyelesaikan todo: " + laf.title,
                                            Toast.LENGTH_SHORT
                                        ).show()
                                    }
                                }
                                is MyResult.Success -> {
                                    if (isChecked) {
                                        Toast.makeText(
                                            this@LafFavoriteActivity,
                                            "Berhasil menyelesaikan todo: " + laf.title,
                                            Toast.LENGTH_SHORT
                                        ).show()
                                    } else {
                                        Toast.makeText(
                                            this@LafFavoriteActivity,
                                            "Berhasil batal menyelesaikan todo: " + laf.title,
                                            Toast.LENGTH_SHORT
                                        ).show()
                                    }
                                    viewModel.insertLocalLaf(newTodo)
                                }
                                else -> {}
                            }
                        }
                    }
                    override fun onClickDetailListener(todoId: Int) {
                        val intent = Intent(
                            this@LafFavoriteActivity,
                            LafDetailActivity::class.java
                        )
                        intent.putExtra(LafDetailActivity.KEY_TODO_ID, todoId)
                        launcher.launch(intent)
                    }
                })
            binding.svTodoFavorite.setOnQueryTextListener(
                object : SearchView.OnQueryTextListener {
                    override fun onQueryTextSubmit(query: String): Boolean {
                        return false
                    }
                    override fun onQueryTextChange(newText: String): Boolean {
                        adapter.filter(newText)
                        binding.rvTodoFavoriteTodos
                            .layoutManager?.scrollToPosition(0)

                        return true
                    }
                })
        }
    }

    private fun showComponentNotEmpty(status: Boolean) {
        binding.svTodoFavorite.visibility =
            if (status) View.VISIBLE else View.GONE
        binding.rvTodoFavoriteTodos.visibility =
            if (status) View.VISIBLE else View.GONE
    }
    private fun showEmptyError(isError: Boolean) {
        binding.tvTodoFavoriteEmptyError.visibility =
            if (isError) View.VISIBLE else View.GONE
    }
    private fun showLoading(isLoading: Boolean) {
        binding.pbTodoFavorite.visibility =
            if (isLoading) View.VISIBLE else View.GONE
    }
}