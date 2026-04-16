package com.musify.data.model

data class Track(
    val id: String,
    val title: String,
    val artist: String,
    val album: String = "",
    val albumArtUrl: String? = null,
    val previewUrl: String? = null,
    val duration: Int = 0,
    val isLiked: Boolean = false,
    val isDownloaded: Boolean = false,
    val source: String = ""
)
