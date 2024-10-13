package com.example.rageamp.ui.song

import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.widget.PopupWindow
import android.widget.TextView
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.rageamp.R
import com.example.rageamp.base.BaseFragment
import com.example.rageamp.databinding.FragmentSongBinding
import com.example.rageamp.ui.SharedViewModel
import com.example.rageamp.ui.adapter.SongAdapter
import com.example.rageamp.ui.main.MainActivity
import com.example.rageamp.utils.Logger
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class SongFragment : BaseFragment<FragmentSongBinding>() {
	private val songViewModel: SongViewModel by viewModels()
	private val sharedViewModel: SharedViewModel by activityViewModels()
	private lateinit var songAdapter: SongAdapter
	
	override fun getContentLayout(): Int = R.layout.fragment_song
	
	override fun initView() {
		setupRecyclerView()
	}
	
	override fun initListener() {
		binding.layoutHeader.layoutBack.setOnClickListener {
			findNavController().popBackStack()
		}
		handleSearchBarListener()
		
		binding.ivSort.setOnClickListener { view ->
			handleSortSongs(view)
		}
	}
	
	override fun observerLiveData() {
		lifecycleScope.launch {
			songViewModel.songs.collect { songs ->
				Logger.d("observerLiveData songs: $songs")
				songAdapter.submitList(songs)
				binding.tvNoData.visibility = if (songs.isEmpty()) View.VISIBLE else View.GONE
			}
		}
	}
	
	private fun setupRecyclerView() {
		songAdapter = SongAdapter(
			onClickItemSong = { song ->
				(requireActivity() as MainActivity).startMusicService(song)
				sharedViewModel.setCurrentSongs(songViewModel.songs.value)
			},
			onLongClickItemSong = { song, view ->
			
			}
		)
		
		binding.rvSongs.apply {
			adapter = songAdapter
			layoutManager = LinearLayoutManager(context)
		}
	}
	
	private fun handleSearchBarListener() {
		binding.edtSearch.setOnEditorActionListener { _, actionId, _ ->
			if (actionId == EditorInfo.IME_ACTION_SEARCH) {
				searchSong()
			}
			false
		}
		
		binding.edtSearch.addTextChangedListener(object : TextWatcher {
			override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
			
			override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
			
			override fun afterTextChanged(s: Editable?) {
				searchSong()
			}
		})
	}
	
	private fun searchSong() {
		val searchText = binding.edtSearch.text.toString().lowercase()
		val filteredList =
			songViewModel.songs.value.filter { it.title?.lowercase()?.contains(searchText) == true }
		songAdapter.submitList(filteredList)
	}
	
	private fun handleSortSongs(view: View) {
		val popupView = LayoutInflater.from(requireContext())
			.inflate(R.layout.popup_sort_song, view.rootView as ViewGroup, false)
		val popupWindow =
			PopupWindow(
				popupView,
				ViewGroup.LayoutParams.WRAP_CONTENT,
				ViewGroup.LayoutParams.WRAP_CONTENT,
				true
			)
		
		val sortByTitleAZItem = popupView.findViewById<TextView>(R.id.item_sort_by_title_az)
		sortByTitleAZItem.setOnClickListener {
			songViewModel.sortSongsByTitleAZ()
			sharedViewModel.setCurrentSongs(songViewModel.songs.value)
			popupWindow.dismiss()
		}
		
		val sortByTitleZAItem = popupView.findViewById<TextView>(R.id.item_sort_by_title_za)
		sortByTitleZAItem.setOnClickListener {
			songViewModel.sortSongsByTitleZA()
			sharedViewModel.setCurrentSongs(songViewModel.songs.value)
			popupWindow.dismiss()
		}
		
		val sortByArtistItem = popupView.findViewById<TextView>(R.id.item_sort_by_artist)
		sortByArtistItem.setOnClickListener {
			songViewModel.sortSongsByArtist()
			sharedViewModel.setCurrentSongs(songViewModel.songs.value)
			popupWindow.dismiss()
		}
		
		val sortByDurationItem = popupView.findViewById<TextView>(R.id.item_sort_by_duration)
		sortByDurationItem.setOnClickListener {
			songViewModel.sortSongsByDuration()
			sharedViewModel.setCurrentSongs(songViewModel.songs.value)
			popupWindow.dismiss()
		}
		
		popupWindow.showAsDropDown(view, 0, 0)
	}
	
}
