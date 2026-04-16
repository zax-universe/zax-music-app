package com.musify.ui.main

import android.content.ComponentName
import android.content.Intent
import android.content.ServiceConnection
import android.os.Bundle
import android.os.IBinder
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupWithNavController
import com.bumptech.glide.Glide
import com.musify.R
import com.musify.databinding.ActivityMainBinding
import com.musify.service.MusicService
import com.musify.ui.player.PlayerActivity
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var navController: NavController
    private var musicService: MusicService? = null

    private val serviceConnection = object : ServiceConnection {
        override fun onServiceConnected(name: ComponentName?, binder: IBinder?) {
            val localBinder = binder as? MusicService.LocalBinder
            musicService = localBinder?.getService()
            setupMiniPlayer()
        }
        override fun onServiceDisconnected(name: ComponentName?) { musicService = null }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val navHostFragment = supportFragmentManager
            .findFragmentById(R.id.navHostFragment) as NavHostFragment
        navController = navHostFragment.navController

        binding.bottomNavigationView.setupWithNavController(navController)

        startMusicService()
        setupMiniPlayerClicks()
    }

    private fun startMusicService() {
        val intent = Intent(this, MusicService::class.java)
        startService(intent)
        bindService(intent, serviceConnection, BIND_AUTO_CREATE)
    }

    private fun setupMiniPlayerClicks() {
        binding.miniPlayer.setOnClickListener {
            startActivity(Intent(this, PlayerActivity::class.java))
        }
        binding.ivMiniPlayPause.setOnClickListener {
            musicService?.togglePlayPause()
        }
    }

    private fun setupMiniPlayer() {
        musicService?.currentTrack?.observe(this) { track ->
            if (track != null) {
                binding.miniPlayer.visibility = View.VISIBLE
                binding.tvMiniTitle.text = track.title
                binding.tvMiniArtist.text = track.artist
                Glide.with(this).load(track.albumArtUrl)
                    .placeholder(R.drawable.ic_musify_logo)
                    .into(binding.ivMiniAlbumArt)
            } else {
                binding.miniPlayer.visibility = View.GONE
            }
        }
        musicService?.isPlaying?.observe(this) { playing ->
            binding.ivMiniPlayPause.setImageResource(
                if (playing) R.drawable.ic_pause else R.drawable.ic_play
            )
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        unbindService(serviceConnection)
    }
}
