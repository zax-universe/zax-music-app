package com.musify.di

import android.content.Context
import com.musify.data.local.AppDatabase
import com.musify.data.local.dao.*
import com.musify.data.remote.api.*
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit
import javax.inject.Named
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides @Singleton
    fun provideOkHttpClient(): OkHttpClient = OkHttpClient.Builder()
        .connectTimeout(30, TimeUnit.SECONDS)
        .readTimeout(30, TimeUnit.SECONDS)
        .addInterceptor(HttpLoggingInterceptor().apply { level = HttpLoggingInterceptor.Level.BODY })
        .build()

    @Provides @Singleton @Named("deezer")
    fun provideDeezerRetrofit(client: OkHttpClient): Retrofit = Retrofit.Builder()
        .baseUrl("https://api.deezer.com/")
        .client(client).addConverterFactory(GsonConverterFactory.create()).build()

    @Provides @Singleton @Named("jamendo")
    fun provideJamendoRetrofit(client: OkHttpClient): Retrofit = Retrofit.Builder()
        .baseUrl("https://api.jamendo.com/v3.0/")
        .client(client).addConverterFactory(GsonConverterFactory.create()).build()

    @Provides @Singleton @Named("lastfm")
    fun provideLastFmRetrofit(client: OkHttpClient): Retrofit = Retrofit.Builder()
        .baseUrl("https://ws.audioscrobbler.com/2.0/")
        .client(client).addConverterFactory(GsonConverterFactory.create()).build()

    @Provides @Singleton
    fun provideDeezerApi(@Named("deezer") retrofit: Retrofit): DeezerApi = retrofit.create(DeezerApi::class.java)

    @Provides @Singleton
    fun provideJamendoApi(@Named("jamendo") retrofit: Retrofit): JamendoApi = retrofit.create(JamendoApi::class.java)

    @Provides @Singleton
    fun provideLastFmApi(@Named("lastfm") retrofit: Retrofit): LastFmApi = retrofit.create(LastFmApi::class.java)

    @Provides @Singleton
    fun provideDatabase(@ApplicationContext context: Context): AppDatabase = AppDatabase.getInstance(context)

    @Provides fun provideUserDao(db: AppDatabase): UserDao = db.userDao()
    @Provides fun provideSessionDao(db: AppDatabase): SessionDao = db.sessionDao()
    @Provides fun providePlaylistDao(db: AppDatabase): PlaylistDao = db.playlistDao()
    @Provides fun provideDownloadDao(db: AppDatabase): DownloadDao = db.downloadDao()
    @Provides fun provideHistoryDao(db: AppDatabase): HistoryDao = db.historyDao()
    @Provides fun provideSearchHistoryDao(db: AppDatabase): SearchHistoryDao = db.searchHistoryDao()
}
