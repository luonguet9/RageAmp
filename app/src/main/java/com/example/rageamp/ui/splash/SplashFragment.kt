package com.example.rageamp.ui.splash

import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.example.rageamp.R
import com.example.rageamp.base.BaseFragment
import com.example.rageamp.databinding.FragmentSplashBinding
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class SplashFragment : BaseFragment<FragmentSplashBinding>() {
	override fun getContentLayout(): Int = R.layout.fragment_splash
	
	override fun initView() {
		lifecycleScope.launch {
			delay(2000)
			findNavController().navigate(R.id.action_splashFragment_to_mainFragment)
		}
	}
	
	override fun initListener() {
	
	}
	
	override fun observerLiveData() {
	
	}
}
