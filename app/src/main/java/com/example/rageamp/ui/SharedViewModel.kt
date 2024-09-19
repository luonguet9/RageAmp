package com.example.rageamp.ui

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.rageamp.utils.enums.NavigationAction
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class SharedViewModel @Inject constructor(

) : ViewModel() {
	private val _navigationAction = MutableLiveData<NavigationAction>()
	val navigationAction: LiveData<NavigationAction> get() = _navigationAction
	
	fun navigate(action: NavigationAction) {
		_navigationAction.value = action
	}
	
	fun resetNavigation() {
		_navigationAction.value = null
	}
}
