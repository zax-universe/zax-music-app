package com.musify.worker

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.hilt.work.HiltWorker
import androidx.work.*
import com.musify.R
import com.musify.data.local.dao.DownloadDao
import com.musify.data.local.entity.DownloadEntity
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File
import java.net.URL

@HiltWorker
class DownloadWorker @AssistedInject constructor(
    @Assisted private val context: Context,
    @Assisted workerParams: WorkerParameters,
    private val downloadDao: DownloadDao
) : CoroutineWorker(context, workerParams) {

    override suspend fun doWork(): Result = withContext(Dispatchers.IO) {
        val trackId = inputData.getString("TRACK_ID") ?: return@withContext Result.failure()
        val title = inputData.getString("TITLE") ?: return@withContext Result.failure()
        val artist = inputData.getString("ARTIST") ?: ""
        val url = inputData.getString("URL") ?: return@withContext Result.failure()
        val artUrl = inputData.getString("ART_URL")

        try {
            createChannel()
            val notifManager = context.getSystemService(NotificationManager::class.java)
            val builder = NotificationCompat.Builder(context, "downloads")
                .setSmallIcon(R.drawable.ic_download)
                .setContentTitle("Downloading: $title")
                .setProgress(100, 0, false)
                .setOngoing(true)
            notifManager.notify(trackId.hashCode(), builder.build())

            val dir = File(context.filesDir, "downloads").apply { mkdirs() }
            val file = File(dir, "$trackId.mp3")
            val connection = URL(url).openConnection()
            val total = connection.contentLength.toLong()
            var downloaded = 0L
            connection.getInputStream().use { input ->
                file.outputStream().use { output ->
                    val buffer = ByteArray(8192)
                    var bytes: Int
                    while (input.read(buffer).also { bytes = it } != -1) {
                        output.write(buffer, 0, bytes)
                        downloaded += bytes
                        if (total > 0) {
                            val progress = (downloaded * 100 / total).toInt()
                            builder.setProgress(100, progress, false)
                            notifManager.notify(trackId.hashCode(), builder.build())
                        }
                    }
                }
            }

            downloadDao.insertDownload(DownloadEntity(
                trackId = trackId, title = title, artist = artist,
                albumArtUrl = artUrl, localPath = file.absolutePath, fileSize = file.length()
            ))

            builder.setContentText("Download complete").setProgress(0,0,false).setOngoing(false)
            notifManager.notify(trackId.hashCode(), builder.build())
            Result.success()
        } catch (e: Exception) {
            Result.failure()
        }
    }

    private fun createChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel("downloads", "Downloads", NotificationManager.IMPORTANCE_LOW)
            context.getSystemService(NotificationManager::class.java).createNotificationChannel(channel)
        }
    }
}
