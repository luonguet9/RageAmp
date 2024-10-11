package com.example.rageamp.ui.dialog

import android.view.Gravity
import com.example.rageamp.R
import com.example.rageamp.base.BaseDialog
import com.example.rageamp.databinding.DialogAddPlaylistBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class AddNewPlaylistDialog : BaseDialog<DialogAddPlaylistBinding>() {
	
	private var addPlaylistCallback: ((playlistName: String) -> Unit)? = null
	
	override fun getLayoutResource(): Int = R.layout.dialog_add_playlist
	
	override fun getGravityForDialog(): Int = Gravity.CENTER
	
	override fun initView() {
	
	}
	
	override fun initListener() {
		binding.btCancel.setOnClickListener {
			dismiss()
		}
		
		binding.btCreate.setOnClickListener {
			val playlistName = binding.edtNamePlaylist.text.toString().trim()
			if (playlistName.isNotEmpty()) {
				addPlaylistCallback?.invoke(playlistName)
				dismiss()
			} else {
				binding.textField.error = getString(R.string.playlist_action_error)
			}
		}
	}
	
	fun addPlaylistCallback(callback: ((playlistName: String) -> Unit)? = null) {
		addPlaylistCallback = callback
	}
}
