package com.example.rageamp.service

import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Intent
import android.os.Binder
import android.os.Build
import android.os.IBinder
import android.support.v4.media.session.MediaSessionCompat
import androidx.core.app.NotificationCompat
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import androidx.media3.common.MediaItem
import androidx.media3.common.Player
import androidx.media3.exoplayer.ExoPlayer
import com.example.rageamp.R
import com.example.rageamp.broadcast_receive.ActionReceive
import com.example.rageamp.data.data_source.pref.SharedPreferencesManager
import com.example.rageamp.data.model.Song
import com.example.rageamp.ui.main.MainActivity
import com.example.rageamp.utils.GlideUtils
import com.example.rageamp.utils.Logger
import com.example.rageamp.utils.MEDIA_SESSION_COMPAT
import com.example.rageamp.utils.MUSIC_ACTION
import com.example.rageamp.utils.MUSIC_ACTION_SERVICE
import com.example.rageamp.utils.MUSIC_CHANNEL_ID
import com.example.rageamp.utils.NOTIFICATION_ID
import com.example.rageamp.utils.REPEAT_MODE
import com.example.rageamp.utils.SEND_ACTION_TO_ACTIVITY
import com.example.rageamp.utils.SHUFFLE_MODE
import com.example.rageamp.utils.SONG_OBJECT
import com.example.rageamp.utils.enums.MusicAction
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class MusicService : Service() {
	@Inject
	lateinit var exoPlayer: ExoPlayer
	
	@Inject
	lateinit var sharedPreferencesManager: SharedPreferencesManager
	
	private val mediaSession: MediaSessionCompat by lazy {
		MediaSessionCompat(this, MEDIA_SESSION_COMPAT)
	}
	
	private var currentSong: Song? = null
	private var songs = listOf<Song>()
	
	private val binder = MusicBinder()
	
	inner class MusicBinder : Binder() {
		fun getService(): MusicService = this@MusicService
	}
	
	override fun onCreate() {
		super.onCreate()
		exoPlayer.apply {
			repeatMode = sharedPreferencesManager.get(REPEAT_MODE, Player.REPEAT_MODE_OFF)
			shuffleModeEnabled = sharedPreferencesManager.get(SHUFFLE_MODE, false)
		}
		initListener()
	}
	
	override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
		Logger.v("onStartCommand: ------------")
		val song = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
			intent?.getSerializableExtra(SONG_OBJECT, Song::class.java)
		} else {
			@Suppress("DEPRECATION")
			intent?.getSerializableExtra(SONG_OBJECT) as? Song
		}
		song?.let {
			if (currentSong == null || currentSong != it) {
				currentSong = it
				startMusic(it)
			} else if (!exoPlayer.isPlaying) {
				resumeMusic()
			}
		}
		
		val actionReceive = intent?.getIntExtra(MUSIC_ACTION_SERVICE, -1)
		handleMusicAction(actionReceive)
		return START_NOT_STICKY
	}
	
	override fun onBind(intent: Intent?): IBinder {
		return binder
	}
	
	override fun onUnbind(intent: Intent?): Boolean {
		return super.onUnbind(intent)
	}
	
	override fun onDestroy() {
		super.onDestroy()
		exoPlayer.release()
		
		// Cancel Notification when close app
		val notificationManager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager
		notificationManager.cancel(NOTIFICATION_ID)
		
	}
	
	private fun initListener() {
		exoPlayer.addListener(object : Player.Listener {
			override fun onPlaybackStateChanged(playbackState: Int) {
				super.onPlaybackStateChanged(playbackState)
				if (playbackState == Player.STATE_ENDED) {
					onCompletionListener()
				}
			}
			
			override fun onIsPlayingChanged(isPlaying: Boolean) {
				Logger.i("onIsPlayingChanged: $isPlaying")
				sendNotification()
				super.onIsPlayingChanged(isPlaying)
				
			}
		})
	}
	
	fun onCompletionListener() {
		val currentIndex = currentSong?.let { song ->
			songs.indexOfFirst { it.songId == song.songId }
		} ?: -1
		
		when (exoPlayer.repeatMode) {
			Player.REPEAT_MODE_OFF -> {
				if (currentIndex == songs.lastIndex && !exoPlayer.shuffleModeEnabled) {
					pauseMusic()
				} else {
					nextMusic()
				}
			}
			
			Player.REPEAT_MODE_ONE -> {
				currentSong?.let {
					startMusic(it)
				}
			}
			
			Player.REPEAT_MODE_ALL -> {
				nextMusic()
			}
		}
	}
	
	private fun sendNotification() {
		Logger.i("sendNotification----------")
		val notificationIntent = Intent(this, MainActivity::class.java).apply {
			flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP
		}
		val pendingIntent = PendingIntent.getActivity(
			this, 0,
			notificationIntent, PendingIntent.FLAG_IMMUTABLE
		)
		
		val builder = NotificationCompat.Builder(this, MUSIC_CHANNEL_ID)
			.setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
			.setContentTitle(currentSong?.title)
			.setContentText(currentSong?.artist)
			.setSmallIcon(R.drawable.ic_music_note)
			.setContentIntent(pendingIntent)
			.setStyle(
				androidx.media.app.NotificationCompat.MediaStyle()
					.setShowActionsInCompactView(0, 1, 2)
					.setMediaSession(mediaSession.sessionToken)
			)
			.setOngoing(true)
			.setSound(null)
		
		builder.addAction(
			R.drawable.ic_previous,
			MusicAction.PREVIOUS.actionName,
			getPendingIntent(MusicAction.PREVIOUS.action)
		)
			.addAction(
				if (exoPlayer.isPlaying) R.drawable.ic_pause else R.drawable.ic_play,
				if (exoPlayer.isPlaying) MusicAction.PAUSE.actionName else MusicAction.RESUME.actionName,
				if (exoPlayer.isPlaying) getPendingIntent(MusicAction.PAUSE.action)
				else getPendingIntent(MusicAction.RESUME.action)
			)
			.addAction(
				R.drawable.ic_next,
				MusicAction.NEXT.actionName,
				getPendingIntent(MusicAction.NEXT.action)
			)
		
		GlideUtils.loadImageFromUrlWithCallback(
			context = applicationContext,
			url = currentSong?.albumArt,
			onFinished = {
				builder.setLargeIcon(it)
				startForeground(NOTIFICATION_ID, builder.build())
			})
	}
	
	private fun startMusic(song: Song) {
		val mediaItem = MediaItem.fromUri(song.data.toString())
		exoPlayer.setMediaItem(mediaItem)
		exoPlayer.prepare()
		exoPlayer.play()
		sendActionToActivity(MusicAction.START.action)
	}
	
	private fun pauseMusic() {
		if (exoPlayer.isPlaying) {
			exoPlayer.pause()
		}
		sendActionToActivity(MusicAction.PAUSE.action)
	}
	
	private fun resumeMusic() {
		if (!exoPlayer.isPlaying) {
			exoPlayer.play()
		}
		sendActionToActivity(MusicAction.RESUME.action)
	}
	
	private fun nextMusic() {
		if (songs.isEmpty()) {
			return
		}
		
		val currentIndex = currentSong?.let { song ->
			songs.indexOfFirst { it.songId == song.songId }
		} ?: 0
		val nextIndex = when (exoPlayer.shuffleModeEnabled) {
			false -> (currentIndex + 1) % (songs.size)
			true -> {
				var randomIndex = (0 until (songs.size)).random()
				while (randomIndex == currentIndex) {
					randomIndex = (0 until (songs.size)).random()
				}
				randomIndex
			}
		}
		currentSong = songs[nextIndex]
		currentSong?.let {
			startMusic(it)
		}
		//sendActionToActivity(ActionMusic.NEXT.action)
	}
	
	private fun previousMusic() {
		if (songs.isEmpty()) {
			return
		}
		
		val currentIndex = currentSong?.let { song ->
			songs.indexOfFirst { it.songId == song.songId }
		} ?: -1
		val prevIndex = when (exoPlayer.shuffleModeEnabled) {
			false -> (currentIndex - 1 + songs.size) % songs.size
			true -> {
				var randomIndex = (0 until (songs.size)).random()
				while (randomIndex == currentIndex) {
					randomIndex = (0 until (songs.size)).random()
				}
				randomIndex
			}
		}
		currentSong = songs[prevIndex]
		currentSong?.let {
			startMusic(it)
		}
		//sendActionToActivity(ActionMusic.PREVIOUS.action)
	}
	
	private fun rewindMusic(action: Int) {
		val currentPosition = exoPlayer.currentPosition
		val newPosition = when (action) {
			MusicAction.REWIND_BACK.action -> (currentPosition - REWIND_TIME).coerceAtLeast(0)
			else -> (currentPosition + REWIND_TIME).coerceAtMost(exoPlayer.duration)
		}
		exoPlayer.seekTo(newPosition)
	}
	
	private fun updateRepeatMode() {
		val repeatMode = when (exoPlayer.repeatMode) {
			Player.REPEAT_MODE_OFF -> Player.REPEAT_MODE_ONE
			Player.REPEAT_MODE_ONE -> Player.REPEAT_MODE_ALL
			Player.REPEAT_MODE_ALL -> Player.REPEAT_MODE_OFF
			else -> {
				Player.REPEAT_MODE_OFF
			}
		}
		exoPlayer.repeatMode = repeatMode
		sharedPreferencesManager.put(REPEAT_MODE, repeatMode)
		sendActionToActivity(MusicAction.REPEAT.action)
	}
	
	private fun updateShuffleMode() {
		exoPlayer.shuffleModeEnabled = !exoPlayer.shuffleModeEnabled
		sharedPreferencesManager.put(SHUFFLE_MODE, exoPlayer.shuffleModeEnabled)
		sendActionToActivity(MusicAction.SHUFFLE.action)
	}
	
	private fun handleMusicAction(action: Int?) {
		Logger.i("handleMusicAction: action: $action")
		when (action) {
			MusicAction.PAUSE.action -> {
				pauseMusic()
			}
			
			MusicAction.RESUME.action -> {
				resumeMusic()
			}
			
			MusicAction.NEXT.action -> {
				nextMusic()
			}
			
			MusicAction.PREVIOUS.action -> {
				previousMusic()
			}
			
			MusicAction.REWIND_BACK.action -> {
				rewindMusic(MusicAction.REWIND_BACK.action)
			}
			
			MusicAction.REWIND_FORWARD.action -> {
				rewindMusic(MusicAction.REWIND_FORWARD.action)
			}
			
			MusicAction.REPEAT.action -> {
				updateRepeatMode()
			}
			
			MusicAction.SHUFFLE.action -> {
				updateShuffleMode()
			}
		}
	}
	
	private fun getPendingIntent(action: Int): PendingIntent {
		val intent = Intent(this, ActionReceive::class.java).apply {
			putExtra(MUSIC_ACTION, action)
		}
		
		return PendingIntent.getBroadcast(
			applicationContext,
			action,
			intent,
			PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
		)
	}
	
	private fun sendActionToActivity(action: Int) {
		val intent = Intent(SEND_ACTION_TO_ACTIVITY).apply {
			putExtra(MUSIC_ACTION, action)
			putExtra(SONG_OBJECT, currentSong)
			//putExtra(IS_PLAYING_STATUS, exoPlayer.isPlaying)
			putExtra(SHUFFLE_MODE, exoPlayer.shuffleModeEnabled)
			putExtra(REPEAT_MODE, exoPlayer.repeatMode)
		}
		LocalBroadcastManager.getInstance(this).sendBroadcast(intent)
	}
	
	fun getCurrentSongIndex(): Int {
		return songs.indexOfFirst { it.songId == currentSong?.songId }
	}
	
	fun setCurrentSongs(songs: List<Song>) {
		this.songs = songs
	}
	
	companion object {
		private const val REWIND_TIME = 5000
	}
}
