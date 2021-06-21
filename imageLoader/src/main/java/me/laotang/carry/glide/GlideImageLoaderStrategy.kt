package me.laotang.carry.glide

import android.graphics.drawable.Drawable
import android.view.View
import android.widget.ImageView
import com.bumptech.glide.TransitionOptions
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.bumptech.glide.request.target.ImageViewTarget
import com.bumptech.glide.request.target.ViewTarget
import com.bumptech.glide.request.transition.Transition
import me.laotang.carry.core.imageloader.*

class GlideImageLoaderStrategy :
    ImageLoaderStrategy<ImageLoaderViewTarget<*>> {
    override fun load(view: View, viewTarget: ImageLoaderViewTarget<*>) {
        val requests = when (viewTarget) {
            is UrlImageLoaderViewTarget -> {
                GlideQuick.with(view.context)
                    .load(viewTarget.scr)
            }
            is FileImageLoaderViewTarget -> {
                GlideQuick.with(view.context)
                    .load(viewTarget.scr)
            }
            is ResImageLoaderViewTarget -> {
                GlideQuick.with(view.context)
                    .load(viewTarget.scr)
            }
            is UriImageLoaderViewTarget -> {
                GlideQuick.with(view.context)
                    .load(viewTarget.scr)
            }
            else -> {
                null
            }
        } ?: return
        loadImageConfig(requests, viewTarget)
        if (view is ImageView) {
            requests.into(object : ImageViewTarget<Drawable>(view) {
                override fun onResourceReady(
                    resource: Drawable,
                    transition: Transition<in Drawable>?
                ) {
                    super.onResourceReady(resource, transition)
                    viewTarget.onResourceReady(resource)
                }

                override fun onLoadFailed(errorDrawable: Drawable?) {
                    super.onLoadFailed(errorDrawable)
                    viewTarget.onLoadFailed(errorDrawable)
                }

                override fun onLoadStarted(placeholder: Drawable?) {
                    super.onLoadStarted(placeholder)
                    viewTarget.onLoadStarted(placeholder)
                }

                override fun setResource(resource: Drawable?) {
                    view.setImageDrawable(resource)
                }
            })
        } else {
            requests.into(object : ViewTarget<View, Drawable>(view) {
                override fun onResourceReady(
                    resource: Drawable,
                    transition: Transition<in Drawable>?
                ) {
                    viewTarget.onResourceReady(resource)
                    view.setBackgroundDrawable(resource)
                }

                override fun onLoadFailed(errorDrawable: Drawable?) {
                    super.onLoadFailed(errorDrawable)
                    viewTarget.onLoadFailed(errorDrawable)
                }

                override fun onLoadStarted(placeholder: Drawable?) {
                    super.onLoadStarted(placeholder)
                    viewTarget.onLoadStarted(placeholder)
                }
            })
        }
    }

    override fun clear(view: View) {
        GlideQuick.get(view.context).requestManagerRetriever.get(view.context).clear(view)
    }

    private fun <T> loadImageConfig(
        request: GlideRequest<T>,
        viewTarget: ImageLoaderViewTarget<*>
    ) {
        request.apply {
            when (viewTarget.cacheStrategy) {
                //缓存策略
                0 -> diskCacheStrategy(DiskCacheStrategy.ALL)
                1 -> diskCacheStrategy(DiskCacheStrategy.NONE)
                2 -> diskCacheStrategy(DiskCacheStrategy.RESOURCE)
                3 -> diskCacheStrategy(DiskCacheStrategy.DATA)
                4 -> diskCacheStrategy(DiskCacheStrategy.AUTOMATIC)
                else -> diskCacheStrategy(DiskCacheStrategy.ALL)
            }
            if (viewTarget.isCenterCrop) {
                centerCrop()
            }
            if (viewTarget.isCircle) {
                circleCrop()
            }
            if (viewTarget.isImageRadius()) {
                transform(RoundedCorners(viewTarget.imageRadius))
            }
            if (viewTarget.placeholder > 0) {
                placeholder(viewTarget.placeholder)
            }
            if (viewTarget.errorPic > 0) {
                error(viewTarget.errorPic)
            }
            if (viewTarget.targetWidth > 0 && viewTarget.targetHeight > 0) {
                override(viewTarget.targetWidth, viewTarget.targetHeight)
            }
            if (viewTarget.crossFade) {
                transition(DrawableTransitionOptions.withCrossFade() as TransitionOptions<*, T>)
            }
        }
    }
}