package com.ifs21028.lostandfound.data.remote.response

import com.google.gson.annotations.SerializedName

data class LafRegisterResponse(

	@field:SerializedName("success")
	val success: Boolean,

	@field:SerializedName("message")
	val message: String
)
