package com.android.project

import android.app.Application
import coil.ImageLoader
import coil.ImageLoaderFactory
import coil.decode.GifDecoder
import coil.decode.ImageDecoderDecoder
import com.android.datastore.impl.di.DataStoreModule
import com.android.dispatchers.provider.di.DispatchersModule
import com.android.giphy.domain.di.GiphyModule
import com.android.network.impl.di.DataModule
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.startKoin

import org.koin.ksp.generated.*

class App: Application(), ImageLoaderFactory {

    override fun onCreate() {
        super.onCreate()
        startKoin{
            androidLogger()
            androidContext(this@App)
            modules(modules)
        }
    }

    override fun newImageLoader(): ImageLoader {
        return ImageLoader.Builder(this)
            .components {
                // Add GIF support
                if (android.os.Build.VERSION.SDK_INT >= 28) {
                    add(ImageDecoderDecoder.Factory())
                } else {
                    add(GifDecoder.Factory())
                }
            }
            .crossfade(true)
            .build()
    }

    private val modules = listOf(
        AppModule().module,
        DataModule().module,
        DispatchersModule().module,
        DataStoreModule().module,
        GiphyModule().module
    )
}