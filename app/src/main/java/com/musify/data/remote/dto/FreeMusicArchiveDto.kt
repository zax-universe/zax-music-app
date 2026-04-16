package com.musify.data.remote.dto
import com.google.gson.annotations.SerializedName

data class FmaSearchResponse(val dataset: List<FmaTrack>? = null)
data class FmaTrack(
    @SerializedName("track_id") val id: String = "",
    @SerializedName("track_title") val title: String = "",
    @SerializedName("artist_name") val artistName: String = "",
    @SerializedName("album_title") val albumTitle: String = "",
    @SerializedName("track_image_file") val imageUrl: String? = null,
    @SerializedName("track_url") val trackUrl: String? = null
)

fun FmaTrack.toTrack() = com.musify.data.model.Track(
    id = "fma_$id", title = title, artist = artistName, album = albumTitle,
    albumArtUrl = imageUrl, previewUrl = trackUrl, source = "FMA"
)
