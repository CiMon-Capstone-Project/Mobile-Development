package com.example.cimon_chilimonitoring.data.remote.response

import com.google.gson.annotations.SerializedName

data class PostArticlesResponse(

	@field:SerializedName("data")
	val data: DataPostArticles? = null,

	@field:SerializedName("message")
	val message: String? = null,

	@field:SerializedName("status")
	val status: String? = null
)

data class DataPostArticles(

	@field:SerializedName("file")
	val imageUrl: String? = null,

	@field:SerializedName("description")
	val description: String? = null,

	@field:SerializedName("title")
	val title: String? = null,
)
