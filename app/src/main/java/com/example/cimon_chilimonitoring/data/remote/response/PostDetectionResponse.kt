package com.example.cimon_chilimonitoring.data.remote.response

import kotlinx.parcelize.Parcelize
import android.os.Parcelable
import com.google.gson.annotations.SerializedName

@Parcelize
data class PostDetectionResponse(

	@field:SerializedName("data")
	val data: DataPostDetection? = null,

	@field:SerializedName("message")
	val message: String? = null,

	@field:SerializedName("status")
	val status: String? = null
) : Parcelable

@Parcelize
data class DataPostDetection(

	@field:SerializedName("treatment")
	val treatment: String? = null,

	@field:SerializedName("symptom")
	val symptom: String? = null,

	@field:SerializedName("displayName")
	val displayName: String? = null,

	@field:SerializedName("image_url")
	val imageUrl: String? = null,

	@field:SerializedName("confidence")
	val confidence: String? = null,

	@field:SerializedName("treatment_id")
	val treatmentId: String? = null,

	@field:SerializedName("email")
	val email: String? = null,

	@field:SerializedName("prevention")
	val prevention: String? = null
) : Parcelable
