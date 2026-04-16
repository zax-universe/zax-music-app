package com.musify.data.remote.api
import com.musify.data.remote.dto.LastFmSearchResponse
import retrofit2.http.GET
import retrofit2.http.Query

interface LastFmApi {
    @GET("?method=track.search&format=json")
    suspend fun search(
        @Query("track") query: String,
        @Query("api_key") apiKey: String = "36f6c5dc5cce4f0a29ce2a87e5e7a0a6",
        @Query("limit") limit: Int = 20
    ): LastFmSearchResponse
}
