package com.example.rageamp.utils

import timber.log.Timber

object Logger {
	private const val TAG = "RAGE_AMP"
	fun v(tag: String? = "", msg: String?, vararg objects: Any?) {
		Timber.tag(TAG).v("[${tag}]: $msg", *objects)
	}
	
	fun d(tag: String? = "", msg: String?, vararg objects: Any?) {
		Timber.tag(TAG).d("[${tag}]: $msg", *objects)
	}
	
	fun i(tag: String? = "", msg: String?, vararg objects: Any?) {
		Timber.tag(TAG).i("[${tag}]: $msg", *objects)
	}
	
	fun w(tag: String? = "", msg: String?, vararg objects: Any?) {
		Timber.tag(TAG).w("[${tag}]: $msg", *objects)
	}
	
	fun e(tag: String?, msg: String?, throwable: Throwable? = null, vararg objects: Any?) {
		Timber.tag(TAG).e(throwable, "[${tag}]: $msg", *objects)
	}
	
	init {
		if (Timber.treeCount == 0) {
			Timber.plant(Timber.DebugTree())
		}
	}
}
