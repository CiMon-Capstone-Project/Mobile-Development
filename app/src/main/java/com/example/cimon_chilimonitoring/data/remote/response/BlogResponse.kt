package com.example.cimon_chilimonitoring.data.remote.response

import com.google.gson.annotations.SerializedName
import java.io.Serializable

data class BlogResponse(

	@field:SerializedName("data")
	val data: DataDeleteArticle? = null,

	@field:SerializedName("message")
	val message: String? = null,

	@field:SerializedName("status")
	val status: String? = null
)

data class DataDeleteArticle(

	@field:SerializedName("results")
	val results: Results? = null
)

data class Results(

	@field:SerializedName("image_url")
	val imageUrl: String? = null,

	@field:SerializedName("description")
	val description: String? = null,

	@field:SerializedName("created_at")
	val createdAt: String? = null,

	@field:SerializedName("source")
	val source: String? = null,

	@field:SerializedName("id")
	val id: Int? = null,

	@field:SerializedName("title")
	val title: String? = null
) : Serializable
