package com.musify.data.remote.api
import com.musify.data.remote.dto.DeezerSearchResponse
import retrofit2.http.GET
import retrofit2.http.Query

interface DeezerApi {
    @GET("search")
    suspend fun search(@Query("q") query: String, @Query("limit") limit: Int = 20): DeezerSearchResponse

    @GET("chart/0/tracks")
    suspend fun getTopTracks(@Query("limit") limit: Int = 20): DeezerSearchResponse
}
