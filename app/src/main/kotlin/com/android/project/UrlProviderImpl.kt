package com.android.project

import com.android.network.impl.services.UrlProvider
import org.koin.core.annotation.Single

@Single
class UrlProviderImpl(): UrlProvider {
    override fun getProtocol(): String  = BuildConfig.PROTOCOL
    override fun getUrl(): String = BuildConfig.BASE_URL
    override fun apiKey(): String  = BuildConfig.API_KEY
}
