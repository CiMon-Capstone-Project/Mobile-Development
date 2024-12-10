package com.example.cimon_chilimonitoring.data.remote.response

import kotlinx.parcelize.Parcelize
import android.os.Parcelable
import com.google.gson.annotations.SerializedName

@Parcelize
data class SaveDetectionResponse(

	@field:SerializedName("data")
	val data: DataDetection? = null,

	@field:SerializedName("message")
	val message: String? = null,

	@field:SerializedName("status")
	val status: String? = null
) : Parcelable

@Parcelize
data class DataDetection(

	@field:SerializedName("results")
	val results: List<ResultsItem?>? = null
) : Parcelable

@Parcelize
data class ResultsItemDetection(

	@field:SerializedName("treatment")
	val treatment: String? = null,

	@field:SerializedName("symptom")
	val symptom: String? = null,

	@field:SerializedName("disease")
	val disease: String? = null,

	@field:SerializedName("image_url")
	val imageUrl: String? = null,

	@field:SerializedName("displayName")
	val displayName: String? = null,

	@field:SerializedName("confidence")
	val confidence: Float? = null,

	@field:SerializedName("treatment_id")
	val treatmentId: Int? = null,

	@field:SerializedName("created_at")
	val createdAt: String? = null,

	@field:SerializedName("id")
	val id: Int? = null,

	@field:SerializedName("email")
	val email: String? = null,

	@field:SerializedName("prevention")
	val prevention: String? = null
) : Parcelable
