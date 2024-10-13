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
	
	fun v(msg: String?, vararg objects: Any?) {
		Timber.tag(TAG).v("[${getTag()}]: $msg", *objects)
	}
	
	fun d(msg: String?, vararg objects: Any?) {
		Timber.tag(TAG).d("[${getTag()}]: $msg", *objects)
	}
	
	fun i(msg: String?, vararg objects: Any?) {
		Timber.tag(TAG).i("[${getTag()}]: $msg", *objects)
	}
	
	fun w(msg: String?, vararg objects: Any?) {
		Timber.tag(TAG).w("[${getTag()}]: $msg", *objects)
	}
	
	fun e(msg: String?, throwable: Throwable? = null, vararg objects: Any?) {
		Timber.tag(TAG).e(throwable, "[${getTag()}]: $msg", *objects)
	}
	
	private fun getTag(): String {
		return Throwable().stackTrace
			.first { it.fileName != "Logger.kt" }
			.fileName
			.removeSuffix(".kt")
	}
	
	init {
		if (Timber.treeCount == 0) {
			Timber.plant(Timber.DebugTree())
		}
	}
}
