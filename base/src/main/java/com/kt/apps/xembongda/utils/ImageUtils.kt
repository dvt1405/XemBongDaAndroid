package com.kt.apps.xembongda.utils

import android.graphics.drawable.Drawable
import android.widget.ImageView
import androidx.core.content.ContextCompat
import androidx.databinding.BindingAdapter
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.Target
import com.kt.apps.xembongda.base.R

fun ImageView.loadImage(url: Int) {
    scaleType = ImageView.ScaleType.CENTER_CROP
    Glide.with(this)
        .load(url)
        .placeholder(R.drawable.avatar_on_error)
        .addListener(object : RequestListener<Drawable> {
            override fun onLoadFailed(e: GlideException?, model: Any?, target: Target<Drawable>?, isFirstResource: Boolean): Boolean {
                this@loadImage.setImageDrawable(ContextCompat.getDrawable(this@loadImage.context, R.drawable.avatar_on_error))
                return false
            }

            override fun onResourceReady(
                resource: Drawable?,
                model: Any?,
                target: Target<Drawable>?,
                dataSource: DataSource?,
                isFirstResource: Boolean
            ): Boolean {
                scaleType = ImageView.ScaleType.CENTER
                return false
            }

        })
        .into(this)
}
