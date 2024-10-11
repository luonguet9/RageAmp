package com.example.rageamp.ui.dialog

import android.view.Gravity
import com.example.rageamp.R
import com.example.rageamp.base.BaseDialog
import com.example.rageamp.databinding.DialogRenamePlaylistBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class RenamePlaylistDialog : BaseDialog<DialogRenamePlaylistBinding>() {
	
	private var renamePlaylistCallback: ((name: String) -> Unit)? = null
	
	override fun getLayoutResource(): Int = R.layout.dialog_rename_playlist
	
	override fun getGravityForDialog(): Int = Gravity.CENTER
	
	override fun initView() {
	
	}
	
	override fun initListener() {
		binding.btCancel.setOnClickListener {
			dismiss()
		}
		
		binding.btRename.setOnClickListener {
			val newName = binding.edtNamePlaylist.text.toString().trim()
			if (newName.isNotEmpty()) {
				renamePlaylistCallback?.invoke(newName)
				dismiss()
			} else {
				binding.textField.error = getString(R.string.playlist_action_error)
			}
		}
	}
	
	fun renamePlaylistCallback(callback: ((newName: String) -> Unit)? = null) {
		renamePlaylistCallback = callback
	}
}
