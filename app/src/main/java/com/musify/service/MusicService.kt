package com.musify.service

import android.app.*
import android.content.Intent
import android.os.Binder
import android.os.Build
import android.os.IBinder
import androidx.core.app.NotificationCompat
import androidx.lifecycle.MutableLiveData
import androidx.media3.common.MediaItem
import androidx.media3.common.Player
import androidx.media3.exoplayer.ExoPlayer
import com.musify.R
import com.musify.data.model.Track
import com.musify.data.repository.MusicRepository
import com.musify.ui.player.PlayerActivity
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.*
import javax.inject.Inject

@AndroidEntryPoint
class MusicService : Service() {

    @Inject lateinit var musicRepository: MusicRepository

    private val binder = LocalBinder()
    private var exoPlayer: ExoPlayer? = null
    private val scope = CoroutineScope(Dispatchers.Main + SupervisorJob())

    val currentTrack = MutableLiveData<Track?>()
    val isPlaying = MutableLiveData(false)
    val progress = MutableLiveData(0)
    val currentTimeStr = MutableLiveData("0:00")
    val durationStr = MutableLiveData("0:00")
    val isShuffle = MutableLiveData(false)
    val isRepeat = MutableLiveData(false)

    inner class LocalBinder : Binder() {
        fun getService(): MusicService = this@MusicService
    }

    override fun onCreate() {
        super.onCreate()
        createNotificationChannel()
        exoPlayer = ExoPlayer.Builder(this).build().apply {
            addListener(object : Player.Listener {
                override fun onIsPlayingChanged(playing: Boolean) {
                    isPlaying.value = playing
                    updateNotification()
                }
                override fun onPlaybackStateChanged(state: Int) {
                    if (state == Player.STATE_ENDED) {
                        if (isRepeat.value == true) exoPlayer?.seekTo(0)
                    }
                }
            })
        }
        startProgressUpdater()
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        intent?.let {
            val trackId = it.getStringExtra("TRACK_ID") ?: ""
            val title = it.getStringExtra("TITLE") ?: ""
            val artist = it.getStringExtra("ARTIST") ?: ""
            val url = it.getStringExtra("URL")
            val art = it.getStringExtra("ART")
            if (url != null) {
                val track = Track(trackId, title, artist, albumArtUrl = art, previewUrl = url)
                playTrack(track)
            }
        }
        return START_NOT_STICKY
    }

    fun playTrack(track: Track) {
        currentTrack.value = track
        track.previewUrl?.let { url ->
            exoPlayer?.setMediaItem(MediaItem.fromUri(url))
            exoPlayer?.prepare()
            exoPlayer?.play()
        }
        scope.launch { musicRepository.addToHistory(track) }
        startForeground(1, buildNotification(track))
    }

    fun togglePlayPause() {
        if (exoPlayer?.isPlaying == true) exoPlayer?.pause() else exoPlayer?.play()
    }

    fun skipNext() { /* Queue handling */ }
    fun skipPrevious() { exoPlayer?.seekTo(0) }
    fun toggleShuffle() { isShuffle.value = !(isShuffle.value ?: false) }
    fun toggleRepeat() { isRepeat.value = !(isRepeat.value ?: false) }

    fun seekTo(progressPercent: Int) {
        val duration = exoPlayer?.duration ?: 0L
        exoPlayer?.seekTo((duration * progressPercent / 100))
    }

    private fun startProgressUpdater() {
        scope.launch {
            while (true) {
                delay(500)
                val player = exoPlayer ?: continue
                val dur = player.duration.coerceAtLeast(1L)
                val pos = player.currentPosition
                progress.value = (pos * 100 / dur).toInt()
                currentTimeStr.value = formatTime(pos)
                durationStr.value = formatTime(dur)
            }
        }
    }

    private fun formatTime(ms: Long): String {
        val totalSec = ms / 1000
        return "%d:%02d".format(totalSec / 60, totalSec % 60)
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel("playback", "Music Playback", NotificationManager.IMPORTANCE_LOW).apply {
                description = "Music playback controls"
                setSound(null, null)
            }
            getSystemService(NotificationManager::class.java).createNotificationChannel(channel)
        }
    }

    private fun buildNotification(track: Track): Notification {
        val intent = Intent(this, PlayerActivity::class.java)
        val pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_IMMUTABLE)
        return NotificationCompat.Builder(this, "playback")
            .setContentTitle(track.title)
            .setContentText(track.artist)
            .setSmallIcon(R.drawable.ic_musify_logo)
            .setContentIntent(pendingIntent)
            .setOngoing(true)
            .setSilent(true)
            .build()
    }

    private fun updateNotification() {
        currentTrack.value?.let {
            getSystemService(NotificationManager::class.java)
                .notify(1, buildNotification(it))
        }
    }

    override fun onBind(intent: Intent): IBinder = binder

    override fun onDestroy() {
        super.onDestroy()
        exoPlayer?.release()
        scope.cancel()
    }
}
