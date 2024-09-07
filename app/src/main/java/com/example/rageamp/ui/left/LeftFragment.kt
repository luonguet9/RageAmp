package com.example.rageamp.ui.left

import androidx.navigation.fragment.findNavController
import com.example.rageamp.R
import com.example.rageamp.base.BaseFragment
import com.example.rageamp.databinding.FragmentLeftBinding

class LeftFragment : BaseFragment<FragmentLeftBinding>() {
	override fun getContentLayout(): Int = R.layout.fragment_left
	
	override fun initView() {
	
	}
	
	override fun initListener() {
		binding.btPlaylist.setOnClickListener {
			findNavController().navigate(R.id.action_leftFragment_to_playlistFragment)
		}
	}
	
	override fun observerLiveData() {
	
	}
}
