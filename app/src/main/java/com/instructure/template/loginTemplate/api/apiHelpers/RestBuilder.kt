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

class RestBuilder(callback: StatusCallback<*> = object : StatusCallback<Any>(){}, authUser: String? = null) : CanvasRestAdapter(callback, authUser) {

    fun <T> build(clazz: Class<T>, params: RestParams): T {
        val restParams = params.copy(isForceReadFromCache = false)
        val restAdapter = buildAdapter(restParams)

        return restAdapter.create(clazz)
    }
    companion object {

        @JvmStatic
        fun clearCacheDirectory(): Boolean {
            return try {
                CanvasRestAdapter.cacheDirectory.delete()
            } catch (e: Exception) {
                false
            }
        }
    }
}

