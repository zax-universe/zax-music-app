package com.musify.data.remote.dto
import com.google.gson.annotations.SerializedName

data class DeezerSearchResponse(val data: List<DeezerTrack>? = null, val total: Int = 0)
data class DeezerTrack(
    val id: Long = 0,
    val title: String = "",
    val artist: DeezerArtist = DeezerArtist(),
    val album: DeezerAlbum = DeezerAlbum(),
    @SerializedName("preview") val previewUrl: String? = null,
    val duration: Int = 0
)
data class DeezerArtist(val id: Long = 0, val name: String = "")
data class DeezerAlbum(val id: Long = 0, val title: String = "", @SerializedName("cover_medium") val coverMedium: String? = null)

fun DeezerTrack.toTrack() = com.musify.data.model.Track(
    id = "deezer_$id", title = title, artist = artist.name,
    album = album.title, albumArtUrl = album.coverMedium, previewUrl = previewUrl,
    duration = duration, source = "Deezer"
)
