package com.hdev.gnews.data.source.remote

import com.hdev.gnews.data.source.remote.dto.NewsResponseDto
import com.hdev.gnews.data.source.remote.dto.SourcesResponseDto
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface ApiService {

    @GET("v2/top-headlines")
    suspend fun getTopHeadline(
        @Query("country") country: String = "us",
        @Query("category") category: String? = null,
        @Query("page") page : Int = 1,
        @Query("pageSize") pageSize: Int = 20
    ) : Response<NewsResponseDto>

    @GET("v2/everything")
    suspend fun getEverything(
        @Query("q") query: String? = null,
        @Query("sources") sources: String? = null,
        @Query("sortBy") sortBy: String? = "publishedAt",
        @Query("language") language: String? = "en",
        @Query("page") page: Int = 1,
        @Query("pageSize") pageSize: Int = 20
    ): Response<NewsResponseDto>

    @GET("v2/top-headlines/sources")
    suspend fun getSources(
        @Query("category") category: String? = null,
        @Query("language") language: String? = "en",
        @Query("country") country: String? = "us"
    ) : Response<SourcesResponseDto>
}
