package com.example.rageamp.ui.main

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.graphics.drawable.ColorDrawable
import android.media.AudioManager
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.fragment.app.activityViewModels
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.example.rageamp.R
import com.example.rageamp.base.BaseFragment
import com.example.rageamp.databinding.FragmentMainBinding
import com.example.rageamp.ui.SharedViewModel
import com.example.rageamp.utils.ACTION_CHANGE_THEME
import com.example.rageamp.utils.BLUE_THEME
import com.example.rageamp.utils.GREEN_THEME
import com.example.rageamp.utils.Logger
import com.example.rageamp.utils.RED_THEME
import com.example.rageamp.utils.THEME
import com.google.android.material.dialog.MaterialAlertDialogBuilder

class MainFragment : BaseFragment<FragmentMainBinding>() {
	private val sharedViewModel: SharedViewModel by activityViewModels()
	
	private val audioManager: AudioManager by lazy {
		context?.getSystemService(Context.AUDIO_SERVICE) as AudioManager
	}
	
	private val volumeChangeReceiver = object : BroadcastReceiver() {
		override fun onReceive(context: Context?, intent: Intent?) {
			if (intent?.action == VOLUME_CHANGED_ACTION) {
				Logger.i(TAG, "onReceive: VOLUME_CHANGED_ACTION")
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
		
		binding.btSettingAudio.setOnClickListener {
			val themes = arrayOf(BLUE_THEME, RED_THEME, GREEN_THEME)
			val currentTheme = sharedViewModel.getTheme()
			var selectedItem = themes.indexOf(currentTheme)
			MaterialAlertDialogBuilder(requireContext())
				.setTitle(getString(R.string.choose_theme))
				.setSingleChoiceItems(themes, selectedItem) { _, which ->
					selectedItem = which
				}
				.setNegativeButton(getString(R.string.cancel)) { dialog, _ ->
					dialog.dismiss()
				}
				.setPositiveButton(getString(R.string.ok)) { _, _ ->
					val intent = Intent(ACTION_CHANGE_THEME).apply {
						putExtra(THEME, themes[selectedItem])
					}
					LocalBroadcastManager.getInstance(requireContext()).sendBroadcast(intent)
					sharedViewModel.saveTheme(themes[selectedItem])
					Toast.makeText(
						context,
						"You chose ${themes[selectedItem]} theme",
						Toast.LENGTH_SHORT
					).show()
				}
				.show()
		}
		
		binding.btPower.setOnClickListener {
			MaterialAlertDialogBuilder(requireContext())
				.setTitle(getString(R.string.exit_confirmation_title))
				.setMessage(getString(R.string.exit_confirmation_message))
				.setNegativeButton(getString(R.string.cancel)) { dialog, _ ->
					dialog.dismiss()
				}
				.setPositiveButton(getString(R.string.exit)) { _, _ ->
					requireActivity().finish()
				}
				.show()
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
