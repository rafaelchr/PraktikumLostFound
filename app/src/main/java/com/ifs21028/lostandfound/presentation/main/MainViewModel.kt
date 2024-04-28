package com.ifs21028.lostandfound.presentation.main

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.ifs21028.lostandfound.data.pref.UserModel
import com.ifs21028.lostandfound.data.remote.MyResult
import com.ifs21028.lostandfound.data.remote.response.LafGetAllLafResponse
import com.ifs21028.lostandfound.data.remote.response.LafUpdateLafResponse
import com.ifs21028.lostandfound.data.repository.AuthRepository
import com.ifs21028.lostandfound.data.repository.LafRepository
import com.ifs21028.lostandfound.presentation.ViewModelFactory
import kotlinx.coroutines.launch

class MainViewModel(
    private val authRepository: AuthRepository,
    private val lafRepository: LafRepository
) : ViewModel() {
    fun getSession(): LiveData<UserModel> {
        return authRepository.getSession().asLiveData()
    }
    fun logout() {
        viewModelScope.launch {
            authRepository.logout()
        }
    }
    fun getAllLaf(): LiveData<MyResult<LafGetAllLafResponse>> {
        return lafRepository.getAllLaf(
            null,
            1,
            null,
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
    companion object {
        @Volatile
        private var INSTANCE: MainViewModel? = null
        fun getInstance(
            authRepository: AuthRepository,
            lafRepository: LafRepository
        ): MainViewModel {
            synchronized(ViewModelFactory::class.java) {
                INSTANCE = MainViewModel(
                    authRepository,
                    lafRepository
                )
            }
            return INSTANCE as MainViewModel
        }
    }
}