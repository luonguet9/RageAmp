package com.example.rageamp.ui.playlist_detail

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.PopupWindow
import android.widget.TextView
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.bumptech.glide.request.RequestOptions
import com.example.rageamp.R
import com.example.rageamp.base.BaseFragment
import com.example.rageamp.data.model.Song
import com.example.rageamp.databinding.FragmentPlaylistDetailBinding
import com.example.rageamp.ui.SharedViewModel
import com.example.rageamp.ui.adapter.SongAdapter
import com.example.rageamp.ui.dialog.AddSongToPlaylistDialog
import com.example.rageamp.ui.main.MainActivity
import com.example.rageamp.ui.playlist.PlaylistViewModel
import com.example.rageamp.utils.FAVORITE_PLAYLIST_ID
import com.example.rageamp.utils.GlideUtils
import com.example.rageamp.utils.Logger
import com.google.android.material.appbar.AppBarLayout
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import kotlin.math.abs

@AndroidEntryPoint
class PlaylistDetailFragment : BaseFragment<FragmentPlaylistDetailBinding>() {
	private lateinit var songAdapter: SongAdapter
	private val playlistViewModel: PlaylistViewModel by activityViewModels()
	private val sharedViewModel: SharedViewModel by activityViewModels()
	override fun getContentLayout(): Int = R.layout.fragment_playlist_detail
	
	override fun initView() {
		binding.tvPlaylistName.text = playlistViewModel.playlist?.name
		setupRecyclerView()
		setupToolbar()
	}
	
	override fun initListener() {
		binding.ivBack.setOnClickListener {
			findNavController().popBackStack()
		}
		
		binding.btAddSongToPlaylistToolbar.setOnClickListener {
			showAddSongToPlaylistDialog()
		}
		
		binding.btAddSongToPlaylist.setOnClickListener {
			showAddSongToPlaylistDialog()
		}
		
		binding.btAddMusic.setOnClickListener {
			showAddSongToPlaylistDialog()
		}
	}
	
	override fun observerLiveData() {
		lifecycleScope.launch {
			playlistViewModel.getAllSongsInPlaylist()?.collect { songs ->
				Logger.i(TAG, "observerLiveData songs: $songs")
				updateNumberOfSongs(songs)
				handlePlaylistImage(songs)
				handleEnableCollapsingToolbar(songs.size)
				songAdapter.submitList(songs)
				binding.clNoData.visibility = if (songs.isNotEmpty()) View.GONE else View.VISIBLE
			}
		}
	}
	
	private fun setupRecyclerView() {
		songAdapter = SongAdapter(
			onClickItemSong = { song ->
				playSong(song)
			},
			onLongClickItemSong = { song, view ->
				val popupWidth = (view.width / 3)
				
				val popupView = LayoutInflater.from(requireContext())
					.inflate(R.layout.popup_song_in_playlist_detail, view as ViewGroup, false)
				val popupWindow =
					PopupWindow(popupView, popupWidth, ViewGroup.LayoutParams.WRAP_CONTENT, true)
				
				val playItem = popupView.findViewById<TextView>(R.id.item_play)
				playItem.setOnClickListener {
					playSong(song)
					popupWindow.dismiss()
				}
				
				val removeItem = popupView.findViewById<TextView>(R.id.item_remove)
				removeItem.setOnClickListener {
					playlistViewModel.playlist?.let { playlist ->
						playlistViewModel.removeSongFromPlaylist(song, playlist) {
							sharedViewModel.setCurrentSongs(playlist.songs)
							if (playlist.playlistId == FAVORITE_PLAYLIST_ID) {
								sharedViewModel.updateFavoriteStatus()
							}
						}
					}
					popupWindow.dismiss()
				}
				
				popupWindow.showAsDropDown(view, 0, 0)
			}
		)
		binding.rvSongs.adapter = songAdapter
		binding.rvSongs.layoutManager = LinearLayoutManager(requireContext())
	}
	
	private fun playSong(song: Song) {
		(requireActivity() as MainActivity).startMusicService(song)
		playlistViewModel.playlist?.songs?.let { sharedViewModel.setCurrentSongs(it) }
	}
	
	private fun showAddSongToPlaylistDialog() {
		childFragmentManager.let { fragmentManager ->
			AddSongToPlaylistDialog().apply {
				show(fragmentManager, tag)
			}
		}
	}
	
	private fun updateNumberOfSongs(songs: List<Song>) {
		val pluralResource =
			if (songs.size > 1) R.string.songs_with_placeholder else R.string.song_with_placeholder
		binding.tvNumberOfSongs.text = getString(pluralResource, songs.size)
	}
	
	private fun handlePlaylistImage(songs: List<Song>) {
		if (songs.isNotEmpty()) {
			GlideUtils.loadImageFromUrl(
				image = binding.ivPlaylist,
				url = songs[0].albumArt,
				options = RequestOptions().transform(RoundedCorners(100)),
				placeholderResId = R.drawable.image_default_playlist
			)
		} else {
			binding.ivPlaylist.setImageResource(R.drawable.image_default_playlist)
		}
	}
	
	private fun setupToolbar() {
		binding.tvToolbar.text = playlistViewModel.playlist?.name
		binding.tvPlaylistName.text = playlistViewModel.playlist?.name
		binding.appBar.addOnOffsetChangedListener { appBarLayout, verticalOffset ->
//			Logger.d(TAG, "totalScrollRange: ${appBarLayout.totalScrollRange}")
			val alpha =
				((abs(verticalOffset.toFloat()) / (appBarLayout.totalScrollRange / 0.5).toFloat()))
//			Logger.i(TAG, "totalScrollRange: ${appBarLayout.totalScrollRange}")
//			Logger.i(TAG, "alpha: $alpha")
			binding.layoutAddSong.alpha = 1 - alpha
			if (binding.layoutAddSong.alpha == alpha) {
				binding.tvToolbar.alpha = 1F
				binding.btAddSongToPlaylistToolbar.alpha = 1F
			} else {
				binding.tvToolbar.alpha = 0F
				binding.btAddSongToPlaylistToolbar.alpha = 0F
			}
			
		}
	}
	
	private fun handleEnableCollapsingToolbar(numberOfSongs: Int) {
		val params = binding.collapsingToolbar.layoutParams as AppBarLayout.LayoutParams
		params.scrollFlags = if (numberOfSongs > 0) {
			AppBarLayout.LayoutParams.SCROLL_FLAG_SCROLL or AppBarLayout.LayoutParams.SCROLL_FLAG_EXIT_UNTIL_COLLAPSED
		} else {
			AppBarLayout.LayoutParams.SCROLL_FLAG_NO_SCROLL
		}
		binding.collapsingToolbar.layoutParams = params
	}
	
	companion object {
		private val TAG = PlaylistDetailFragment::class.simpleName
	}
}
