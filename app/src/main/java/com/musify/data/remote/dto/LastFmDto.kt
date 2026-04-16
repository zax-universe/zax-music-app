package com.musify.data.remote.dto
import com.google.gson.annotations.SerializedName

data class LastFmSearchResponse(val results: LastFmResults? = null)
data class LastFmResults(@SerializedName("trackmatches") val trackMatches: LastFmTrackMatches? = null)
data class LastFmTrackMatches(val track: List<LastFmTrack>? = null)
data class LastFmTrack(
    val mbid: String = "", val name: String = "", val artist: String = "",
    val url: String = "", val image: List<LastFmImage>? = null
)
data class LastFmImage(@SerializedName("#text") val url: String = "", val size: String = "")

fun LastFmTrack.toTrack() = com.musify.data.model.Track(
    id = "lastfm_$mbid", title = name, artist = artist,
    albumArtUrl = image?.firstOrNull { it.size == "large" }?.url, source = "LastFM"
)
