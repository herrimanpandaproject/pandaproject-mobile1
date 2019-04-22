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
package com.instructure.template.loginTemplate.api

import android.webkit.URLUtil
import com.instructure.template.loginTemplate.api.apiHelpers.RestBuilder
import com.instructure.template.loginTemplate.api.models.User
import com.instructure.template.loginTemplate.utils.*
import java.io.File


/** Preference file name **/
const val PREFERENCE_FILE_NAME = "canvas-kit-sp"

/**
 * Canvas API preferences containing data required for most core networking such as
 * the school domain, protocol, auth token, and user object.
 *
 * All public properties of ApiPrefs should be cached in memory (after any initial loading), so it is
 * safe to access these properties in hot code paths like View.OnDraw() and RecyclerView binders.
 */
@Suppress("unused", "UNUSED_PARAMETER")
object ApiPrefs : PrefManager(PREFERENCE_FILE_NAME) {

    @JvmStatic
    var token by StringPref()

    @JvmStatic
    var protocol by StringPref("https", "api_protocol")

    @JvmStatic
    var userAgent by StringPref("pandaprojectm1")

    @JvmStatic
    var perPageCount = 100

    internal var originalDomain by StringPref("", "domain")
    private var originalUser: User? by GsonPref(
        User::class.java,
        null,
        "user"
    )

    @JvmStatic
    var domain: String
        get() = originalDomain
        set(newDomain) {
            val strippedDomain = newDomain.replaceFirst(Regex("https?://"), "").removeSuffix("/")
            originalDomain = strippedDomain
        }

    @JvmStatic
    val fullDomain: String
        get() = when {
            domain.isBlank() || protocol.isBlank() -> ""
            URLUtil.isHttpUrl(domain) || URLUtil.isHttpsUrl(domain) -> domain
            else -> "$protocol://$domain"
        }

    @JvmStatic
    var user: User?
        get() = originalUser
        set(newUser) {
            originalUser = newUser
        }


    /**
     * clearAllData is required for logout.
     * Clears all data including credentials and cache.
     * @return true if caches files were deleted
     */
    @JvmStatic
    fun clearAllData(): Boolean {
        // Clear preferences
        clearPrefs()

        // Clear http cache
        RestBuilder.clearCacheDirectory()

        // Clear file cache
        val cacheDir = File(ContextKeeper.appContext.filesDir, FileUtils.FILE_DIRECTORY)
        return FileUtils.deleteAllFilesInDirectory(cacheDir)
    }
}

