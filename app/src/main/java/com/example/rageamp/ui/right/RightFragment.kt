package com.example.rageamp.ui.right

import androidx.fragment.app.activityViewModels
import com.example.rageamp.R
import com.example.rageamp.base.BaseFragment
import com.example.rageamp.databinding.FragmentRightBinding
import com.example.rageamp.ui.SharedViewModel
import com.example.rageamp.ui.main.MainActivity
import com.example.rageamp.utils.enums.MusicAction
import com.example.rageamp.utils.enums.NavigationAction

class RightFragment : BaseFragment<FragmentRightBinding>() {
	private val sharedViewModel: SharedViewModel by activityViewModels()
	override fun getContentLayout(): Int = R.layout.fragment_right
	
	override fun initView() {
	
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
			
			}
			btPlayOrPause.setOnClickListener {
				sendActionToService(
					if (sharedViewModel.isPlaying.value == true) MusicAction.PAUSE.action
					else MusicAction.RESUME.action
				)
			}
			btRewindForward.setOnClickListener {
			
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
		}
		
		sharedViewModel.isPlaying.observe(this) { isPlaying ->
			binding.btPlayOrPause.setImageResource(if (isPlaying) R.drawable.ic_pause else R.drawable.ic_play)
		}
	}
	
	private fun sendActionToService(action: Int) {
		(requireActivity() as MainActivity).sendActionToService(action)
	}
	
}
