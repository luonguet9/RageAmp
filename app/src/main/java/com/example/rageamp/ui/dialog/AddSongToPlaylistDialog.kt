package com.example.rageamp.ui.dialog

import android.content.res.Resources
import android.text.Editable
import android.text.TextWatcher
import android.view.Gravity
import android.view.WindowManager
import android.view.inputmethod.EditorInfo
import android.widget.Toast
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.rageamp.R
import com.example.rageamp.base.BaseDialog
import com.example.rageamp.data.model.Song
import com.example.rageamp.databinding.DialogAddSongToPlaylistBinding
import com.example.rageamp.ui.SharedViewModel
import com.example.rageamp.ui.adapter.SongAdapter
import com.example.rageamp.ui.playlist.PlaylistViewModel
import com.example.rageamp.utils.Logger
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@AndroidEntryPoint
class AddSongToPlaylistDialog : BaseDialog<DialogAddSongToPlaylistBinding>() {
	
	private val sharedViewModel: SharedViewModel by activityViewModels()
	private val playlistViewModel: PlaylistViewModel by activityViewModels()
	
	private lateinit var songAdapter: SongAdapter
	private var songs = mutableListOf<Song>()
	
	override fun getLayoutResource(): Int = R.layout.dialog_add_song_to_playlist
	
	override fun getGravityForDialog(): Int = Gravity.CENTER or Gravity.BOTTOM
	
	override fun initView() {
		setLeftHalfScreen()
		setupRecyclerView()
		observeLiveData()
	}
	
	override fun initListener() {
		handleSearchBarListener()
		binding.ivClose.setOnClickListener() {
			dismiss()
		}
	}
	
	private fun setupRecyclerView() {
		songAdapter = SongAdapter(
			onClickItemSong = { song ->
				playlistViewModel.playlist?.let { playlist ->
					playlistViewModel.addSongToPlaylist(song, playlist) { success ->
						Logger.i(TAG, "addSongToPlaylist success = $success")
						if (success) {
							playlist.songs.add(song)
							songAdapter.notifyItemChanged(
								songs.indexOfFirst { it.songId == song.songId },
								song
							)
							Toast.makeText(
								requireContext(),
								getString(R.string.successfully),
								Toast.LENGTH_SHORT
							).show()
						} else {
							Toast.makeText(
								requireContext(),
								getString(R.string.song_exists),
								Toast.LENGTH_SHORT
							).show()
						}
					}
				}
			},
			onLongClickItemSong = { song, view ->
			
			},
			playlist = playlistViewModel.playlist
		)
		
		binding.rcvListSong.apply {
			adapter = songAdapter
			layoutManager = LinearLayoutManager(context, RecyclerView.VERTICAL, false)
		}
	}
	
	private fun observeLiveData() {
		lifecycleScope.launch {
			sharedViewModel.allSongs.collectLatest { songs ->
				this@AddSongToPlaylistDialog.songs = songs.toMutableList()
				songAdapter.submitList(songs)
			}
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
		val filteredList = songs.filter { it.title?.lowercase()?.contains(searchText) == true }
		songAdapter.submitList(filteredList)
	}
	
	companion object {
		private val TAG = AddSongToPlaylistDialog::class.simpleName
	}
	
}
