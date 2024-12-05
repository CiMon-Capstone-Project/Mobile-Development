package com.example.cimon_chilimonitoring.data.remote.response

import com.google.gson.annotations.SerializedName

data class DeleteArticlesResponse(

	@field:SerializedName("data")
	val data: Data? = null,

	@field:SerializedName("message")
	val message: String? = null,

	@field:SerializedName("status")
	val status: String? = null
)

data class Data(

	@field:SerializedName("id")
	val id: String? = null
)
