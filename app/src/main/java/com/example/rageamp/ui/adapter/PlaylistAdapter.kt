package com.example.rageamp.ui.adapter

import android.content.res.Resources
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncDifferConfig
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.rageamp.R
import com.example.rageamp.data.model.Playlist
import com.example.rageamp.databinding.ItemPlaylistBinding
import com.example.rageamp.utils.GlideUtils
import com.example.rageamp.utils.Logger
import java.util.concurrent.Executors

class PlaylistAdapter(
	private val onClickItemPlaylist: (Playlist) -> Unit,
	private val onLongClickItemPlaylist: (Playlist, View) -> Unit,
) : ListAdapter<Any, RecyclerView.ViewHolder>(
	AsyncDifferConfig.Builder(ItemPlaylistCallback())
		.setBackgroundThreadExecutor(Executors.newSingleThreadExecutor())
		.build()
) {
	
	inner class PlaylistViewHolder(
		private val binding: ItemPlaylistBinding,
		private val onClickItemPlaylist: (Playlist) -> Unit,
	) : RecyclerView.ViewHolder(binding.root) {
		
		init {
			setupUIItemPlaylist(itemView)
		}
		
		fun bind(playlist: Playlist) {
			Logger.d(PLAYLIST_ADAPTER_TAG, "bind playlist: $playlist")
			binding.tvNamePlaylist.text = playlist.name
			val pluralResource =
				if (playlist.songs.size > 1) R.string.songs_with_placeholder else R.string.song_with_placeholder
			binding.tvNumberOfSongs.text =
				itemView.context.getString(pluralResource, playlist.songs.size)
			
			if (playlist.songs.isNotEmpty()) {
				GlideUtils.loadImageFromUrl(
					image = binding.ivPlaylist,
					url = playlist.songs[0].albumArt,
					placeholderResId = R.drawable.image_default_playlist
				)
			} else {
				binding.ivPlaylist.setImageResource(R.drawable.image_default_playlist)
			}
			
			binding.root.setOnClickListener {
				onClickItemPlaylist.invoke(playlist)
			}
			
			if (playlist.playlistId != 1) {
				binding.root.setOnLongClickListener { view ->
					onLongClickItemPlaylist.invoke(playlist, view)
					true
				}
			}
		}
		
		private fun setupUIItemPlaylist(view: View) {
			Logger.d(PLAYLIST_VIEW_HOLDER_TAG, "setupUIItemPlaylist")
			val screenWidth = Resources.getSystem().displayMetrics.widthPixels / 2
			val margin = view.context.resources.getDimensionPixelOffset(R.dimen.dp32)
			val itemWidth = (screenWidth - 3 * margin) / 2
			
			when (val layoutParams = view.layoutParams) {
				is GridLayoutManager.LayoutParams -> {
					layoutParams.marginEnd = margin
					
					view.post {
//						binding.ivPlaylist.layoutParams.width = itemWidth
//						binding.ivPlaylist.layoutParams.height = itemWidth
						binding.clRoot.layoutParams.width = itemWidth
						//binding.clRoot.layoutParams.height = itemWidth + binding.tvNamePlaylist.height + binding.tvNumberOfSongs.height
						
						//layoutParams.width = itemWidth
						//layoutParams.height = itemWidth + binding.tvNamePlaylist.height + binding.tvNumberOfSongs.height
						view.requestLayout()
						//view.invalidate()
					}
				}
			}
		}
	}
	
	override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
		return PlaylistViewHolder(
			binding = ItemPlaylistBinding.inflate(
				LayoutInflater.from(parent.context),
				parent,
				false
			),
			onClickItemPlaylist = onClickItemPlaylist,
		)
	}
	
	override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
		val item = getItem(position)
		when (holder) {
			is PlaylistViewHolder -> {
				val playlist = item as? Playlist ?: return
				holder.bind(playlist)
			}
		}
	}
	
	companion object {
		private val PLAYLIST_VIEW_HOLDER_TAG = PlaylistViewHolder::class.simpleName
		private val PLAYLIST_ADAPTER_TAG = PlaylistAdapter::class.simpleName
	}
	
	class ItemPlaylistCallback : DiffUtil.ItemCallback<Any>() {
		override fun areItemsTheSame(oldItem: Any, newItem: Any): Boolean {
			return if (oldItem is Playlist && newItem is Playlist) oldItem.playlistId == newItem.playlistId
			else false
		}
		
		override fun areContentsTheSame(oldItem: Any, newItem: Any): Boolean {
			return if (oldItem is Playlist && newItem is Playlist) oldItem == newItem
			else false
		}
		
		override fun getChangePayload(oldItem: Any, newItem: Any): Any {
			return newItem
		}
	}
}
