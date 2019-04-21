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
package com.instructure.template.loginTemplate.utils

import android.annotation.SuppressLint
import android.content.Context
import android.content.SharedPreferences
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlin.properties.ReadWriteProperty
import kotlin.reflect.KClass
import kotlin.reflect.KMutableProperty0
import kotlin.reflect.KProperty


/**
 * [PrefManager] is a small [SharedPreferences] wrapper that aims to provide a type-safe,
 * convenient, and centralized approach to key-value persistence. No need to define dozens
 * of string constants for key names, spin up a new [SharedPreferences] instance for
 * every get/set, or figure out how to obtain a Context object in utility methods. [PrefManager]
 * leverages the power of delegated properties in Kotlin to specify the key name, value type,
 * getter, setter, and default value in a single line.
 *
 * Generally, concrete implementations of this class should be objects (rather than subclasses),
 * and generally there should only exist one PrefManager per backing preference file. The name
 * of preference file should be passed as the sole constructor parameter of [PrefManager].
 *
 * To create preference properties inside a [PrefManager], use any delegate of type [Pref], such
 * as [StringPref], [BooleanPref], [IntPref], etc. For example:
 *
 * ```
 * var myPref by StringPref()
 * ```
 *
 * For most delegate classes the property name is used as the [SharedPreferences] key name, and
 * a sane value is used as the default value. In the example above the key name is 'myPref'
 * and the default value is an empty String. However, most delegates also allow you to manually
 * specify the key name and default value:
 *
 * ```
 * var myPref by StringPref("default value", "other_key_name")
 * ```
 *
 * You may also create your own delegate classes by subclassing [Pref] and implementing
 * the 'getValue()' and 'setValue()' extension functions.
 *
 * Here is an example of a complete, simple PrefManager implementation:
 *
 * ```
 * object MyPrefs : PrefManager("my_prefs_file_name") {
 *
 *     var name by StringPref()
 *
 *     var age by IntPref()
 *
 *     override fun onClearPrefs() { } // Required override. See [onClearPrefs]
 *
 * }
 * ```
 *
 * In another file you could then access these shared preferences like so:
 *
 * ```
 * val userAge = MyPrefs.age
 * val userName = MyPrefs.name
 * MyPrefs.age = 25
 * MyPrefs.name = "Awesome Dev Guy"
 * ```
 */
@SuppressLint("CommitPrefEdits")
abstract class PrefManager(prefFileName: String) {

    /* The [SharedPreferences] instance which serves as the core persistence mechanism */
    internal val prefs by lazy {
        ContextKeeper.appContext.getSharedPreferences(prefFileName, Context.MODE_PRIVATE)
    }

    /* A [SharedPreferences.Editor] instance of [prefs], used for adding/updating values */
    internal val editor by lazy { prefs.edit() }

    val delegates = arrayListOf<Pref<*>>()

    internal fun registerDelegate(delegate: Pref<*>) {
        delegates += delegate
    }

    /**
     * Because [PrefManager] does not require its properties to be directly bound to the backing
     * [SharedPreferences] instance through [Pref] delegates, simply clearing the shared prefs
     * cannot guarantee that all properties will be properly reset. This function provides the
     * opportunity to manually clear properties or perform other logic, and will be executed
     * when either [clearPrefs] or [safeClearPrefs] are invoked.
     *
     * NOTE: properties preserved during the [safeClearPrefs] process will be unaffected by any
     * changes made during the execution of this function.
     */
    protected open fun onClearPrefs() {}

    /**
     * Clears this PrefManager's backing [SharedPreferences] file. This is only guaranteed to clear
     * properties which have been delegated to an instance of the [Pref] class. Which properties
     * are reset is determined by the specific [PrefManager] implementation.
     * See [onClearPrefs] for more info.
     */
    fun clearPrefs() {
        onClearPrefs()
        delegates.forEach { it.onClear() }
        editor.clear().apply()
    }

    /**
     * Supplies a base list of properties to be preserved when [safeClearPrefs] is called.
     * The default implementation provides an empty list; override this function to specify your
     * own list of properties. The list must only contain mutable properties belonging to the
     * PrefManager instance for which this function is being overridden.
     */
    open fun keepBaseProps(): List<KMutableProperty0<out Any?>> = emptyList()

    /**
     * Clears this PrefManager's backing SharedPreferences file while preserving all properties
     * passed to this function as well as all properties provided by [keepBaseProps]. This calls
     * [clearPrefs] internally and does not guarantee all non-preserved properties will be reset.
     */
    @JvmOverloads
    @Suppress("UNCHECKED_CAST")
    fun safeClearPrefs(keepProps: List<KMutableProperty0<out Any?>> = emptyList()) {
        val keeperMap = (keepProps + keepBaseProps()).associate { it as KMutableProperty0<Any?> to it.get() }
        clearPrefs()
        for ((property, value) in keeperMap) property.set(value)
    }

    private inline fun SharedPreferences.Editor.save(block: SharedPreferences.Editor.() -> Unit) { block(); apply() }
}

/**
 * Extend [Pref] to create concrete delegate classes for use in [PrefManager]. Subclasses are
 * required to implement a [SharedPreferences.getValue()] and [Editor.setValue()] extension
 * function to ensure data is correctly and reliably saved to and retrieved from shared prefs.
 *
 * If an implementation of this class caches values in memory and must reset them when clearing
 * shared preferences, or has other operations that must take place at the same time, this must
 * be done in [onClear].
 *
 * @param defaultValue The default value that should be retrieved for the delegated property if it has
 * not previously been set.
 * @param keyName The optional key name under which the value will be stored. This is useful
 * when converting other [SharedPreferences] implementations to [PrefManager] and the existing
 * key name does not match the desired property name. Defaults to the property name.
 */
abstract class Pref<T>(private val defaultValue: T, private val keyName: String? = null) :
    ReadWriteProperty<PrefManager, T> {

    open operator fun provideDelegate(thisRef: PrefManager, prop: KProperty<*>): ReadWriteProperty<PrefManager, T> {
        thisRef.registerDelegate(this)
        return this
    }

    abstract fun SharedPreferences.getValue(key: String, default: T): T
    abstract fun SharedPreferences.Editor.setValue(key: String, value: T): SharedPreferences.Editor
    override fun getValue(thisRef: PrefManager, property: KProperty<*>): T = thisRef.prefs.getValue(keyName ?: property.name, defaultValue)
    override fun setValue(thisRef: PrefManager, property: KProperty<*>, value: T) = thisRef.editor.setValue(keyName ?: property.name, value).apply()
    abstract fun onClear()
}

/**
 * [Pref] delegate for [String] properties. May only be used in [PrefManager] implementations.
 *
 * @param defaultValue The optional value returned for the property when no value has been set
 * internally. Defaults to an empty String.
 * @param keyName The optional key name under which the property value will be stored. Defaults
 * to the property name. This is useful when converting other [SharedPreferences] implementations
 * to [PrefManager] and the required key name does not match the desired property name.
 */
class StringPref(defaultValue: String = "", keyName: String? = null) : Pref<String>(defaultValue, keyName) {
    override fun onClear() {}
    override fun SharedPreferences.getValue(key: String, default: String): String = getString(key, default) ?: default
    override fun SharedPreferences.Editor.setValue(key: String, value: String): SharedPreferences.Editor = putString(key, value)
}

/**
 * [Pref] delegate for [Boolean] properties. May only be used in [PrefManager] implementations.
 *
 * @param defaultValue The optional value returned for the property when no value has been set
 * internally. Defaults to false.
 * @param keyName The optional key name under which the property value will be stored. Defaults
 * to the property name. This is useful when converting other [SharedPreferences] implementations
 * to [PrefManager] and the required key name does not match the desired property name.
 */
class BooleanPref(defaultValue: Boolean = false, keyName: String? = null) : Pref<Boolean>(defaultValue, keyName) {
    override fun onClear() {}
    override fun SharedPreferences.getValue(key: String, default: Boolean) = getBoolean(key, default)
    override fun SharedPreferences.Editor.setValue(key: String, value: Boolean): SharedPreferences.Editor = putBoolean(key, value)
}

/**
 * [Pref] delegate for [Int] properties. May only be used in [PrefManager] implementations.
 *
 * @param defaultValue The optional value returned for the property when no value has been set
 * internally. Defaults to 0.
 * @param keyName The optional key name under which the property value will be stored. Defaults
 * to the property name. This is useful when converting other [SharedPreferences] implementations
 * to [PrefManager] and the required key name does not match the desired property name.
 */
class IntPref(defaultValue: Int = 0, keyName: String? = null) : Pref<Int>(defaultValue, keyName) {
    override fun onClear() {}
    override fun SharedPreferences.getValue(key: String, default: Int) = getInt(key, default)
    override fun SharedPreferences.Editor.setValue(key: String, value: Int): SharedPreferences.Editor = putInt(key, value)
}

/**
 * [Pref] delegate for arbitrary, nullable properties to be stored in SharedPreferences as
 * serialized strings using Gson. May only be used in [PrefManager] implementations.
 *
 * @param defaultValue (Optional) A default value to use until this property has been set.
 * @param keyName The optional key name under which the property value will be stored. Defaults
 * to the property name. This is useful when converting other [SharedPreferences] implementations
 * to [PrefManager] and the required key name does not match the desired property name.
 */
class GsonPref<T>(
    private val klazz: Class<T>,
    defaultValue: T? = null,
    keyName: String? = null
) : Pref<T?>(defaultValue, keyName) {

    private var cachedObject: T? = null

    override fun onClear() { cachedObject = null }

    override fun SharedPreferences.getValue(key: String, default: T?): T? {
        if (cachedObject == null) {
            cachedObject = Gson().fromJson<T>(getString(key, null), klazz)
        }
        return cachedObject ?: default
    }

    override fun SharedPreferences.Editor.setValue(key: String, value: T?): SharedPreferences.Editor {
        cachedObject = value
        if (value == null) {
            putString(key, null)
        } else {
            putString(key, Gson().toJson(value) ?: return this)
        }
        return this
    }
}

