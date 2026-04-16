package com.musify.data.remote.dto
import com.google.gson.annotations.SerializedName

data class JamendoSearchResponse(val results: List<JamendoTrack>? = null)
data class JamendoTrack(
    val id: String = "",
    val name: String = "",
    @SerializedName("artist_name") val artistName: String = "",
    @SerializedName("album_name") val albumName: String = "",
    @SerializedName("image") val imageUrl: String? = null,
    @SerializedName("audio") val audioUrl: String? = null,
    val duration: Int = 0
)

fun JamendoTrack.toTrack() = com.musify.data.model.Track(
    id = "jamendo_$id", title = name, artist = artistName, album = albumName,
    albumArtUrl = imageUrl, previewUrl = audioUrl, duration = duration, source = "Jamendo"
)
