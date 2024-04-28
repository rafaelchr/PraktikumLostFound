package com.ifs21028.lostandfound.data.repository

import com.google.gson.Gson
import com.ifs21028.lostandfound.data.remote.MyResult
import com.ifs21028.lostandfound.data.remote.response.LafAddLafResponse
import com.ifs21028.lostandfound.data.remote.response.LafDeleteLafResponse
import com.ifs21028.lostandfound.data.remote.response.LafDetailLafResponse
import com.ifs21028.lostandfound.data.remote.response.LafGetAllLafResponse
import com.ifs21028.lostandfound.data.remote.response.LafRegisterResponse
import com.ifs21028.lostandfound.data.remote.response.LafUpdateLafResponse
import com.ifs21028.lostandfound.data.remote.retrofit.IApiService
import kotlinx.coroutines.flow.flow
import okhttp3.MultipartBody
import retrofit2.HttpException

class LafRepository private constructor(
    private val apiService: IApiService,
) {
    fun postLaf(
        title: String,
        description: String,
        status: String,
    ) = flow {
        emit(MyResult.Loading)
        try {
            //get success message
            emit(
                MyResult.Success(
                    apiService.postLaf(title, description, status).data
                )
            )
        } catch (e: HttpException) {
            //get error message
            val jsonInString = e.response()?.errorBody()?.string()
            emit(
                MyResult.Error(
                    Gson()
                        .fromJson(jsonInString, LafAddLafResponse::class.java)
                        .message
                )
            )
        }
    }

    fun putLaf(
        lafId: Int,
        title: String,
        description: String,
        status: String,
        isCompleted: Boolean,
    ) = flow {
        emit(MyResult.Loading)
        try {
            //get success message
            emit(
                MyResult.Success(
                    apiService.putLaf(
                        lafId,
                        title,
                        description,
                        status,
                        if (isCompleted) 1 else 0
                    )
                )
            )
        } catch (e: HttpException) {
            //get error message
            val jsonInString = e.response()?.errorBody()?.string()
            emit(
                MyResult.Error(
                    Gson()
                        .fromJson(jsonInString, LafUpdateLafResponse::class.java)
                        .message
                )
            )
        }
    }

    fun getAllLaf(
        isCompleted: Int?,
        isMe: Int?,
        status: String?,
    ) = flow {
        emit(MyResult.Loading)
        try {
            //get success message
            emit(
                MyResult.Success(
                    apiService.getAllLaf(
                        isCompleted,
                        isMe,
                        status,
                    )
                )
            )
        } catch (e: HttpException) {
            //get error message
            val jsonInString = e.response()?.errorBody()?.string()
            emit(
                MyResult.Error(
                    Gson()
                        .fromJson(jsonInString, LafGetAllLafResponse::class.java)
                        .message
                )
            )
        }
    }

    fun getDetailLaf(
        lafId: Int,
    ) = flow {
        emit(MyResult.Loading)
        try {
            //get success message
            emit(MyResult.Success(apiService.getDetailLaf(lafId)))
        } catch (e: HttpException) {
            //get error message
            val jsonInString = e.response()?.errorBody()?.string()
            emit(
                MyResult.Error(
                    Gson()
                        .fromJson(jsonInString, LafDetailLafResponse::class.java)
                        .message
                )
            )
        }
    }

    fun deleteLaf(
        lafId: Int,
    ) = flow {
        emit(MyResult.Loading)
        try {
        //get success message
            emit(MyResult.Success(apiService.deleteLaf(lafId)))
        } catch (e: HttpException) {
            //get error message
            val jsonInString = e.response()?.errorBody()?.string()
            emit(
                MyResult.Error(
                    Gson()
                        .fromJson(jsonInString, LafDeleteLafResponse::class.java)
                        .message
                )
            )
        }
    }

    fun addCoverLaf(
        todoId: Int,
        cover: MultipartBody.Part,
    ) = flow {
        emit(MyResult.Loading)
        try {
            //get success message
            emit(MyResult.Success(apiService.addCoverLaf(todoId, cover)))
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
        private var INSTANCE: LafRepository? = null
        fun getInstance(
            apiService: IApiService,
        ): LafRepository {
            synchronized(LafRepository::class.java) {
                INSTANCE = LafRepository(
                    apiService
                )
            }
            return INSTANCE as LafRepository
        }
    }
}



