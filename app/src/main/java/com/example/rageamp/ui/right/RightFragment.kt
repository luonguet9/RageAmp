package com.example.rageamp.ui.right

import android.util.Log
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import com.example.rageamp.R
import com.example.rageamp.base.BaseFragment
import com.example.rageamp.databinding.FragmentRightBinding
import com.example.rageamp.service.MusicService
import com.example.rageamp.ui.SharedViewModel
import com.example.rageamp.ui.main.MainActivity
import com.example.rageamp.utils.enums.MusicAction
import com.example.rageamp.utils.enums.NavigationAction
import com.example.rageamp.utils.timeFormatter
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class RightFragment : BaseFragment<FragmentRightBinding>() {
	private val sharedViewModel: SharedViewModel by activityViewModels()
	private val musicService: MusicService?
		get() = (requireActivity() as MainActivity).musicService
	private var sliderUpdateJob: Job? = null
	override fun getContentLayout(): Int = R.layout.fragment_right
	
	override fun initView() {
		setupSliderMusic()
	}
	
	override fun initListener() {
		binding.apply {
			btRepeat.setOnClickListener {
			
			}
			btShuffle.setOnClickListener {
			
			}
			btPrevious.setOnClickListener {
				sendActionToService(MusicAction.PREVIOUS.action)
			}
			btRewindBack.setOnClickListener {
				sendActionToService(MusicAction.REWIND_BACK.action)
			}
			
			btPlayOrPause.setOnClickListener {
				sendActionToService(
					if (sharedViewModel.isPlaying.value == true) MusicAction.PAUSE.action
					else MusicAction.RESUME.action
				)
			}
			btRewindForward.setOnClickListener {
				sendActionToService(MusicAction.REWIND_FORWARD.action)
			}
			
			btNext.setOnClickListener {
				sendActionToService(MusicAction.NEXT.action)
			}
			btSongs.setOnClickListener {
				sharedViewModel.navigate(NavigationAction.TO_SONG_FRAGMENT)
			}
			btPlaylist.setOnClickListener {
				sharedViewModel.navigate(NavigationAction.TO_PLAYLIST_FRAGMENT)
			}
		}
	}
	
	override fun observerLiveData() {
		sharedViewModel.currentSong.observe(this) {
			binding.song = it
			updateRealTimeSlider()
		}
		
		sharedViewModel.isPlaying.observe(this) { isPlaying ->
			binding.btPlayOrPause.setImageResource(if (isPlaying) R.drawable.ic_pause else R.drawable.ic_play)
		}
	}
	
	private fun sendActionToService(action: Int) {
		(requireActivity() as MainActivity).sendActionToService(action)
	}
	
	private fun setupSliderMusic() {
		binding.slider.apply {
			setLabelFormatter { value: Float ->
				timeFormatter().format(value)
			}
			addOnChangeListener { _, value, fromUser ->
				if (fromUser) {
					musicService?.exoPlayer?.seekTo(value.toLong())
				}
			}
		}
	}
	
	private fun updateRealTimeSlider() {
		sliderUpdateJob?.cancel()
		sliderUpdateJob = lifecycleScope.launch {
			while (true) {
				musicService?.exoPlayer?.let {
					binding.apply {
						try {
							slider.apply {
								if (it.duration > 0) valueTo = it.duration.toFloat()
								if (valueTo > 0) valueFrom = 0f
								if (it.currentPosition > 0 && it.currentPosition < valueTo) value =
									it.currentPosition.toFloat()
							}
						} catch (e: Exception) {
							Log.e(TAG, "updateRealTimeSlider error: $e")
						}
						
						tvTotalTime.text = timeFormatter().format(it.duration)
						tvRunTime.text = timeFormatter().format(it.currentPosition)
					}
				}
				delay(300)
			}
		}
	}
	
	companion object {
		private val TAG = RightFragment::class.simpleName
	}
}
