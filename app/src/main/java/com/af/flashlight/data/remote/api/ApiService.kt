package com.af.flashlight.data.remote.api

import com.af.network.model.BaseResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Query

interface ApiService {

//    @POST("auth/login")
//    suspend fun login(@Body request: LoginRequest): Response<BaseResponse<LoginData>>
//
//    @POST("auth/refresh-token")
//    suspend fun refreshToken(@Body request: RefreshTokenRequest): Response<BaseResponse<RefreshTokenData>>
//
//    @POST("user/update-language")
//    suspend fun updateLanguage(@Body request: UpdateLanguageRequest): Response<BaseResponse<UserModel>>
//
//    @GET("dramas/load-all")
//    suspend fun getAll(
//        @Query("lang_code") languageCode: String
//    ): Response<BaseResponse<List<HomeSection>>>
//
//    @GET("categories")
//    suspend fun getCategories(
//        @Query("lang_code") languageCode: String
//    ): Response<BaseResponse<CategoryData>>
//
//    @GET("dramas/by-category")
//    suspend fun getMovieByCategory(
//        @Query("lang_code") languageCode: String,
//        @Query("cate_id") categoryId: Int,
//        @Query("page") page: Int
//    ): Response<BaseResponse<DramaData>>

}
