package com.kt.apps.xembongda.storage

import android.content.SharedPreferences
import com.google.common.reflect.TypeParameter
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import javax.inject.Inject

class KeyValueStorageImpl @Inject constructor(
    private val sharedPreferences: SharedPreferences
) : IKeyValueStorage {

    override fun <T> get(key: String, clazz: Class<T>): T {
        return when (clazz) {
            String::class.java -> {
                sharedPreferences.getString(key, "") as T
            }
            Boolean::class.java -> {
                sharedPreferences.getBoolean(key, false) as T
            }
            Int::class.java -> {
                sharedPreferences.getInt(key, -1) as T
            }
            Float::class.java -> sharedPreferences.getFloat(key, -1f) as T

            Long::class.java -> sharedPreferences.getLong(key, -1) as T
            else -> {
                val strValue = sharedPreferences.getString(key, "")
                Gson().fromJson(strValue, clazz)
            }
        }
    }

    override fun <T> save(key: String, value: T, clazz: Class<T>) {
        when (clazz) {
            String::class.java -> sharedPreferences.edit().putString(key, value as String).apply()

            Boolean::class.java -> sharedPreferences.edit().putBoolean(key, value as Boolean)
                .apply()

            Int::class.java -> sharedPreferences.edit().putInt(key, value as Int).apply()

            Float::class.java -> sharedPreferences.edit().putFloat(key, value as Float).apply()

            Long::class.java -> sharedPreferences.edit().putLong(key, value as Long).apply()

            else -> {
                val strValue = Gson().toJson(value)
                sharedPreferences.edit().putString(key, strValue).apply()
            }
        }
    }

    override fun <T, U> save(key: String, value: Map<T, U>) {
        sharedPreferences.edit().putString(key, Gson().toJson(value)).apply()
    }

    override fun <T, U> get(key: String, clazz: Class<T>, clazz2: Class<U>): Map<T, U> {
        return try {
            val type = TypeToken.getParameterized(Map::class.java, clazz, clazz2).type
            Gson().fromJson(sharedPreferences.getString(key, ""), type) as Map<T, U>
        } catch (_: Exception) {
            mapOf()
        }

    }

    override fun <T> save(key: String, value: List<T>) {
        sharedPreferences.edit().putString(key, Gson().toJson(value)).apply()
    }

    override fun <T> getList(key: String, clazz: Class<T>): List<T> {
        val type = TypeToken.getParameterized(List::class.java, clazz).type
        val gsonValue = sharedPreferences.getString(key, "")
        if (gsonValue?.isEmpty() != false) return listOf()
        return Gson().fromJson(gsonValue, type)
    }
}