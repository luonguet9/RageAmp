package com.example.rageamp.utils

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Color
import android.graphics.drawable.Drawable
import android.widget.ImageView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.bumptech.glide.request.target.CustomTarget
import com.bumptech.glide.request.transition.Transition
import com.example.rageamp.R

object GlideUtils {
    fun loadImageFromUrl(
        image: ImageView,
        url: Any?,
        options: RequestOptions? = null,
        placeholderResId: Int? = R.drawable.image_default_song
    ) {
        Logger.i("loadImageFromUrl url: $url")
        val glideRequest = Glide.with(image.context)
            .load(url)
        
        options?.let { glideRequest.apply(it) }
        placeholderResId?.let { glideRequest.placeholder(it) }
        
        glideRequest.into(image)
    }
    
    fun loadImageFromUrlWithCallback(
        context: Context,
        url: String?,
        onFinished: (Bitmap) -> Unit
    ) {
        Glide.with(context)
            .asBitmap()
            .load(url)
            .placeholder(R.drawable.image_default_song)
            .error(R.drawable.image_default_song)
            .into(object : CustomTarget<Bitmap>() {
                override fun onResourceReady(resource: Bitmap, transition: Transition<in Bitmap>?) {
                    Logger.i("onResourceReady: resource: $resource")
                    onFinished(resource)
                }
                
                override fun onLoadCleared(placeholder: Drawable?) {}
                
                override fun onLoadFailed(errorDrawable: Drawable?) {
                    super.onLoadFailed(errorDrawable)
                    Logger.w("onLoadFailed: errorDrawable: $errorDrawable")
                    /*val bitmap = (errorDrawable as BitmapDrawable).bitmap
                    onFinished(bitmap)*/
                    val bitmap = Bitmap.createBitmap(100, 100, Bitmap.Config.ARGB_8888)
                    bitmap.eraseColor(Color.GRAY)
                    onFinished(bitmap)
                }
            })
    }
    
}
