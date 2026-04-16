package com.musify.ui.player

import android.content.ComponentName
import android.content.Intent
import android.content.ServiceConnection
import android.os.Bundle
import android.os.IBinder
import android.widget.SeekBar
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.musify.R
import com.musify.databinding.ActivityPlayerBinding
import com.musify.service.MusicService
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class PlayerActivity : AppCompatActivity() {

    private lateinit var binding: ActivityPlayerBinding
    private var musicService: MusicService? = null
    private var isBound = false

    private val serviceConnection = object : ServiceConnection {
        override fun onServiceConnected(name: ComponentName?, binder: IBinder?) {
            musicService = (binder as? MusicService.LocalBinder)?.getService()
            isBound = true
            setupObservers()
        }
        override fun onServiceDisconnected(name: ComponentName?) {
            musicService = null
            isBound = false
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPlayerBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val trackId = intent.getStringExtra("TRACK_ID") ?: ""
        val title = intent.getStringExtra("TRACK_TITLE") ?: "Unknown"
        val artist = intent.getStringExtra("TRACK_ARTIST") ?: "Unknown"
        val url = intent.getStringExtra("TRACK_URL")
        val artUrl = intent.getStringExtra("TRACK_ART")

        binding.tvTrackTitle.text = title
        binding.tvArtistName.text = artist
        Glide.with(this).load(artUrl).placeholder(R.drawable.ic_musify_logo)
            .into(binding.ivAlbumArt)
        Glide.with(this).load(artUrl).placeholder(R.drawable.ic_musify_logo)
            .into(binding.ivBackgroundBlur)

        bindService(Intent(this, MusicService::class.java), serviceConnection, BIND_AUTO_CREATE)

        if (url != null) {
            startService(Intent(this, MusicService::class.java).apply {
                putExtra("TRACK_ID", trackId); putExtra("TITLE", title)
                putExtra("ARTIST", artist); putExtra("URL", url); putExtra("ART", artUrl)
            })
        }

        setupControls()
    }

    private fun setupControls() {
        binding.btnCollapse.setOnClickListener { finish() }

        binding.btnPlayPause.setOnClickListener { musicService?.togglePlayPause() }
        binding.btnNext.setOnClickListener { musicService?.skipNext() }
        binding.btnPrevious.setOnClickListener { musicService?.skipPrevious() }
        binding.btnShuffle.setOnClickListener { musicService?.toggleShuffle() }
        binding.btnRepeat.setOnClickListener { musicService?.toggleRepeat() }

        binding.btnLike.setOnClickListener {
            val liked = binding.btnLike.tag == "liked"
            if (liked) {
                binding.btnLike.setImageResource(R.drawable.ic_heart_outline)
                binding.btnLike.setColorFilter(resources.getColor(R.color.not_liked, null))
                binding.btnLike.tag = "not_liked"
            } else {
                binding.btnLike.setImageResource(R.drawable.ic_heart_filled)
                binding.btnLike.setColorFilter(resources.getColor(R.color.liked, null))
                binding.btnLike.tag = "liked"
            }
        }

        binding.seekBar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(sb: SeekBar?, progress: Int, fromUser: Boolean) {
                if (fromUser) musicService?.seekTo(progress)
            }
            override fun onStartTrackingTouch(sb: SeekBar?) {}
            override fun onStopTrackingTouch(sb: SeekBar?) {}
        })
    }

    private fun setupObservers() {
        musicService?.isPlaying?.observe(this) { playing ->
            binding.btnPlayPause.setImageResource(if (playing) R.drawable.ic_pause else R.drawable.ic_play_circle)
        }
        musicService?.progress?.observe(this) { progress ->
            binding.seekBar.progress = progress
        }
        musicService?.currentTimeStr?.observe(this) { time -> binding.tvCurrentTime.text = time }
        musicService?.durationStr?.observe(this) { dur -> binding.tvDuration.text = dur }
    }

    override fun onDestroy() {
        super.onDestroy()
        if (isBound) unbindService(serviceConnection)
    }
}
