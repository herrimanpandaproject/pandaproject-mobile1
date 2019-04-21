/*
 * Copyright (C) 2019 - present Instructure, Inc.
 *
 *     Licensed under the Apache License, Version 2.0 (the "License");
 *     you may not use this file except in compliance with the License.
 *     You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 *     Unless required by applicable law or agreed to in writing, software
 *     distributed under the License is distributed on an "AS IS" BASIS,
 *     WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *     See the License for the specific language governing permissions and
 *     limitations under the License.
 */
package com.instructure.template.loginTemplate.api.apiHelpers

import android.net.http.HttpResponseCache
import com.google.gson.GsonBuilder
import com.instructure.template.loginTemplate.api.ApiPrefs
import com.instructure.template.loginTemplate.api.models.CanvasContext
import com.instructure.template.loginTemplate.utils.ContextKeeper
import okhttp3.Cache
import okhttp3.Dispatcher
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.io.File
import java.io.IOException
import java.util.concurrent.TimeUnit


/**
 * To use this adapter use RestBuilder
 */
abstract class CanvasRestAdapter
/**
 * Constructor for CanvasRestAdapter
 * @param statusCallback Only null when not making calls via callbacks. RestBuilder requires one
 */
protected constructor(var statusCallback: StatusCallback<*>?, private val authUser: String? = null) {

    private val okHttpClientNoRedirects: OkHttpClient
        get() {
            var client = okHttpClient
            client = client.newBuilder().followRedirects(false).build()

            return client
        }

    private val okHttpClientNoRetry: OkHttpClient
        get() {
            return okHttpClient.newBuilder()
                .retryOnConnectionFailure(false)
                .build()
        }

    //region Adapter Builders

    fun buildAdapterNoRedirects(params: RestParams): Retrofit {
        val params = if (params.domain.isNullOrEmpty()) params.copy(domain = ApiPrefs.fullDomain) else params

        statusCallback?.onCallbackStarted()

        //Can make this check as we KNOW that the setter doesn't allow empty strings.
        if (params.domain == "") {
            return Retrofit.Builder().baseUrl("http://invalid.domain.com/").build()
        }

        var apiContext = ""
        if (params.canvasContext != null) {
            apiContext = when (params.canvasContext.type) {
                CanvasContext.Type.COURSE -> "courses/"
                else -> "users/"
            }
        }

        // Sets the auth token, user agent, and handles masquerading.
        val restParams = params
        return Retrofit.Builder()
            .baseUrl(params.domain + params.apiVersion + apiContext)
            .addConverterFactory(GsonConverterFactory.create())
            .callFactory { request ->
                // Tag this request with the rest params so we can access them later in RequestInterceptor
                okHttpClientNoRedirects.newCall(request.newBuilder().tag(restParams).build())
            }
            .build()
    }

    fun buildAdapterSerializeNulls(params: RestParams): Retrofit {
        val params = if (params.domain.isNullOrEmpty()) params.copy(domain = ApiPrefs.fullDomain) else params

        statusCallback?.onCallbackStarted()

        // Can make this check as we KNOW that the setter doesn't allow empty strings.
        if (params.domain == "") {
            return Retrofit.Builder().baseUrl("http://invalid.domain.com/").build()
        }

        var apiContext = ""
        if (params.canvasContext != null) {
            apiContext = when (params.canvasContext.type) {
                CanvasContext.Type.COURSE -> "courses/"
                else -> "users/"
            }
        }

        // Sets the auth token, user agent, and handles masquerading.
        val restParams = params
        return Retrofit.Builder()
            .baseUrl(params.domain + params.apiVersion + apiContext)
            .addConverterFactory(GsonConverterFactory.create(GsonBuilder().serializeNulls().create()))
            .callFactory { request ->
                // Tag this request with the rest params so we can access them later in RequestInterceptor
                okHttpClient.newCall(request.newBuilder().tag(restParams).build())
            }
            .build()
    }

    fun buildAdapter(params: RestParams): Retrofit = buildAdapterHelper(
        if (params.domain.isNullOrEmpty()) params.copy(domain = ApiPrefs.fullDomain)
        else params
    )

    fun buildAdapterNoRetry(params: RestParams): Retrofit {
        val params = if (params.domain.isNullOrEmpty()) params.copy(domain = ApiPrefs.fullDomain) else params

        statusCallback?.onCallbackStarted()

        //Can make this check as we KNOW that the setter doesn't allow empty strings.
        if (params.domain == "") {
            return Retrofit.Builder().baseUrl("http://invalid.domain.com/").build()
        }

        var apiContext = ""
        if (params.canvasContext != null) {
            apiContext = when (params.canvasContext.type) {
                CanvasContext.Type.COURSE -> "courses/"
                else -> "users/"
            }
        }

        // Sets the auth token, user agent, and handles masquerading.
        val restParams = params
        //Sets the auth token, user agent, and handles masquerading.
        return Retrofit.Builder()
            .baseUrl(params.domain + params.apiVersion + apiContext)
            .addConverterFactory(GsonConverterFactory.create())
            .callFactory { request ->
                // Tag this request with the rest params so we can access them later in RequestInterceptor
                okHttpClientNoRetry.newCall(request.newBuilder().tag(restParams).build())
            }
            .build()
    }

    private fun buildAdapterHelper(params: RestParams): Retrofit {

        statusCallback?.onCallbackStarted()

        // Can make this check as we KNOW that the setter doesn't allow empty strings.
        if (params.domain == "") {
            return Retrofit.Builder().baseUrl("http://invalid.domain.com/").build()
        }

        var apiContext = ""
        if (params.canvasContext != null) {
            apiContext = when {
                params.canvasContext.type == CanvasContext.Type.COURSE -> "courses/"
                else -> "users/"
            }
        }

        return finalBuildAdapter(params, apiContext).build()
    }

    /**
     * @param params RestParams
     * @param apiContext courses, groups, sections, users, or nothing
     * @return Retrofit.Builder
     */
    @Suppress("MemberVisibilityCanBePrivate")
    protected fun finalBuildAdapter(params: RestParams, apiContext: String): Retrofit.Builder {
        //Sets the auth token, user agent, and handles masquerading.
        return Retrofit.Builder()
            .baseUrl(params.domain + params.apiVersion + apiContext)
            .addConverterFactory(GsonConverterFactory.create())
            .callFactory { request ->
                // Tag this request with the rest params so we can access them later in RequestInterceptor
                okHttpClient.newCall(request.newBuilder().tag(params).build())
            }
    }

    companion object {

        private const val DEBUG = true
        private const val TIMEOUT_IN_SECONDS = 60
        private const val CACHE_SIZE = (20 * 1024 * 1024).toLong()

        private var mHttpCacheDirectory: File? = null
        private val mDispatcher = Dispatcher()
        private var mCache: Cache? = null
        var client: OkHttpClient? = null

        val cacheDirectory: File
            get() {
                if (mHttpCacheDirectory == null) {
                    mHttpCacheDirectory = File(ContextKeeper.appContext.cacheDir, "canvasCache")
                }
                return mHttpCacheDirectory!!
            }

        @JvmStatic
        val okHttpClient: OkHttpClient
            get() {
                if (mCache == null) {
                    mCache = Cache(cacheDirectory, CACHE_SIZE)
                }

                try {
                    if (HttpResponseCache.getInstalled() == null) {
                        HttpResponseCache.install(cacheDirectory, CACHE_SIZE)
                    }
                } catch (e: IOException) { }

                if (client == null) {
                    val loggingInterceptor = HttpLoggingInterceptor()
                    loggingInterceptor.level = HttpLoggingInterceptor.Level.BODY

                    client = OkHttpClient.Builder()
                        .cache(mCache)
                        .addInterceptor(loggingInterceptor)
                        .addInterceptor(RequestInterceptor())
                        .addNetworkInterceptor(ResponseInterceptor())
                        .readTimeout(TIMEOUT_IN_SECONDS.toLong(), TimeUnit.SECONDS)
                        .dispatcher(mDispatcher)
                        .build()
                }

                return client!!
            }
        //endregion

        fun cancelAllCalls() {
            mDispatcher.cancelAll()
        }
    }

}

