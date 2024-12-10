package com.example.cimon_chilimonitoring.data.remote.response

import com.google.gson.annotations.SerializedName
import java.io.Serializable

data class GetArticleResponse(

	@field:SerializedName("data")
	val data: DataArticles? = null,

	@field:SerializedName("message")
	val message: String? = null,

	@field:SerializedName("status")
	val status: String? = null
)

data class DataArticles(

	@field:SerializedName("results")
	val results: List<ResultsItem?>? = null
)

data class ResultsItem(

	@field:SerializedName("user_id")
	val userId: String? = null,

	@field:SerializedName("image_url")
	val imageUrl: String? = null,

	@field:SerializedName("description")
	val description: String? = null,

	@field:SerializedName("created_at")
	val createdAt: String? = null,

	@field:SerializedName("id")
	val id: Int? = null,

	@field:SerializedName("email")
	val email: String? = null,

	@field:SerializedName("displayName")
	val displayName: String? = null,

	@field:SerializedName("title")
	val title: String? = null
) : Serializable
