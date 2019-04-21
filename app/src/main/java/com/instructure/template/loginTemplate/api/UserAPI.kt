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

import com.instructure.template.loginTemplate.api.apiHelpers.RestBuilder
import com.instructure.template.loginTemplate.api.apiHelpers.RestParams
import com.instructure.template.loginTemplate.api.apiHelpers.StatusCallback
import com.instructure.template.loginTemplate.api.models.User
import retrofit2.Call
import retrofit2.http.GET

object UserAPI {

    internal interface UsersInterface {

        @GET("users/self/profile")
        fun getSelf(): Call<User>

    }

    fun getSelf(adapter: RestBuilder, params: RestParams, callback: StatusCallback<User>) {
        callback.addCall(adapter.build(UsersInterface::class.java, params).getSelf()).enqueue(callback)
    }

}
