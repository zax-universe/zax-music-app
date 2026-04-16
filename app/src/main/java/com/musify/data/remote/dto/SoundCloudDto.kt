package com.musify.data.remote.dto
import com.google.gson.annotations.SerializedName

data class SoundCloudSearchResponse(val collection: List<SoundCloudTrack>? = null)
data class SoundCloudTrack(
    val id: Long = 0, val title: String = "",
    val user: SoundCloudUser = SoundCloudUser(),
    @SerializedName("artwork_url") val artworkUrl: String? = null,
    @SerializedName("stream_url") val streamUrl: String? = null,
    val duration: Int = 0
)
data class SoundCloudUser(val id: Long = 0, val username: String = "")

fun SoundCloudTrack.toTrack() = com.musify.data.model.Track(
    id = "sc_$id", title = title, artist = user.username,
    albumArtUrl = artworkUrl, previewUrl = streamUrl, duration = duration / 1000, source = "SoundCloud"
)
