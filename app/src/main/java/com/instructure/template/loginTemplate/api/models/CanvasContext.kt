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

import java.util.*

abstract class CanvasContext : CanvasModel<CanvasContext>() {
    abstract val name: String?
    abstract val type: Type
    abstract override val id: Long

    override val comparisonDate: Date? get() = null
    override val comparisonString: String? get() = name

    /**
     * Make sure they have the same type and the same ID.
     */
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other == null || javaClass != other.javaClass) return false
        if (!super.equals(other)) return false

        val that = other as CanvasContext

        return !(type != that.type || id != that.id)
    }

    enum class Type(val apiString: String) {
        COURSE("courses"),
        USER("users");

        companion object {
            fun isCourse(canvasContext: CanvasContext?): Boolean = COURSE == canvasContext?.type
            fun isUser(canvasContext: CanvasContext?): Boolean = USER == canvasContext?.type
        }
    }

    companion object {

        fun getGenericContext(type: Type, id: Long, name: String = ""): CanvasContext =
            when (type) {
                Type.USER -> User(
                    id = id,
                    name = name
                )
                Type.COURSE -> Course(
                    id = id,
                    name = name
                )
            }
    }
}