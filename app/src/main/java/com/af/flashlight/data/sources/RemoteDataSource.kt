package com.af.flashlight.data.sources

import com.af.flashlight.data.remote.api.ApiService
import com.af.flashlight.utils.SpManager
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class RemoteDataSource @Inject constructor(
    private val apiService: ApiService,
    private val spManager: SpManager
) {

//    suspend fun login(request: LoginRequest) = apiService.login(request)
//
//    suspend fun refreshToken(request: RefreshTokenRequest) = apiService.refreshToken(request)
//
//    suspend fun updateLanguage(request: UpdateLanguageRequest) = apiService.updateLanguage(request)
//
//    suspend fun getAll() =
//        apiService.getAll(spManager.getLanguage().languageCode)
//
//    suspend fun getCategories() =
//        apiService.getCategories(spManager.getLanguage().languageCode)
//
//    suspend fun getMovieByCategory(categoryId: Int, page: Int) =
//        apiService.getMovieByCategory(spManager.getLanguage().languageCode, categoryId, page)
//
//    suspend fun getMovieDetail(dramaId: Int) =
//        apiService.getMovieDetail(spManager.getLanguage().languageCode, dramaId)
//
//    suspend fun searchMovie(keyword: String, page: Int) =
//        apiService.searchMovie(spManager.getLanguage().languageCode, keyword, page)
//
//    suspend fun getTrendingMovie(page: Int) =
//        apiService.getTrendingMovie(spManager.getLanguage().languageCode, page)
//
//    suspend fun getForYouMovies() =
//        apiService.getForYouMovies(spManager.getLanguage().languageCode)
//
//    suspend fun getTopFilms(type: String) =
//        apiService.getTopFilms(spManager.getLanguage().languageCode, type)
//
//    suspend fun getNewRelease() =
//        apiService.getNewRelease(spManager.getLanguage().languageCode)
}
