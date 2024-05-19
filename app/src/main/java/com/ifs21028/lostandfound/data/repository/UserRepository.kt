package com.ifs21028.lostandfound.data.repository

import com.google.gson.Gson
import com.ifs21028.lostandfound.data.remote.MyResult
import com.ifs21028.lostandfound.data.remote.response.LafGetMeResponse
import com.ifs21028.lostandfound.data.remote.response.LafRegisterResponse
import com.ifs21028.lostandfound.data.remote.retrofit.IApiService
import kotlinx.coroutines.flow.flow
import okhttp3.MultipartBody
import retrofit2.HttpException

class UserRepository private constructor(
    private val apiService: IApiService,
) {

    fun getMe() = flow {
        emit(MyResult.Loading)
        try {
            //get success message
            emit(MyResult.Success(apiService.getMe().data))
        } catch (e: HttpException) {
            //get error message
            val jsonInString = e.response()?.errorBody()?.string()
            emit(
                MyResult.Error(
                    Gson()
                        .fromJson(jsonInString, LafGetMeResponse::class.java)
                        .message
                )
            )
        }
    }

    fun addPhoto(
        photo: MultipartBody.Part,
    ) = flow {
        emit(MyResult.Loading)
        try {
            //get success message
            emit(MyResult.Success(apiService.addPhoto(photo)))
        } catch (e: HttpException) {
            //get error message
            val jsonInString = e.response()?.errorBody()?.string()
            emit(
                MyResult.Error(
                    Gson()
                        .fromJson(jsonInString, LafRegisterResponse::class.java)
                        .message
                )
            )
        }
    }

    companion object {
        @Volatile
        private var INSTANCE: UserRepository? = null
        fun getInstance(
            apiService: IApiService,
        ): UserRepository {
            synchronized(UserRepository::class.java) {
                INSTANCE = UserRepository(
                    apiService
                )
            }
            return INSTANCE as UserRepository
        }
    }
}