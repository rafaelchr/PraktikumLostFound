package com.ifs21028.lostandfound.presentation.laf

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import com.ifs21028.lostandfound.data.remote.MyResult
import com.ifs21028.lostandfound.data.remote.response.DataAddLafResponse
import com.ifs21028.lostandfound.data.remote.response.LafDeleteLafResponse
import com.ifs21028.lostandfound.data.remote.response.LafDetailLafResponse
import com.ifs21028.lostandfound.data.remote.response.LafUpdateLafResponse
import com.ifs21028.lostandfound.data.repository.LafRepository
import com.ifs21028.lostandfound.presentation.ViewModelFactory

class LafViewModel(
    private val lafRepository: LafRepository
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
    companion object {
        @Volatile
        private var INSTANCE: LafViewModel? = null
        fun getInstance(
            lafRepository: LafRepository
        ): LafViewModel {
            synchronized(ViewModelFactory::class.java) {
                INSTANCE = LafViewModel(
                    lafRepository
                )
            }
            return INSTANCE as LafViewModel
        }
    }
}
