package me.laotang.carry.glide

import android.content.Context
import com.bumptech.glide.Glide
import com.bumptech.glide.GlideBuilder
import com.bumptech.glide.Registry
import com.bumptech.glide.annotation.GlideModule
import com.bumptech.glide.load.engine.bitmap_recycle.LruBitmapPool
import com.bumptech.glide.load.engine.cache.DiskLruCacheWrapper
import com.bumptech.glide.load.engine.cache.LruResourceCache
import com.bumptech.glide.load.engine.cache.MemorySizeCalculator
import com.bumptech.glide.load.model.GlideUrl
import com.bumptech.glide.module.AppGlideModule
import me.laotang.carry.di.GlobalEntryPoint
import java.io.File
import java.io.InputStream

@GlideModule(glideName = "GlideQuick")
class GlideConfiguration : AppGlideModule() {
    private var IMAGE_DISK_CACHE_MAX_SIZE = 100 * 1024 * 1024//图片缓存文件最大值为100Mb

    override fun applyOptions(context: Context, builder: GlideBuilder) {
        val cacheFileDir: File = context.externalCacheDir ?: context.cacheDir
        val cacheFile = File(cacheFileDir, "Glide")
        builder.setDiskCache {
            DiskLruCacheWrapper.create(
                cacheFile,
                IMAGE_DISK_CACHE_MAX_SIZE.toLong()
            )
        }

        val calculator = MemorySizeCalculator.Builder(context).build()
        val defaultMemoryCacheSize = calculator.memoryCacheSize
        val defaultBitmapPoolSize = calculator.bitmapPoolSize

        val customMemoryCacheSize = (1.2 * defaultMemoryCacheSize).toInt()
        val customBitmapPoolSize = (1.2 * defaultBitmapPoolSize).toInt()

        builder.setMemoryCache(LruResourceCache(customMemoryCacheSize.toLong()))
        builder.setBitmapPool(LruBitmapPool(customBitmapPoolSize.toLong()))

    }

    override fun registerComponents(context: Context, glide: Glide, registry: Registry) {
        try {
            if (hasOkHttp) {
                val okHttpClient = GlobalEntryPoint.getOkHttpClient(context)
                registry.replace(
                    GlideUrl::class.java,
                    InputStream::class.java,
                    OkHttpUrlLoader.Factory(okHttpClient)
                )
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    override fun isManifestParsingEnabled() = false

    companion object {

        private var definedGlobalEntryPoint = false

        val hasOkHttp
            get() = definedGlobalEntryPoint

        init {
            try {
                Class.forName("me.laotang.carry.di.GlobalEntryPoint")
                definedGlobalEntryPoint = true
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }
}