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
import com.example.rageamp.data.model.Album
import com.example.rageamp.databinding.ItemAlbumBinding
import com.example.rageamp.utils.GlideUtils
import com.example.rageamp.utils.Logger
import java.util.concurrent.Executors

class AlbumAdapter(
	private val onClickItemAlbum: (Album) -> Unit,
	private val onLongClickItemAlbum: (Album, View) -> Unit,
) : ListAdapter<Any, RecyclerView.ViewHolder>(
	AsyncDifferConfig.Builder(ItemAlbumCallback())
		.setBackgroundThreadExecutor(Executors.newSingleThreadExecutor())
		.build()
) {
	
	inner class AlbumViewHolder(
		private val binding: ItemAlbumBinding,
		private val onClickItemAlbum: (Album) -> Unit,
	) : RecyclerView.ViewHolder(binding.root) {
		
		init {
			setupUIItemAlbum(itemView)
		}
		
		fun bind(album: Album) {
			Logger.d(ALBUM_VIEW_HOLDER_TAG, "bind album: $album")
			binding.tvNameAlbum.text = album.name
			val pluralResource =
				if (album.songs.size > 1) R.string.songs_with_placeholder else R.string.song_with_placeholder
			binding.tvNumberOfSongs.text =
				itemView.context.getString(pluralResource, album.songs.size)
			
			if (album.songs.isNotEmpty()) {
				GlideUtils.loadImageFromUrl(
					image = binding.ivAlbum,
					url = album.songs[0].albumArt,
					placeholderResId = R.drawable.image_default_playlist
				)
			} else {
				binding.ivAlbum.setImageResource(R.drawable.image_default_playlist)
			}
			
			binding.root.setOnClickListener {
				onClickItemAlbum.invoke(album)
			}
			
			binding.root.setOnLongClickListener { view ->
				onLongClickItemAlbum.invoke(album, view)
				true
			}
			
		}
		
		private fun setupUIItemAlbum(view: View) {
			Logger.d(ALBUM_VIEW_HOLDER_TAG, "setupUIItemAlbum")
			val screenWidth = Resources.getSystem().displayMetrics.widthPixels / 2
			val margin = view.context.resources.getDimensionPixelOffset(R.dimen.dp32)
			val itemWidth = (screenWidth - 3 * margin) / 2
			
			when (val layoutParams = view.layoutParams) {
				is GridLayoutManager.LayoutParams -> {
					layoutParams.marginEnd = margin
					
					view.post {
						binding.clRoot.layoutParams.width = itemWidth
						view.requestLayout()
					}
				}
			}
		}
	}
	
	override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
		return AlbumViewHolder(
			binding = ItemAlbumBinding.inflate(
				LayoutInflater.from(parent.context),
				parent,
				false
			),
			onClickItemAlbum = onClickItemAlbum,
		)
	}
	
	override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
		val item = getItem(position)
		when (holder) {
			is AlbumViewHolder -> {
				val album = item as? Album ?: return
				holder.bind(album)
			}
		}
	}
	
	companion object {
		private val ALBUM_VIEW_HOLDER_TAG = AlbumViewHolder::class.simpleName
	}
	
	class ItemAlbumCallback : DiffUtil.ItemCallback<Any>() {
		override fun areItemsTheSame(oldItem: Any, newItem: Any): Boolean {
			return if (oldItem is Album && newItem is Album) oldItem == newItem
			else false
		}
		
		override fun areContentsTheSame(oldItem: Any, newItem: Any): Boolean {
			return if (oldItem is Album && newItem is Album) oldItem == newItem
			else false
		}
		
		override fun getChangePayload(oldItem: Any, newItem: Any): Any {
			return newItem
		}
	}
}
