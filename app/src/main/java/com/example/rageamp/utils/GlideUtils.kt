package com.example.rageamp.utils

import android.widget.ImageView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.example.rageamp.R

object GlideUtils {
    fun loadImageFromUrl(
        image: ImageView,
        url: Any?,
        options: RequestOptions? = null,
        placeholderResId: Int? = R.drawable.image_default_song
    ) {
        val glideRequest = Glide.with(image.context)
            .load(url)
        
        options?.let { glideRequest.apply(it) }
        placeholderResId?.let { glideRequest.placeholder(it) }
        
        glideRequest.into(image)
    }
    

}
