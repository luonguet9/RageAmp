package com.example.rageamp.ui.main

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.media.AudioManager
import android.util.Log
import com.example.rageamp.R
import com.example.rageamp.base.BaseFragment
import com.example.rageamp.databinding.FragmentMainBinding

class MainFragment : BaseFragment<FragmentMainBinding>() {
	private val audioManager: AudioManager by lazy {
		context?.getSystemService(Context.AUDIO_SERVICE) as AudioManager
	}
	
	private val volumeChangeReceiver = object : BroadcastReceiver() {
		override fun onReceive(context: Context?, intent: Intent?) {
			if (intent?.action == VOLUME_CHANGED_ACTION) {
				Log.i(TAG, "onReceive: VOLUME_CHANGED_ACTION")
				displayVolumeSlider()
			}
		}
	}
	
	override fun getContentLayout(): Int = R.layout.fragment_main
	
	override fun initView() {
		displayVolumeSlider()
	}
	
	override fun initListener() {
		requireContext().registerReceiver(volumeChangeReceiver, IntentFilter(VOLUME_CHANGED_ACTION))
		binding.volumeSlider.addOnChangeListener { _, value, fromUser ->
			if (fromUser) {
				val newVolume =
					((value / 100) * audioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC)).toInt()
				audioManager.setStreamVolume(AudioManager.STREAM_MUSIC, newVolume, 0)
				updateVolumeDisplay(value.toInt())
			}
		}
	}
	
	override fun observerLiveData() {
	
	}
	
	override fun onDestroyView() {
		super.onDestroyView()
		requireActivity().unregisterReceiver(volumeChangeReceiver)
	}
	
	private fun displayVolumeSlider() {
		val currentVolume = audioManager.getStreamVolume(AudioManager.STREAM_MUSIC)
		val maxVolume = audioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC)
		val sliderValue = (currentVolume.toFloat() / maxVolume * 100)
		binding.volumeSlider.value = sliderValue
		updateVolumeDisplay(sliderValue.toInt())
		
	}
	
	private fun updateVolumeDisplay(value: Int) {
		binding.tvVolumeValue.text = value.toString()
		val iconRes = when {
			value <= 0 -> R.drawable.ic_volume_off
			value < 33 -> R.drawable.ic_volume_mute
			value < 67 -> R.drawable.ic_volume_down
			else -> R.drawable.ic_volume_up
		}
		binding.btVolume.setImageResource(iconRes)
	}
	
	companion object {
		private val TAG = MainFragment::class.simpleName
		private const val VOLUME_CHANGED_ACTION = "android.media.VOLUME_CHANGED_ACTION"
	}
}
