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
			
			when (action) {
				NavigationAction.TO_SONG_FRAGMENT -> {
					navigateTo(R.id.action_leftFragment_to_songFragment)
					sharedViewModel.resetNavigation()
				}
				
				NavigationAction.TO_PLAYLIST_FRAGMENT -> {
					navigateTo(R.id.action_leftFragment_to_playlistFragment)
					sharedViewModel.resetNavigation()
				}
				
				else -> {
					Logger.w(TAG, "NavigationAction: ACTION IS NULL OR NOT EXIST ACTION")
				}
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
