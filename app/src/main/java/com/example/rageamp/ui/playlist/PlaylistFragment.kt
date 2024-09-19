package com.example.rageamp.ui.playlist

import androidx.navigation.fragment.findNavController
import com.example.rageamp.R
import com.example.rageamp.base.BaseFragment
import com.example.rageamp.databinding.FragmentPlaylistBinding

class PlaylistFragment : BaseFragment<FragmentPlaylistBinding>() {
	override fun getContentLayout(): Int = R.layout.fragment_playlist
	
	override fun initView() {
	
	}
	
	override fun initListener() {
		binding.layoutHeader.layoutBack.setOnClickListener {
			findNavController().popBackStack()
		}
	}
	
	override fun observerLiveData() {
	
	}
}
