package com.example.cimon_chilimonitoring.data.remote.retrofit

import com.example.cimon_chilimonitoring.data.remote.response.GetArticleResponse
import com.example.cimon_chilimonitoring.data.remote.response.GetBlogResponse
import com.example.cimon_chilimonitoring.data.remote.response.GetHistoryResponse
import com.example.cimon_chilimonitoring.data.remote.response.GetTreatmentResponse
import com.example.cimon_chilimonitoring.data.remote.response.LoginResponse
import com.example.cimon_chilimonitoring.data.remote.response.PostArticlesResponse
import com.example.cimon_chilimonitoring.data.remote.response.PostDetectionResponse
import com.example.cimon_chilimonitoring.data.remote.response.RegisterResponse
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.http.DELETE
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.GET
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Part
import retrofit2.http.Path
import retrofit2.http.Query

interface ApiService {

    // register
    @FormUrlEncoded
    @POST("register")
    suspend fun register(
        @Field("name") name: String,
        @Field("email") email: String,
        @Field("password") password: String
    ): RegisterResponse

    // login
    @FormUrlEncoded
    @POST("login")
    suspend fun login(
        @Field("email") email: String,
        @Field("password") password: String
    ): LoginResponse

    // getter
    @GET("articles")
    suspend fun getArticles(
    ): GetArticleResponse

    // blog
    @GET("blog")
    suspend fun getBlog(
    ): GetBlogResponse

    // treatment
    @GET("treatment/{id}")
    suspend fun getTreatments(
        @Path("id") id: Int
    ): GetTreatmentResponse

    // add articles
    @Multipart
    @POST("articles")
    suspend fun postArticle(
        @Part("title") title: RequestBody,
        @Part("description") description: RequestBody,
        @Part file: MultipartBody.Part
    ): PostArticlesResponse

    // update articles
    @Multipart
    @PUT("articles/{id}")
    suspend fun updateArticle(
        @Path("id") id: Int,
        @Part("title") title: RequestBody,
        @Part("description") description: RequestBody,
        @Part file: MultipartBody.Part
    ): PostArticlesResponse

    // delete
    @DELETE("articles/{id}")
    suspend fun deleteArticle(@Path("id") id: Int)

    // post Detection
    @Multipart
    @POST("detection")
    suspend fun postDetection(
        @Part("confidence") confidence: RequestBody,
        @Part("disease") disease: RequestBody,
        @Part("treatment_id") treatment: RequestBody,
        @Part file: MultipartBody.Part
    ): PostDetectionResponse

    @GET("detection")
    suspend fun getHistory(
        @Query("page") page: Int
    ): GetHistoryResponse
}