package com.ifs21028.lostandfound.presentation.laf

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import com.ifs21028.lostandfound.data.local.entity.LafEntity
import com.ifs21028.lostandfound.data.remote.MyResult
import com.ifs21028.lostandfound.data.remote.response.DataAddLafResponse
import com.ifs21028.lostandfound.data.remote.response.LafDeleteLafResponse
import com.ifs21028.lostandfound.data.remote.response.LafDetailLafResponse
import com.ifs21028.lostandfound.data.remote.response.LafUpdateLafResponse
import com.ifs21028.lostandfound.data.repository.LafRepository
import com.ifs21028.lostandfound.data.repository.LocalLafRepository
import com.ifs21028.lostandfound.presentation.ViewModelFactory

class LafViewModel(
    private val lafRepository: LafRepository,
    private val localLafRepository: LocalLafRepository,
) : ViewModel() {
    fun getDetailLaf(todoId: Int): LiveData<MyResult<LafDetailLafResponse>>{
        return lafRepository.getDetailLaf(todoId).asLiveData()
    }
    fun postLaf(
        title: String,
        description: String,
        status: String,
    ): LiveData<MyResult<DataAddLafResponse>>{
        return lafRepository.postLaf(
            title,
            description,
            status
        ).asLiveData()
    }
    fun putLaf(
        lafId: Int,
        title: String,
        description: String,
        status: String,
        isCompleted: Boolean,
    ): LiveData<MyResult<LafUpdateLafResponse>> {
        return lafRepository.putLaf(
            lafId,
            title,
            description,
            status,
            isCompleted,
        ).asLiveData()
    }
    fun deleteLaf(lafId: Int): LiveData<MyResult<LafDeleteLafResponse>> {
        return lafRepository.deleteLaf(lafId).asLiveData()
    }

    fun getLocalLaf(): LiveData<List<LafEntity>?> {
        return localLafRepository.getAllTodos()
    }
    fun getLocalLaf(todoId: Int): LiveData<LafEntity?> {
        return localLafRepository.get(todoId)
    }
    fun insertLocalLaf(todo: LafEntity) {
        localLafRepository.insert(todo)
    }
    fun deleteLocalLaf(todo: LafEntity) {
        localLafRepository.delete(todo)
    }

    companion object {
        @Volatile
        private var INSTANCE: LafViewModel? = null
        fun getInstance(
            lafRepository: LafRepository,
            localLafRepository: LocalLafRepository,
        ): LafViewModel {
            synchronized(ViewModelFactory::class.java) {
                INSTANCE = LafViewModel(
                    lafRepository,
                    localLafRepository
                )
            }
            return INSTANCE as LafViewModel
        }
    }

}
