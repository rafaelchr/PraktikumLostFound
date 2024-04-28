package com.ifs21028.lostandfound.data.remote.retrofit

import com.ifs21028.lostandfound.data.remote.response.LafAddLafResponse
import com.ifs21028.lostandfound.data.remote.response.LafDeleteLafResponse
import com.ifs21028.lostandfound.data.remote.response.LafDetailLafResponse
import com.ifs21028.lostandfound.data.remote.response.LafGetAllLafResponse
import com.ifs21028.lostandfound.data.remote.response.LafGetMeResponse
import com.ifs21028.lostandfound.data.remote.response.LafLoginResponse
import com.ifs21028.lostandfound.data.remote.response.LafRegisterResponse
import com.ifs21028.lostandfound.data.remote.response.LafUpdateLafResponse
import okhttp3.MultipartBody
import retrofit2.http.DELETE
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.GET
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Part
import retrofit2.http.Path
import retrofit2.http.Query

interface IApiService {

    @FormUrlEncoded
    @POST("auth/register")
    suspend fun register(
        @Field("name") name: String,
        @Field("email") email: String,
        @Field("password") password: String
    ): LafRegisterResponse

    @FormUrlEncoded
    @POST("auth/login")
    suspend fun login(
        @Field("email") email: String,
        @Field("password") password: String
    ): LafLoginResponse

    @GET("users/me")
    suspend fun getMe(): LafGetMeResponse

    @FormUrlEncoded
    @POST("lost-founds")
    suspend fun postLaf(
        @Field("title") title: String,
        @Field("description") description: String,
        @Field("status") status: String,
    ): LafAddLafResponse

    @FormUrlEncoded
    @PUT("lost-founds/{id}")
    suspend fun putLaf(
        @Path("id") lafId: Int,
        @Field("title") title: String,
        @Field("description") description: String,
        @Field("status") status: String,
        @Field("is_completed") isCompleted: Int,
    ): LafUpdateLafResponse

    @GET("lost-founds")
    suspend fun getAllLaf(
        @Query("is_completed") isCompleted: Int?,
        @Query("is_me") isMe: Int?,
        @Query("status") status: String?,
    ): LafGetAllLafResponse

    @GET("lost-founds/{id}")
    suspend fun getDetailLaf(
        @Path("id") lafId: Int,
    ): LafDetailLafResponse

    @DELETE("lost-founds/{id}")
    suspend fun deleteLaf(
        @Path("id") lafId: Int,
    ): LafDeleteLafResponse

    @Multipart
    @POST("lost-founds/{id}/cover")
    suspend fun addCoverLaf(
        @Path("id") lafId: Int,
        @Part cover: MultipartBody.Part,
    ): LafRegisterResponse
}