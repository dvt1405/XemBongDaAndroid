package com.kt.apps.xembongda.utils

import android.net.Uri
import android.text.Html
import android.text.format.DateUtils
import android.util.Log
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.annotation.DrawableRes
import androidx.databinding.BindingAdapter
import com.bumptech.glide.Glide
import com.google.android.gms.common.SignInButton
import com.kt.apps.xembongda.GlideApp
import com.kt.apps.xembongda.base.R
import java.text.SimpleDateFormat
import java.util.*


@BindingAdapter("kt:visible")
fun bindViewVisibility(view: View, visible: Boolean) {
    view.setVisible(visible)
}

@BindingAdapter("kt:bindImageUrl")
fun bindImage(view: ImageView, url: String) {
    view.loadImage(url)
}


@BindingAdapter("kt:bindImageUri")
fun bindImage(view: ImageView, url: Uri?) {
    val placeHolder: Int = R.drawable.image_place_holder_corner_8
    val defImg: Int = R.drawable.app_icon
    if (url == null) {
        view.loadImage(defImg)
        return
    }
    GlideApp.with(view)
        .load(url)
        .error(defImg)
        .placeholder(placeHolder)
        .into(view)
}

@BindingAdapter("kt:bindImageUrlPlacholderLogo")
fun bindImageWithLogo(view: ImageView, url: String) {
    view.loadImage(url, placeHolder = R.drawable.app_icon)
}

fun ImageView.loadImage(
    url: String?,
    @DrawableRes defImg: Int = R.drawable.app_icon,
    placeHolder: Int = R.drawable.image_place_holder_corner_8,
) {
    if (url.isNullOrEmpty()) {
        loadImage(defImg)
        return
    }
    GlideApp.with(this)
        .load(url)
        .error(defImg)
        .placeholder(placeHolder)
        .into(this)
}


@BindingAdapter(
    "kt:bindFormatDateTimeString",
    "kt:oldDateTimePattern",
    "kt:newDateTimePattern",
    "kt:isUtcTimeZone",
    requireAll = true
)
fun bindFormatDateTime(
    view: TextView,
    dateTime: String,
    oldPattern: String,
    newPattern: String,
    isUtc: Boolean = false
) {
    try {
        val date = dateTime.toDate(oldPattern, isUtc = isUtc) ?: throw NullPointerException()
        view.text = if (DateUtils.isToday(date.time)) {
            "${date.formatDateTime(newPattern)} - ${view.context.getString(R.string.today_format_date_title)}"
        } else {
            date.formatDateTime("$newPattern - dd/MM")
        }
    } catch (e: Exception) {
        view.text = dateTime
    }
}

@BindingAdapter("kt:bindDateTimeInMilli", requireAll = true)
fun bindFormatDateTime(view: TextView, dateTime: String) {
    try {
        val long = dateTime.toLong()
        val calendar = Calendar.getInstance()
        calendar.timeInMillis = long
        calendar.time
        val date = SimpleDateFormat("MM/dd/yyyy HH:mm:ss").format(Date(dateTime.toLong() * 1000))
        view.text = if (DateUtils.isToday(dateTime.toLong() * 1000)) {
            "${SimpleDateFormat("HH:mm").format(Date(dateTime.toLong() * 1000))} - ${
                view.context.getString(
                    R.string.today_format_date_title
                )
            }"
        } else {
            SimpleDateFormat("HH:mm - dd/MM").format(Date(dateTime.toLong() * 1000))
        }
    } catch (e: Exception) {
        view.text = Html.fromHtml(dateTime)
    }
}


@BindingAdapter("kt:bindFormatDateTimeMilli", "kt:newDateTimePattern", requireAll = true)
fun bindFormatDateTimeFromTimeMilli(view: TextView, dateTime: Long, newPattern: String) {
    val calendar = Calendar.getInstance(Locale("us"))
    calendar.timeInMillis = dateTime
    view.text = if (DateUtils.isToday(calendar.timeInMillis)) {
        "${calendar.time.formatDateTime(newPattern)} - ${view.context.getString(R.string.today_format_date_title)}"
    } else {
        calendar.time.formatDateTime("$newPattern - dd/MM")
    }

}


@BindingAdapter("kt:bindFormatDateTime", "kt:dateTimePattern", requireAll = true)
fun bindFormatDateTime(view: TextView, dateTime: Date, pattern: String) {
    view.text = dateTime.formatDateTime(pattern)
}

@BindingAdapter("android:onClick")
fun bindSignInClick(button: SignInButton, method: () -> Unit) {
    button.setOnClickListener { method.invoke() }
}