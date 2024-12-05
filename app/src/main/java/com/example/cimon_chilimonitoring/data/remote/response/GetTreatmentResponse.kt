package com.example.cimon_chilimonitoring.data.remote.response

import com.google.gson.annotations.SerializedName

data class GetTreatmentResponse(

	@field:SerializedName("data")
	val data: DataTreatment? = null,

	@field:SerializedName("message")
	val message: String? = null,

	@field:SerializedName("status")
	val status: String? = null
)

data class DataTreatment(

	@field:SerializedName("treatments")
	val treatments: Treatments? = null
)

data class Treatments(

	@field:SerializedName("treatment")
	val treatment: String? = null,

	@field:SerializedName("symptom")
	val symptom: String? = null,

	@field:SerializedName("disease")
	val disease: String? = null,

	@field:SerializedName("treatment_id")
	val treatmentId: Int? = null,

	@field:SerializedName("created_at")
	val createdAt: String? = null,

	@field:SerializedName("id")
	val id: Int? = null,

	@field:SerializedName("prevention")
	val prevention: String? = null
)
