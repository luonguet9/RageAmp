package com.example.rageamp.ui.main

import android.content.BroadcastReceiver
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.ServiceConnection
import android.os.IBinder
import android.util.Log
import androidx.core.view.WindowCompat
import androidx.lifecycle.ViewModelProvider
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.example.rageamp.R
import com.example.rageamp.base.BaseActivity
import com.example.rageamp.data.model.Song
import com.example.rageamp.databinding.ActivityMainBinding
import com.example.rageamp.service.MusicService
import com.example.rageamp.ui.SharedViewModel
import com.example.rageamp.utils.MUSIC_ACTION
import com.example.rageamp.utils.MUSIC_ACTION_SERVICE
import com.example.rageamp.utils.SEND_ACTION_TO_ACTIVITY
import com.example.rageamp.utils.SONG_OBJECT
import com.example.rageamp.utils.enums.MusicAction
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : BaseActivity<ActivityMainBinding>() {
	/**Music Service*/
	var musicService: MusicService? = null
	private var isServiceConnected = false
	private val serviceConnection = object : ServiceConnection {
		override fun onServiceConnected(name: ComponentName?, service: IBinder?) {
			val binder = service as MusicService.MusicBinder
			musicService = binder.getService()
			isServiceConnected = true
		}
		
		override fun onServiceDisconnected(name: ComponentName?) {
			musicService = null
			isServiceConnected = false
		}
		
	}
	private val broadcastReceiver = object : BroadcastReceiver() {
		override fun onReceive(context: Context?, intent: Intent?) {
			intent?.getIntExtra(MUSIC_ACTION, 0)?.let { action ->
				handleActionMusic(action)
			}
		}
	}
	
	private val sharedViewModel: SharedViewModel by lazy {
		ViewModelProvider(this)[SharedViewModel::class.java]
	}
	
	override fun getContentLayout(): Int = R.layout.activity_main
	
	override fun initView() {
		WindowCompat.setDecorFitsSystemWindows(window, false)
	}
	
	override fun initListener() {
		LocalBroadcastManager.getInstance(applicationContext)
			.registerReceiver(broadcastReceiver, IntentFilter(SEND_ACTION_TO_ACTIVITY))
	}
	
	override fun observerLiveData() {
	
	}
	
	override fun onDestroy() {
		super.onDestroy()
		LocalBroadcastManager.getInstance(this).unregisterReceiver(broadcastReceiver)
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
		Log.i("handleActionMusic", "action: $action")
		when (action) {
			MusicAction.START.action -> {
				musicService?.currentSong?.let { song ->
					sharedViewModel.setCurrentSong(song)
				}
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
	
}
