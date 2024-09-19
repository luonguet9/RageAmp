package com.example.rageamp.ui.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncDifferConfig
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.bumptech.glide.request.RequestOptions
import com.example.rageamp.data.model.Song
import com.example.rageamp.databinding.ItemSongBinding
import com.example.rageamp.utils.GlideUtils
import java.util.concurrent.Executors

class SongAdapter(
	private val onClickItemSong: (Song) -> Unit,
) : ListAdapter<Any, ViewHolder>(
	AsyncDifferConfig.Builder(ItemSongCallback())
		.setBackgroundThreadExecutor(Executors.newSingleThreadExecutor())
		.build()
) {
	
	inner class SongViewHolder(
		private val binding: ItemSongBinding
	) : ViewHolder(binding.root) {
		
		fun bind(song: Song) {
			binding.song = song
			binding.executePendingBindings()
			
			GlideUtils.loadImageFromUrl(
				image = binding.ivSong,
				url = song.albumArt,
				options = RequestOptions().transform(RoundedCorners(16)),
			)
			
			binding.root.setOnClickListener {
				onClickItemSong.invoke(song)
			}
		}
	}
	override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SongViewHolder {
		return SongViewHolder(
			binding = ItemSongBinding.inflate(
				LayoutInflater.from(parent.context),
				parent,
				false
			)
		)
	}
	
	override fun onBindViewHolder(holder: ViewHolder, position: Int) {
		val item = getItem(position)
		when (holder) {
			is SongViewHolder -> {
				val song = item as? Song ?: return
				holder.bind(song)
			}
		}
	}
	
	class ItemSongCallback : DiffUtil.ItemCallback<Any>() {
		override fun areItemsTheSame(oldItem: Any, newItem: Any): Boolean {
			return if (oldItem is Song && newItem is Song) oldItem.id == newItem.id
			else false
		}
		
		override fun areContentsTheSame(oldItem: Any, newItem: Any): Boolean {
			return if (oldItem is Song && newItem is Song) oldItem == newItem
			else false
		}
		
		override fun getChangePayload(oldItem: Any, newItem: Any): Any {
			return newItem
		}
	}
}
