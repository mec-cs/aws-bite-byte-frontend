package com.chattingapp.foodrecipeuidemo.coil

import android.content.Context
import coil.ImageLoader
import coil.disk.DiskCache
import coil.memory.MemoryCache

class CoilSetup(private val context: Context) {

    val imageLoader: ImageLoader by lazy {
        ImageLoader.Builder(context)
            .memoryCache {
                MemoryCache.Builder(context)
                    .maxSizePercent(0.25) // Use 25% of the available memory
                    .build()
            }
            .diskCache {
                DiskCache.Builder()
                    .directory(context.cacheDir.resolve("image_cache")) // Cache directory
                    .maxSizeBytes(50 * 1024 * 1024) // 50 MB cache size
                    .build()
            }
            .build()
    }
}
