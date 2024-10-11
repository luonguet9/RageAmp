package com.example.rageamp.ui.main

import android.content.BroadcastReceiver
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.ServiceConnection
import android.os.IBinder
import androidx.activity.viewModels
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.example.rageamp.R
import com.example.rageamp.base.BaseActivity
import com.example.rageamp.data.model.Song
import com.example.rageamp.databinding.ActivityMainBinding
import com.example.rageamp.service.MusicService
import com.example.rageamp.ui.SharedViewModel
import com.example.rageamp.utils.ACTION_CHANGE_THEME
import com.example.rageamp.utils.BLUE_THEME
import com.example.rageamp.utils.GREEN_THEME
import com.example.rageamp.utils.Logger
import com.example.rageamp.utils.MUSIC_ACTION
import com.example.rageamp.utils.MUSIC_ACTION_SERVICE
import com.example.rageamp.utils.RED_THEME
import com.example.rageamp.utils.REPEAT_MODE
import com.example.rageamp.utils.SEND_ACTION_TO_ACTIVITY
import com.example.rageamp.utils.SHUFFLE_MODE
import com.example.rageamp.utils.SONG_OBJECT
import com.example.rageamp.utils.THEME
import com.example.rageamp.utils.enums.MusicAction
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class MainActivity : BaseActivity<ActivityMainBinding>() {
	/**Music Service*/
	var musicService: MusicService? = null
	private var isServiceConnected = false
	private val serviceConnection = object : ServiceConnection {
		override fun onServiceConnected(name: ComponentName?, service: IBinder?) {
			Logger.v(TAG, "onServiceConnected: ------------")
			val binder = service as MusicService.MusicBinder
			musicService = binder.getService()
			isServiceConnected = true
			lifecycleScope.launch {
				sharedViewModel.currentSongs.collect {songs->
					Logger.i(TAG, "currentSongs: $songs")
					musicService?.setCurrentSongs(songs)
				}
			}
		}
		
		override fun onServiceDisconnected(name: ComponentName?) {
			Logger.v(TAG, "onServiceDisconnected: ------------")
			musicService = null
			isServiceConnected = false
		}
		
	}
	private val musicActionReceiver = object : BroadcastReceiver() {
		override fun onReceive(context: Context?, intent: Intent?) {
			intent?.getIntExtra(MUSIC_ACTION, 0)?.let { action ->
				Logger.i(TAG, "musicActionReceiver: action: $action")
				handleActionMusic(action)
			}
			(intent?.getSerializableExtra(SONG_OBJECT) as Song?)?.let { song->
				Logger.i(TAG, "musicActionReceiver: song: $song")
				sharedViewModel.setCurrentSong(song)
			}
			intent?.getIntExtra(REPEAT_MODE, 0)?.let { repeatMode ->
				Logger.i(TAG, "musicActionReceiver: repeatMode: $repeatMode")
				sharedViewModel.setRepeatMode(repeatMode)
			}
			intent?.getBooleanExtra(SHUFFLE_MODE, false)?.let { shuffleMode ->
				Logger.i(TAG, "musicActionReceiver: shuffleMode: $shuffleMode")
				sharedViewModel.setShuffleModeEnabled(shuffleMode)
			}
		}
	}
	
	private val themeChangeReceiver = object : BroadcastReceiver() {
		override fun onReceive(context: Context?, intent: Intent?) {
			if (intent?.action == ACTION_CHANGE_THEME) {
				Logger.i(TAG, "themeChangeReceiver: ${intent.getStringExtra(THEME)}")
				when (intent.getStringExtra(THEME)) {
					BLUE_THEME -> context?.setTheme(R.style.Theme_Blue)
					RED_THEME -> context?.setTheme(R.style.Theme_Red)
					GREEN_THEME -> context?.setTheme(R.style.Theme_Green)
				}
				this@MainActivity.recreate()
				//window.decorView.invalidate()
			}
		}
	}
	
	private val sharedViewModel: SharedViewModel by viewModels()
	
	override fun getContentLayout(): Int = R.layout.activity_main
	
	override fun initView() {
		applySavedTheme(this)
		//WindowCompat.setDecorFitsSystemWindows(window, false)
	}
	
	override fun initListener() {
		LocalBroadcastManager.getInstance(applicationContext)
			.registerReceiver(musicActionReceiver, IntentFilter(SEND_ACTION_TO_ACTIVITY))
		LocalBroadcastManager.getInstance(applicationContext)
			.registerReceiver(themeChangeReceiver, IntentFilter(ACTION_CHANGE_THEME))
	}
	
	override fun observerLiveData() {
	
	}
	
	override fun onDestroy() {
		super.onDestroy()
		LocalBroadcastManager.getInstance(this).unregisterReceiver(musicActionReceiver)
		LocalBroadcastManager.getInstance(this).unregisterReceiver(themeChangeReceiver)
	}
	
	fun startMusicService(song: Song) {
		val intent = Intent(this, MusicService::class.java).apply {
			putExtra(SONG_OBJECT, song)
		}
		startService(intent)
		if (!isServiceConnected) {
			bindService(intent, serviceConnection, BIND_AUTO_CREATE)
		}
		sharedViewModel.setCurrentSong(song)
	}
	
	private fun handleActionMusic(action: Int) {
		Logger.i("handleActionMusic", "action: $action")
		when (action) {
			MusicAction.START.action -> {
				sharedViewModel.setPlayingStatus(true)
			}
			
			MusicAction.PAUSE.action -> {
				sharedViewModel.setPlayingStatus(false)
			}
			
			MusicAction.RESUME.action -> {
				sharedViewModel.setPlayingStatus(true)
			}
		}
	}
	
	fun sendActionToService(action: Int) {
		Intent(this, MusicService::class.java).apply {
			putExtra(MUSIC_ACTION_SERVICE, action)
			startService(this)
		}
	}
	
	private fun applySavedTheme(context: Context) {
		when (sharedViewModel.getTheme()) {
			BLUE_THEME -> context.setTheme(R.style.Theme_Blue)
			RED_THEME -> context.setTheme(R.style.Theme_Red)
			GREEN_THEME -> context.setTheme(R.style.Theme_Green)
		}
	}
	
	
	companion object {
		private val TAG = MainActivity::class.simpleName
	}
	
}
