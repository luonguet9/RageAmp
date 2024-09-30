package com.example.rageamp.utils

import java.text.SimpleDateFormat
import java.util.Locale

fun timeFormatter(): SimpleDateFormat {
	return SimpleDateFormat("mm:ss", Locale.getDefault())
}
