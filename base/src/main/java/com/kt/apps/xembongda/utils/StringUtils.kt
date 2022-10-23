package com.kt.apps.xembongda.utils

import java.text.SimpleDateFormat
import java.util.*

fun String.formatDateTime(
    pattern: String,
    newPattern: String,
    locale: Locale = Locale.getDefault()
): String? {
    val formatter = SimpleDateFormat(pattern, locale)
    return formatter.parse(this)?.let {
        SimpleDateFormat(newPattern, locale).format(it)
    } ?: this
}

fun String.toDate(
    pattern: String,
    locale: Locale = Locale.getDefault(),
    isUtc: Boolean = false
): Date? {
    val formatter = SimpleDateFormat(pattern, locale)
    if (isUtc) formatter.timeZone = TimeZone.getTimeZone("UTC")
    return formatter.parse(this)
}

fun Date.formatDateTime(pattern: String, locale: Locale = Locale.getDefault()): String {
    val formatter = SimpleDateFormat(pattern, locale)
    formatter.timeZone = Calendar.getInstance(locale).timeZone
    return formatter.format(this)
}


private const val SECOND_MILLIS = 1000
private const val MINUTE_MILLIS = 60 * SECOND_MILLIS
private const val HOUR_MILLIS = 60 * MINUTE_MILLIS
private const val DAY_MILLIS = 24 * HOUR_MILLIS


@JvmName("getTimeAgoFrom")
fun Long.getTimeAgo(): String = getTimeAgo(this)

fun getTimeAgo(timeInMilli: Long): String {
    var time = timeInMilli
    if (time < 1000000000000L) {
        // if timestamp given in seconds, convert to millis
        time *= 1000
    }
    val now: Long = Calendar.getInstance(Locale.getDefault()).timeInMillis

    val diff = now - time
    return when {
        diff < MINUTE_MILLIS -> {
            "${diff / 1000}s"
        }
        diff < 2 * MINUTE_MILLIS -> {
            "1m"
        }
        diff < 50 * MINUTE_MILLIS -> {
            (diff / MINUTE_MILLIS).toString() + "m"
        }
        diff < 90 * MINUTE_MILLIS -> {
            "1h"
        }
        diff < 24 * HOUR_MILLIS -> {
            (diff / HOUR_MILLIS).toString() + "hrs"
        }
        diff < 48 * HOUR_MILLIS -> {
            "Yesterday"
        }
        else -> {
            (diff / DAY_MILLIS).toString() + "d"
        }
    }
}