package com.example.rageamp.ui.album_detail

import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.bumptech.glide.request.RequestOptions
import com.example.rageamp.R
import com.example.rageamp.base.BaseFragment
import com.example.rageamp.data.model.Song
import com.example.rageamp.databinding.FragmentAlbumDetailBinding
import com.example.rageamp.ui.SharedViewModel
import com.example.rageamp.ui.adapter.SongAdapter
import com.example.rageamp.ui.main.MainActivity
import com.example.rageamp.utils.GlideUtils
import com.google.android.material.appbar.AppBarLayout
import dagger.hilt.android.AndroidEntryPoint
import kotlin.math.abs

@AndroidEntryPoint
class AlbumDetailFragment : BaseFragment<FragmentAlbumDetailBinding>() {
	private lateinit var songAdapter: SongAdapter
	private val args: AlbumDetailFragmentArgs by navArgs()
	private val sharedViewModel: SharedViewModel by activityViewModels()
	override fun getContentLayout(): Int = R.layout.fragment_album_detail
	
	override fun initView() {
		binding.tvAlbumName.text = args.album.name
		setupRecyclerView()
		updateNumberOfSongs(args.album.songs)
		handleAlbumImage(args.album.songs)
		setupToolbar()
		handleEnableCollapsingToolbar(args.album.songs.size)
	}
	
	override fun initListener() {
		binding.ivBack.setOnClickListener {
			findNavController().popBackStack()
		}
	}
	
	override fun observerLiveData() {
	
	}
	
	private fun setupRecyclerView() {
		songAdapter = SongAdapter(
			onClickItemSong = { song ->
				playSong(song)
			},
			onLongClickItemSong = { song, view ->
			
			}
		)
		binding.rvSongs.adapter = songAdapter
		binding.rvSongs.layoutManager = LinearLayoutManager(requireContext())
		songAdapter.submitList(args.album.songs)
	}
	
	private fun playSong(song: Song) {
		(requireActivity() as MainActivity).startMusicService(song)
		sharedViewModel.setCurrentSongs(args.album.songs)
	}
	
	private fun updateNumberOfSongs(songs: List<Song>) {
		val pluralResource =
			if (songs.size > 1) R.string.songs_with_placeholder else R.string.song_with_placeholder
		binding.tvNumberOfSongs.text = getString(pluralResource, songs.size)
	}
	
	private fun handleAlbumImage(songs: List<Song>) {
		if (songs.isNotEmpty()) {
			GlideUtils.loadImageFromUrl(
				image = binding.ivAlbum,
				url = songs[0].albumArt,
				options = RequestOptions().transform(RoundedCorners(100)),
				placeholderResId = R.drawable.image_default_playlist
			)
		} else {
			binding.ivAlbum.setImageResource(R.drawable.image_default_playlist)
		}
	}
	
	private fun setupToolbar() {
		binding.tvToolbar.text = args.album.name
		binding.tvAlbumName.text = args.album.name
		binding.appBar.addOnOffsetChangedListener { appBarLayout, verticalOffset ->
//			Logger.d(TAG, "totalScrollRange: ${appBarLayout.totalScrollRange}")
			val alpha =
				((abs(verticalOffset.toFloat()) / (appBarLayout.totalScrollRange / 0.5).toFloat()))
//			Logger.i(TAG, "totalScrollRange: ${appBarLayout.totalScrollRange}")
//			Logger.i(TAG, "alpha: $alpha")
			binding.layoutAddSong.alpha = 1 - alpha
			if (binding.layoutAddSong.alpha == alpha) {
				binding.tvToolbar.alpha = 1F
				binding.btAddSongToAlbumToolbar.alpha = 1F
			} else {
				binding.tvToolbar.alpha = 0F
				binding.btAddSongToAlbumToolbar.alpha = 0F
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
	
}
