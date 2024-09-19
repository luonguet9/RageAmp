package com.example.rageamp.ui.splash

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.navigation.fragment.findNavController
import com.example.rageamp.R
import com.example.rageamp.base.BaseFragment
import com.example.rageamp.databinding.FragmentSplashBinding

class SplashFragment : BaseFragment<FragmentSplashBinding>() {
	
	private val requestPermissionLauncher =
		registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted: Boolean ->
			if (isGranted) {
				findNavController().navigate(R.id.action_splashFragment_to_mainFragment)
			} else {
				Toast.makeText(
					requireContext(),
					getString(R.string.denied_to_fetch_songs),
					Toast.LENGTH_SHORT
				).show()
			}
		}
	override fun getContentLayout(): Int = R.layout.fragment_splash
	
	override fun initView() {
		requestPermission()
	}
	
	override fun initListener() {
	
	}
	
	override fun observerLiveData() {
	
	}
	
	private fun requestPermission() {
		val permission =
			if (Build.VERSION.SDK_INT > 32) Manifest.permission.READ_MEDIA_AUDIO else Manifest.permission.READ_EXTERNAL_STORAGE
		
		if (ContextCompat.checkSelfPermission(requireContext(), permission) == PackageManager.PERMISSION_GRANTED) {
			findNavController().navigate(R.id.action_splashFragment_to_mainFragment)
		} else {
			requestPermissionLauncher.launch(permission)
		}
	}
}
