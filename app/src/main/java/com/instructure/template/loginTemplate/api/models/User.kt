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
package com.instructure.template.loginTemplate.api.models

import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize
import java.util.ArrayList


@Parcelize
data class User(
    override val id: Long = 0,
    override val name: String = "",
    @SerializedName("short_name")
    val shortName: String? = null,
    @SerializedName("login_id")
    val loginId: String? = null,
    @SerializedName("avatar_url")
    var avatarUrl: String? = null,
    @SerializedName("primary_email")
    val primaryEmail: String? = null,
    val email: String? = null,
    @SerializedName("sortable_name")
    val sortableName: String? = null,
    val bio: String? = null,
    val enrollments: List<Enrollment> = ArrayList(),
    // Helper variable for the "specified" enrollment.
    val enrollmentIndex: Int = 0,
    @SerializedName("last_login")
    private val lastLogin: String? = null,
    val locale: String? = null,
    @SerializedName("effective_locale")
    val effective_locale: String? = null
) : CanvasContext() {
    override val type get() = Type.USER
}

