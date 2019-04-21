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

import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.PackageManager
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.net.NetworkRequest
import android.text.TextUtils
import com.instructure.template.loginTemplate.api.ApiPrefs
import com.instructure.template.loginTemplate.utils.ContextKeeper
import okhttp3.Headers
import okhttp3.MediaType
import okhttp3.RequestBody
import retrofit2.Response
import java.text.SimpleDateFormat
import java.util.*


object APIHelper {

    // Lazy init so we don't break unit tests
    private val networkRequest by lazy {
        NetworkRequest.Builder()
            .addTransportType(NetworkCapabilities.TRANSPORT_CELLULAR)
            .addTransportType(NetworkCapabilities.TRANSPORT_WIFI)
            .build()
    }

    private fun getConnectivityManager(): ConnectivityManager {
        return ContextKeeper.appContext.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
    }

    @SuppressLint("MissingPermission")
    fun hasNetworkConnection(): Boolean {
        val netInfo = getConnectivityManager().activeNetworkInfo
        return netInfo != null && netInfo.isConnectedOrConnecting
    }

    /**
     * parseLinkHeaderResponse parses HTTP headers to return the first, next, prev, and last urls. Used for pagination.
     *
     * @param headers List of headers
     * @return A LinkHeaders object
     */
    fun parseLinkHeaderResponse(headers: Headers): LinkHeaders {
        val linkHeaders = LinkHeaders()

        val map = headers.toMultimap()

        for (name in map.keys) {
            if ("link".equals(name, ignoreCase = true)) {
                for (value in map[name]!!) {
                    val split = value.split(",".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
                    for (segment in split) {
                        val index = segment.indexOf(">")
                        var url: String? = segment.substring(0, index)
                        url = url!!.substring(1)

                        //Remove the domain, keep the encoding (for time zones MBL-11262)
                        url = removeDomainFromUrl(url)

                        when {
                            segment.contains("rel=\"next\"") -> linkHeaders.nextUrl = url
                            segment.contains("rel=\"prev\"") -> linkHeaders.prevUrl = url
                            segment.contains("rel=\"first\"") -> linkHeaders.firstUrl = url
                            segment.contains("rel=\"last\"") -> linkHeaders.lastUrl = url
                        }
                    }
                }
                break
            }
        }

        return linkHeaders
    }

    /**
     * removeDomainFromUrl is a helper function for removing the domain from a url. Used for pagination/routing
     *
     * @param url A url
     * @return a String without a domain
     */
    fun removeDomainFromUrl(url: String?): String? = url?.substringAfter("/api/v1/")

    fun isCachedResponse(response: okhttp3.Response): Boolean = response.cacheResponse() != null

    fun generateUserAgent(context: Context, userAgentString: String): String {
        var userAgent: String
        try {
            userAgent = userAgentString + "/" +
                    context.packageManager.getPackageInfo(context.packageName, 0).versionName +
                    " (" + context.packageManager.getPackageInfo(context.packageName, 0).versionCode + ")"
        } catch (e: PackageManager.NameNotFoundException) {
            userAgent = userAgentString
        }

        return userAgent
    }

}

