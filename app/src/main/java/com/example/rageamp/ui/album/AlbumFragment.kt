package com.example.rageamp.ui.album

import android.text.Editable
import android.text.TextWatcher
import android.view.inputmethod.EditorInfo
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import com.example.rageamp.R
import com.example.rageamp.base.BaseFragment
import com.example.rageamp.databinding.FragmentAlbumBinding
import com.example.rageamp.ui.adapter.AlbumAdapter
import com.example.rageamp.utils.Logger
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class AlbumFragment : BaseFragment<FragmentAlbumBinding>() {
	private val albumViewModel: AlbumViewModel by viewModels()
	private lateinit var albumAdapter: AlbumAdapter
	override fun getContentLayout(): Int = R.layout.fragment_album
	
	override fun initView() {
		setupRecyclerView()
	}
	
	override fun initListener() {
		binding.layoutHeader.layoutBack.setOnClickListener {
			findNavController().popBackStack()
		}
		handleSearchBarListener()
	}
	
	override fun observerLiveData() {
		lifecycleScope.launch {
			albumViewModel.albums.collect { albums ->
				Logger.i(TAG, "albums: $albums")
				albumAdapter.submitList(albums)
			}
		}
	}
	
	private fun setupRecyclerView() {
		albumAdapter = AlbumAdapter(
			onClickItemAlbum = { album ->
				val action = AlbumFragmentDirections.actionAlbumFragmentToAlbumDetailFragment(album)
				findNavController().navigate(action)
			},
			onLongClickItemAlbum = { album, view ->
			
			}
		)
		binding.rvAlbum.adapter = albumAdapter
		binding.rvAlbum.layoutManager = GridLayoutManager(requireContext(), 2)
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
			albumViewModel.albums.value.filter {
				it.name?.lowercase()?.contains(searchText) == true
			}
		albumAdapter.submitList(filteredList)
	}
	
	companion object {
		private val TAG = AlbumFragment::class.simpleName
	}
}
