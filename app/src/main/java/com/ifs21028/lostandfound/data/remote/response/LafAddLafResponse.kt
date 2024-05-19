package com.ifs21028.lostandfound.data.remote.response

import com.google.gson.annotations.SerializedName

data class LafAddLafResponse(

	@field:SerializedName("data")
	val data: DataAddLafResponse,

	@field:SerializedName("success")
	val success: Boolean,

	@field:SerializedName("message")
	val message: String
)

data class DataAddLafResponse(

	@field:SerializedName("lost_found_id")
	val  lostFoundId: Int
)
