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

	@field:SerializedName("disease")
	val disease: String? = null,

	@field:SerializedName("user_id")
	val userId: String? = null,

	@field:SerializedName("image_url")
	val imageUrl: String? = null,

	@field:SerializedName("confidence")
	val confidence: String? = null,

	@field:SerializedName("treatment_id")
	val treatmentId: String? = null
) : Parcelable
