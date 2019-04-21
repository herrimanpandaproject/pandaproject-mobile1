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
import java.util.*
import kotlinx.android.parcel.Parcelize

@Parcelize
data class Course(
    override val id: Long = 0,
    override var name: String = "",
    @SerializedName("original_name")
    var originalName: String? = null,
    @SerializedName("course_code")
    val courseCode: String? = null,
    @SerializedName("start_at")
    val startAt: String? = null,
    @SerializedName("end_at")
    val endAt: String? = null,
    @SerializedName("syllabus_body")
    val syllabusBody: String? = null,
    var enrollments: MutableList<Enrollment>? = ArrayList(),
    @SerializedName("is_favorite")
    var isFavorite: Boolean = false,
    @SerializedName("access_restricted_by_date")
    val accessRestrictedByDate: Boolean = false
) : CanvasContext(), Comparable<CanvasContext> {
    override val type: Type get() = Type.COURSE
}

