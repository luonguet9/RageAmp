package com.example.rageamp.ui.playlist

import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.widget.PopupWindow
import android.widget.TextView
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import com.example.rageamp.R
import com.example.rageamp.base.BaseFragment
import com.example.rageamp.data.model.Playlist
import com.example.rageamp.databinding.FragmentPlaylistBinding
import com.example.rageamp.ui.adapter.PlaylistAdapter
import com.example.rageamp.ui.dialog.AddNewPlaylistDialog
import com.example.rageamp.ui.dialog.DeletePlaylistDialog
import com.example.rageamp.ui.dialog.RenamePlaylistDialog
import com.example.rageamp.utils.Logger
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class PlaylistFragment : BaseFragment<FragmentPlaylistBinding>() {
	private lateinit var playlistAdapter: PlaylistAdapter
	private val playlistViewModel: PlaylistViewModel by activityViewModels()
	override fun getContentLayout(): Int = R.layout.fragment_playlist
	
	override fun initView() {
		setupRecyclerView()
		playlistViewModel.checkAndInsertFavoritePlaylist()
	}
	
	override fun initListener() {
		binding.layoutHeader.layoutBack.setOnClickListener {
			findNavController().popBackStack()
		}
		handleSearchBarListener()
		
		binding.btAddPlaylist.setOnClickListener {
			activity?.supportFragmentManager?.let {
				AddNewPlaylistDialog().apply {
					addPlaylistCallback { playlistName ->
						playlistViewModel.insertPlaylists(
							Playlist(name = playlistName)
						)
					}
					show(it, this.tag)
				}
			}
		}
	}
	
	override fun observerLiveData() {
		lifecycleScope.launch {
			//repeatOnLifecycle(Lifecycle.State.STARTED) {
			playlistViewModel.playlists.collect { playlists ->
				Logger.i("observerLiveData: playlists: $playlists")
				playlistAdapter.submitList(playlists)
			}
			//}
		}
	}
	
	private fun setupRecyclerView() {
		playlistAdapter = PlaylistAdapter(
			onClickItemPlaylist = { playlist ->
				findNavController().navigate(R.id.action_playlistFragment_to_playlistDetailFragment)
				playlistViewModel.playlist = playlist
			},
			onLongClickItemPlaylist = { playlist, view ->
				val popupWidth = (view.width * 2 / 3)
				
				val popupView = LayoutInflater.from(requireContext())
					.inflate(R.layout.popup_playlist, view as ViewGroup, false)
				val popupWindow =
					PopupWindow(popupView, popupWidth, ViewGroup.LayoutParams.WRAP_CONTENT, true)
				
				val renameItem = popupView.findViewById<TextView>(R.id.item_rename)
				renameItem.setOnClickListener {
					renamePlaylist(playlist)
					popupWindow.dismiss()
				}
				
				val deleteItem = popupView.findViewById<TextView>(R.id.item_delete)
				deleteItem.setOnClickListener {
					deletePlaylist(playlist)
					popupWindow.dismiss()
				}
				
				popupWindow.showAsDropDown(view, 0, 0)
			}
		)
		binding.rvPlaylist.adapter = playlistAdapter
		binding.rvPlaylist.layoutManager = GridLayoutManager(requireContext(), 2)
	}
	
	private fun renamePlaylist(playlist: Playlist) {
		activity?.supportFragmentManager?.let {
			RenamePlaylistDialog().apply {
				renamePlaylistCallback { newName ->
					playlistViewModel.renamePlaylist(playlist, newName)
				}
				show(it, this.tag)
			}
		}
	}
	
	private fun deletePlaylist(playlist: Playlist) {
		activity?.supportFragmentManager?.let {
			DeletePlaylistDialog().apply {
				deletePlaylistCallback {
					playlistViewModel.deletePlaylists(playlist)
				}
				show(it, this.tag)
			}
		}
	}
	
	private fun handleSearchBarListener() {
		binding.edtSearch.setOnEditorActionListener { _, actionId, _ ->
			if (actionId == EditorInfo.IME_ACTION_SEARCH) {
				searchPlaylist()
			}
			false
		}
		
		binding.edtSearch.addTextChangedListener(object : TextWatcher {
			override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
			
			override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
			
			override fun afterTextChanged(s: Editable?) {
				searchPlaylist()
			}
		})
	}
	
	private fun searchPlaylist() {
		val searchText = binding.edtSearch.text.toString().lowercase()
		val filteredList =
			playlistViewModel.playlists.value.filter { it.name.lowercase().contains(searchText) }
		playlistAdapter.submitList(filteredList)
	}
	
}
