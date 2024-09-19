package com.example.rageamp.ui.right

import androidx.fragment.app.activityViewModels
import com.example.rageamp.R
import com.example.rageamp.base.BaseFragment
import com.example.rageamp.databinding.FragmentRightBinding
import com.example.rageamp.ui.SharedViewModel
import com.example.rageamp.utils.enums.NavigationAction

class RightFragment : BaseFragment<FragmentRightBinding>() {
	private val sharedViewModel: SharedViewModel by activityViewModels()
	override fun getContentLayout(): Int = R.layout.fragment_right
	
	override fun initView() {
	
	}
	
	override fun initListener() {
		binding.btSongs.setOnClickListener {
			sharedViewModel.navigate(NavigationAction.TO_SONG_FRAGMENT)
		}
		
		binding.btPlaylist.setOnClickListener {
			sharedViewModel.navigate(NavigationAction.TO_PLAYLIST_FRAGMENT)
		}
	}
	
	override fun observerLiveData() {
	
	}
}
