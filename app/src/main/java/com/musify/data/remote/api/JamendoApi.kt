package com.musify.data.remote.api
import com.musify.data.remote.dto.JamendoSearchResponse
import retrofit2.http.GET
import retrofit2.http.Query

interface JamendoApi {
    @GET("tracks/")
    suspend fun search(
        @Query("client_id") clientId: String = "b6747d04",
        @Query("search") query: String,
        @Query("format") format: String = "json",
        @Query("limit") limit: Int = 20,
        @Query("include") include: String = "musicinfo"
    ): JamendoSearchResponse

    @GET("tracks/")
    suspend fun getTrending(
        @Query("client_id") clientId: String = "b6747d04",
        @Query("order") order: String = "popularity_total",
        @Query("format") format: String = "json",
        @Query("limit") limit: Int = 20
    ): JamendoSearchResponse
}
