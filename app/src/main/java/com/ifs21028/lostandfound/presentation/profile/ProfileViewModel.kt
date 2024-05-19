package com.ifs21028.lostandfound.presentation.profile

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.ifs21028.lostandfound.data.remote.MyResult
import com.ifs21028.lostandfound.data.remote.response.DataGetMeResponse
import com.ifs21028.lostandfound.data.remote.response.LafRegisterResponse
import com.ifs21028.lostandfound.data.repository.AuthRepository
import com.ifs21028.lostandfound.data.repository.UserRepository
import com.ifs21028.lostandfound.presentation.ViewModelFactory
import kotlinx.coroutines.launch
import okhttp3.MultipartBody

class ProfileViewModel(
    private val authRepository: AuthRepository,
    private val userRepository: UserRepository
) : ViewModel() {
    fun logout() {
        viewModelScope.launch {
            authRepository.logout()
        }
    }
    fun getMe(): LiveData<MyResult<DataGetMeResponse>> {
        return userRepository.getMe().asLiveData()
    }

    fun addPhoto(
        photo: MultipartBody.Part,
    ): LiveData<MyResult<LafRegisterResponse>> {
        return userRepository.addPhoto(photo).asLiveData()
    }

    companion object {
        @Volatile
        private var INSTANCE: ProfileViewModel? = null
        fun getInstance(
            authRepository: AuthRepository,
            userRepository: UserRepository
        ): ProfileViewModel {
            synchronized(ViewModelFactory::class.java) {
                INSTANCE = ProfileViewModel(
                    authRepository,
                    userRepository
                )
            }
            return INSTANCE as ProfileViewModel
        }
    }
}