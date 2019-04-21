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

import android.content.res.Resources
import androidx.core.os.ConfigurationCompat
import com.instructure.template.loginTemplate.api.UserAPI
import com.instructure.template.loginTemplate.api.models.User

object UserManager {

    @JvmStatic
    fun getSelf(callback: StatusCallback<User>) {
        val adapter = RestBuilder(callback)
        val params = RestParams(
            acceptLanguageOverride = getSystemAcceptLanguage()
        )

        UserAPI.getSelf(adapter, params, callback)
    }

    private fun getSystemAcceptLanguage(): String {
        val systemLocale = ConfigurationCompat.getLocales(Resources.getSystem().configuration)[0]
        return "${systemLocale.toLanguageTag()},${systemLocale.language}"
    }

}
