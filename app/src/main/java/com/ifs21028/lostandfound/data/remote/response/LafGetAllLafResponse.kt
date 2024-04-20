package com.ifs21028.lostandfound.data.remote.response

import com.google.gson.annotations.SerializedName

data class LafGetAllLafResponse(

	@field:SerializedName("data")
	val data: DataGetAllLafResponse,

	@field:SerializedName("success")
	val success: Boolean,

	@field:SerializedName("message")
	val message: String
)

data class AuthorGetAllLafResponse(

	@field:SerializedName("name")
	val name: String,

	@field:SerializedName("photo")
	val photo: Any
)

data class LostFoundsItemResponse(

	@field:SerializedName("cover")
	val cover: String,

	@field:SerializedName("updated_at")
	val updatedAt: String,

	@field:SerializedName("user_id")
	val userId: Int,

	@field:SerializedName("author")
	val author: AuthorGetAllLafResponse,

	@field:SerializedName("description")
	val description: String,

	@field:SerializedName("created_at")
	val createdAt: String,

	@field:SerializedName("id")
	val id: Int,

	@field:SerializedName("title")
	val title: String,

	@field:SerializedName("is_completed")
	var isCompleted: Int,

	@field:SerializedName("status")
	val status: String
)

data class DataGetAllLafResponse(

	@field:SerializedName("lost_founds")
	val lostFounds: List<LostFoundsItemResponse>
)
