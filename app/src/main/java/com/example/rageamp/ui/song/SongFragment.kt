package com.example.rageamp.ui.song

import android.util.Log
import android.view.View
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.rageamp.R
import com.example.rageamp.base.BaseFragment
import com.example.rageamp.databinding.FragmentSongBinding
import com.example.rageamp.ui.adapter.SongAdapter
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class SongFragment : BaseFragment<FragmentSongBinding>() {
	private val songViewModel: SongViewModel by viewModels()
	private lateinit var songAdapter: SongAdapter
	
	override fun getContentLayout(): Int = R.layout.fragment_song
	
	override fun initView() {
		setupRecyclerView()
	}
	
	override fun initListener() {
		binding.layoutHeader.layoutBack.setOnClickListener {
			findNavController().popBackStack()
		}
	}
	
	override fun observerLiveData() {
		lifecycleScope.launch {
			songViewModel.songs.collect { songs ->
				Log.d("CHECK_CHECK", "observerLiveData songs: $songs")
				songAdapter.submitList(songs)
				binding.tvNoData.visibility = if (songs.isEmpty()) View.VISIBLE else View.GONE
			}
		}
	}
	
	private fun setupRecyclerView() {
		songAdapter = SongAdapter(
			onClickItemSong = { song ->
			
			}
		)
		
		binding.rvSongs.apply {
			adapter = songAdapter
			layoutManager = LinearLayoutManager(context)
		}
	}
	
	companion object {
		val TAG = this::class.simpleName
	}
}
