package com.example.rageamp.ui.dialog

import android.view.Gravity
import com.example.rageamp.R
import com.example.rageamp.base.BaseDialog
import com.example.rageamp.databinding.DialogDeletePlaylistBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class DeletePlaylistDialog : BaseDialog<DialogDeletePlaylistBinding>() {
	
	private var deletePlaylistCallback: (() -> Unit)? = null
	
	override fun getLayoutResource(): Int = R.layout.dialog_delete_playlist
	
	override fun getGravityForDialog(): Int = Gravity.CENTER
	
	override fun initView() {
	
	}
	
	override fun initListener() {
		binding.btCancel.setOnClickListener {
			dismiss()
		}
		
		binding.btDelete.setOnClickListener {
			deletePlaylistCallback?.invoke()
			dismiss()
		}
	}
	
	fun deletePlaylistCallback(callback: (() -> Unit)? = null) {
		deletePlaylistCallback = callback
	}
}
