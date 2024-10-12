package com.example.rageamp.ui.left

import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.example.rageamp.R
import com.example.rageamp.base.BaseFragment
import com.example.rageamp.databinding.FragmentLeftBinding
import com.example.rageamp.ui.SharedViewModel
import com.example.rageamp.utils.Logger
import com.example.rageamp.utils.enums.NavigationAction

class LeftFragment : BaseFragment<FragmentLeftBinding>() {
	private val sharedViewModel: SharedViewModel by activityViewModels()
	
	override fun getContentLayout(): Int = R.layout.fragment_left
	
	override fun initView() {
	
	}
	
	override fun initListener() {
	
	}
	
	override fun observerLiveData() {
		sharedViewModel.navigationAction.observeForever { action ->
			Logger.i(TAG, "NavigationAction: action: $action")
			action.takeIf { it != sharedViewModel.lastNavigationAction }?.let {
				val destinationId = when (it) {
					NavigationAction.TO_SONG_FRAGMENT -> R.id.action_leftFragment_to_songFragment
					NavigationAction.TO_PLAYLIST_FRAGMENT -> R.id.action_leftFragment_to_playlistFragment
					NavigationAction.TO_ALBUM_FRAGMENT -> R.id.action_leftFragment_to_albumFragment
					else -> {
						Logger.w(TAG, "NavigationAction: INVALID ACTION")
						return@observeForever
					}
				}
				
				Logger.d(TAG, "Navigate to $it")
				sharedViewModel.lastNavigationAction = it
				navigateTo(destinationId)
				sharedViewModel.resetNavigation()
			} ?: run {
				Logger.w(TAG, "Do not perform navigation")
			}
		}
	}
	
	private fun navigateTo(actionId: Int) {
		findNavController().popBackStack(findNavController().graph.startDestinationId, false)
		findNavController().navigate(actionId)
	}
	
	companion object {
		private val TAG = LeftFragment::class.simpleName
	}
}
