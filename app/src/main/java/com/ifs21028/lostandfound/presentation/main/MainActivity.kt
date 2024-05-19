package com.ifs21028.lostandfound.presentation.main

import com.ifs21028.lostandfound.R
import com.ifs21028.lostandfound.adapter.LafAdapter
import com.ifs21028.lostandfound.data.remote.MyResult
import com.ifs21028.lostandfound.data.remote.response.LafGetAllLafResponse
import com.ifs21028.lostandfound.data.remote.response.LostFoundsItemResponse
import com.ifs21028.lostandfound.databinding.ActivityMainBinding
import com.ifs21028.lostandfound.helper.Utils.Companion.observeOnce
import com.ifs21028.lostandfound.presentation.ViewModelFactory
import com.ifs21028.lostandfound.presentation.laf.LafDetailActivity
import com.ifs21028.lostandfound.presentation.laf.LafManageActivity
import com.ifs21028.lostandfound.presentation.login.LoginActivity
import com.ifs21028.lostandfound.presentation.profile.ProfileActivity
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.SearchView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.ifs21028.lostandfound.presentation.laf.LafFavoriteActivity

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private val viewModel by viewModels<MainViewModel> {
        ViewModelFactory.getInstance(this)
    }
    private val launcher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == LafManageActivity.RESULT_CODE) {
            recreate()
        }
//        if (result.resultCode == LafDetailActivity.RESULT_CODE) {
//            result.data?.let {
//                val isChanged = it.getBooleanExtra(
//                    LafDetailActivity.KEY_IS_CHANGED,
//                    false
//                )
//                if (isChanged) {
//                    recreate()
//                }
//            }
//        }
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setupView()
        setupAction()
    }
    private fun setupView() {
        showComponentNotEmpty(false)
        showEmptyError(false)
        showLoading(true)
        binding.appbarMain.overflowIcon =
            ContextCompat
                .getDrawable(this, R.drawable.ic_more_vert_24)
        observeGetAllLaf(null, null, null)
    }
    private fun setupAction() {
        binding.appbarMain.setOnMenuItemClickListener { menuItem ->
            when (menuItem.itemId) {
                R.id.mainMenuProfile -> {
                    openProfileActivity()
                    true
                }
                R.id.mainMenuFavoriteTodos -> {
                    openFavoriteTodoActivity()
                    true
                }
                R.id.mainMenuLogout -> {
                    viewModel.logout()
                    openLoginActivity()
                    true
                }
                R.id.btnFilter -> {
                    val checkedItems = booleanArrayOf(false, false, false, false, false)
                    val builder: AlertDialog.Builder = AlertDialog.Builder(this)
                    builder
                        .setTitle("Pilih yang ingin ditampilkan")
                        .setPositiveButton("Pilih") { dialog, which ->
                            val saya = if (checkedItems[0]) 1 else null

                            val lostorfound: String? = if(checkedItems[1]) {
                                if(checkedItems[2]) {
                                    null
                                } else {
                                    "lost"
                                }
                            } else {
                                if(checkedItems[2]) {
                                    "found"
                                } else {
                                    null
                                }
                            }

                            val status: Int? = if(checkedItems[3]) {
                                if(checkedItems[4]) {
                                    null
                                } else {
                                    1
                                }
                            } else {
                                if(checkedItems[4]) {
                                    0
                                } else {
                                    null
                                }
                            }

                            observeGetAllLaf(status, saya, lostorfound)
                        }
                        .setNegativeButton("Batal") { dialog, which ->
                            // Do something else.
                        }
                        .setMultiChoiceItems(
                            arrayOf("Saya", "Lost", "Found", "Completed", "Incompleted"), checkedItems) { dialog, which, isChecked ->
                            checkedItems[which] = isChecked
                        }

//                        Log.d("CheckedItemsDump", "Checked items: ${checkedItems.contentToString()}")

                    val dialog: AlertDialog = builder.create()
                    dialog.show()
                    true
                }
                else -> false
            }
        }
        binding.fabMainAddTodo.setOnClickListener {
            openAddLafActivity()
        }
        viewModel.getSession().observe(this) { user ->
            if (!user.isLogin) {
                openLoginActivity()
            } else {
                observeGetAllLaf(null, null, null)
            }
        }
    }
    private fun observeGetAllLaf(isCompleted: Int?, isMe: Int?, status: String?) {
        viewModel.getAllLaf(isCompleted, isMe, status).observe(this) { result ->
            if (result != null) {
                when (result) {
                    is MyResult.Loading -> {
                        showLoading(true)
                    }
                    is MyResult.Success -> {
                        showLoading(false)
                        loadLafToLayout(result.data)
                    }
                    is MyResult.Error -> {
                        showLoading(false)
                        showEmptyError(true)
                    }
                }
            }
        }
    }
    private fun loadLafToLayout(response: LafGetAllLafResponse) {
        val allLaf = response.data.lostFounds
        val layoutManager = LinearLayoutManager(this)
        binding.rvMainTodos.layoutManager = layoutManager
        val itemDecoration = DividerItemDecoration(
            this,
            layoutManager.orientation
        )
        binding.rvMainTodos.removeItemDecoration(itemDecoration)
        if (allLaf.isEmpty()) {
            showComponentNotEmpty(false)
            showEmptyError(true)
            binding.rvMainTodos.adapter = null
        } else {
            showComponentNotEmpty(true)
            showEmptyError(false)
            val adapter = LafAdapter()
            adapter.submitOriginalList(allLaf)
            binding.rvMainTodos.adapter = adapter
            adapter.setOnItemClickCallback(object : LafAdapter.OnItemClickCallback {
                override fun onCheckedChangeListener(
                    laf: LostFoundsItemResponse,
                    isChecked: Boolean
                ) {
                    adapter.filter(binding.svMain.query.toString())
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
                                        this@MainActivity,
                                        "Gagal menyelesaikan Lost and Found: " + laf.title,
                                        Toast.LENGTH_SHORT
                                    ).show()
                                } else {
                                    Toast.makeText(
                                        this@MainActivity,
                                        "Gagal batal menyelesaikan Lost and Found: " + laf.title,
                                        Toast.LENGTH_SHORT
                                    ).show()
                                }
                            }
                            is MyResult.Success -> {
                                if (isChecked) {
                                    Toast.makeText(
                                        this@MainActivity,
                                        "Berhasil menyelesaikan Lost and Found: " + laf.title,
                                        Toast.LENGTH_SHORT
                                    ).show()
                                } else {
                                    Toast.makeText(
                                        this@MainActivity,
                                        "Berhasil batal menyelesaikan Lost and Found: " + laf.title,
                                        Toast.LENGTH_SHORT
                                    ).show()
                                }
                            }
                            else -> {}
                        }
                    }
                }
                override fun onClickDetailListener(lafId: Int) {
                    val intent = Intent(
                        this@MainActivity,
                        LafDetailActivity::class.java
                    )
                    intent.putExtra(LafDetailActivity.KEY_TODO_ID, lafId)
                    launcher.launch(intent)
                }
            })
            binding.svMain.setOnQueryTextListener(
                object : SearchView.OnQueryTextListener {
                    override fun onQueryTextSubmit(query: String): Boolean {
                        return false
                    }
                    override fun onQueryTextChange(newText: String): Boolean {
                        adapter.filter(newText)
                        binding.rvMainTodos.layoutManager?.scrollToPosition(0)
                        return true
                    }
                })
        }
    }
    private fun showLoading(isLoading: Boolean) {
        binding.pbMain.visibility =
            if (isLoading) View.VISIBLE else View.GONE
    }
    private fun openProfileActivity() {
        val intent = Intent(applicationContext, ProfileActivity::class.java)
        startActivity(intent)
    }
    private fun showComponentNotEmpty(status: Boolean) {
        binding.svMain.visibility =
            if (status) View.VISIBLE else View.GONE
        binding.rvMainTodos.visibility =
            if (status) View.VISIBLE else View.GONE
    }
    private fun showEmptyError(isError: Boolean) {
        binding.tvMainEmptyError.visibility =
            if (isError) View.VISIBLE else View.GONE
    }
    private fun openLoginActivity() {
        val intent = Intent(applicationContext, LoginActivity::class.java)
        intent.flags =
            Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
        startActivity(intent)
        finish()
    }
    private fun openAddLafActivity() {
        val intent = Intent(
            this@MainActivity,
            LafManageActivity::class.java
        )
        intent.putExtra(LafManageActivity.KEY_IS_ADD, true)
        launcher.launch(intent)
    }
    private fun openFavoriteTodoActivity() {
        val intent = Intent(
            this@MainActivity,
            LafFavoriteActivity::class.java
        )
        launcher.launch(intent)
    }
}