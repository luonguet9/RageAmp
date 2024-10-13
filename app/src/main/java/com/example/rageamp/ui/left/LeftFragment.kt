package com.example.rageamp.ui.left

import android.view.animation.LinearInterpolator
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.example.rageamp.R
import com.example.rageamp.base.BaseFragment
import com.example.rageamp.databinding.FragmentLeftBinding
import com.example.rageamp.ui.SharedViewModel
import com.example.rageamp.utils.GlideUtils
import com.example.rageamp.utils.Logger
import com.example.rageamp.utils.enums.NavigationAction

class LeftFragment : BaseFragment<FragmentLeftBinding>() {
	private val sharedViewModel: SharedViewModel by activityViewModels()
	
	override fun getContentLayout(): Int = R.layout.fragment_left
	
	override fun initView() {
		Logger.i("initView $this")
	}
	
	override fun initListener() {
	
	}
	
	override fun observerLiveData() {
		sharedViewModel.navigationAction.observeForever { action ->
			Logger.i("NavigationAction: action: $action")
			action.takeIf { it != sharedViewModel.lastNavigationAction }?.let {
				val destinationId = when (it) {
					NavigationAction.TO_SONG_FRAGMENT -> R.id.action_leftFragment_to_songFragment
					NavigationAction.TO_PLAYLIST_FRAGMENT -> R.id.action_leftFragment_to_playlistFragment
					NavigationAction.TO_ALBUM_FRAGMENT -> R.id.action_leftFragment_to_albumFragment
					else -> {
						Logger.w("NavigationAction: INVALID ACTION")
						return@observeForever
					}
				}
				
				Logger.d("Navigate to $it")
				sharedViewModel.lastNavigationAction = it
				navigateTo(destinationId)
				sharedViewModel.resetNavigation()
			} ?: run {
				Logger.w("Do not perform navigation")
			}
		}
		
		sharedViewModel.currentSong.observe(this) { song ->
			GlideUtils.loadImageFromUrl(
				image = binding.ivSong,
				url = song.albumArt,
				placeholderResId = R.drawable.ic_app
			)
		}
		
		sharedViewModel.isPlaying.observe(this) { isPlaying ->
			startOrStopAnimationImageSong(isPlaying)
		}
	}
	
	override fun onResume() {
		super.onResume()
		sharedViewModel.lastNavigationAction = NavigationAction.INVALID
	}
	
	private fun navigateTo(actionId: Int) {
		findNavController().popBackStack(findNavController().graph.startDestinationId, false)
		findNavController().navigate(actionId)
	}
	
	private fun startOrStopAnimationImageSong(isStart: Boolean = true) {
		Logger.i("startOrStopAnimationImageSong isStart: $isStart")
		if (isStart) {
			binding.ivSong.animate()
				.rotationBy(360f)
				.setDuration(20000)
				.setInterpolator(LinearInterpolator())
				.withEndAction {
					startOrStopAnimationImageSong(sharedViewModel.isPlaying.value == true)
				}
				.start()
			
		} else {
			binding.ivSong.animate().cancel()
		}
	}
	
}
