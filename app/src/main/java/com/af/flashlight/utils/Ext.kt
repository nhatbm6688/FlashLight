@file:Suppress("DEPRECATION")

package com.af.flashlight.utils

import android.app.Activity
import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.content.res.Resources.getSystem
import android.graphics.LinearGradient
import android.graphics.Shader
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Parcelable
import android.provider.Settings
import android.text.SpannableString
import android.text.Spanned
import android.text.style.StrikethroughSpan
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.ImageView
import android.widget.TextView
import androidx.annotation.ColorInt
import androidx.core.content.ContextCompat
import androidx.core.net.toUri
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.google.android.material.tabs.TabLayout
import com.google.gson.Gson
import com.af.flashlight.R
import com.af.flashlight.data.model.Language
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale
import java.util.TimeZone


fun isTiramisuPlus() = Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU

fun Context?.isPermissionGranted(permission: String?): Boolean {
    val context = this
    return if (context == null || permission.isNullOrEmpty()) false else ContextCompat.checkSelfPermission(
        context,
        permission
    ) == PackageManager.PERMISSION_GRANTED
}

fun Language.toJson(): String {
    return Gson().toJson(this)
}

fun String.toLanguageModel(): Language? {
    return kotlin.runCatching {
        Gson().fromJson(this, Language::class.java)
    }.getOrNull()
}

fun Context.setAppLanguage(languageCode: String) {
    LocaleHelper.setLocale(this, languageCode)
}

fun Context.getDeviceLanguage(): String {
    return Locale.getDefault().language
}

val Int.dp: Int get() = (this * getSystem().displayMetrics.density).toInt()

fun Long.convertTimeToDate(): String {
    val utc = Calendar.getInstance(TimeZone.getTimeZone("UTC"))
    utc.timeInMillis = this
    val format = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
    return format.format(utc.time)
}

fun Long.format(format: String): String {
    val simpleDateFormat = SimpleDateFormat(format, Locale.getDefault())
    val calendar = Calendar.getInstance()
    calendar.timeInMillis = this
    return simpleDateFormat.format(calendar.time)
}

inline fun <reified T : Parcelable> Intent.parcelable(key: String): T? = getParcelableExtra(key) as? T

inline fun <reified T : Parcelable> Bundle.parcelable(key: String): T? = getParcelable(key) as? T

inline fun <reified T : Parcelable> Intent.getParcelableArrayList(key: String): ArrayList<T>? = getParcelableArrayListExtra(key)

fun TextView.strikeThrough(text: String) {
    val spannable = SpannableString(text)
    spannable.setSpan(StrikethroughSpan(), 0, spannable.length, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
    this.text = spannable
}

fun TabLayout.setDotsSpacing(spacingDp: Int) {
    val spacingPx = (spacingDp * resources.displayMetrics.density).toInt()
    val tabStrip = getChildAt(0) as ViewGroup
    for (i in 0 until tabStrip.childCount) {
        val tabView = tabStrip.getChildAt(i)
        val lp = tabView.layoutParams as ViewGroup.MarginLayoutParams
        lp.marginStart = spacingPx / 2
        lp.marginEnd = spacingPx / 2
        tabView.layoutParams = lp
    }
}

fun Context.openAppInStore() {
    val uri = ("market://details?id=" + this.packageName).toUri()
    val myAppLinkToMarket = Intent(Intent.ACTION_VIEW, uri)
    try {
        startActivity(myAppLinkToMarket)
    } catch (e: ActivityNotFoundException) {
        Logger.e(e.message)
    }
}

fun Context.share(text: String) {
    val intent = Intent(Intent.ACTION_SEND)
    intent.type = "text/plain"
    intent.putExtra(Intent.EXTRA_SUBJECT, this.getString(R.string.app_name))
    intent.putExtra(Intent.EXTRA_TEXT, text)
    this.startActivity(Intent.createChooser(intent, this.getString(R.string.choose_one)))
}

fun Activity.openBrowser(url: String) {
    try {
        this.startActivity(
            Intent(
                Intent.ACTION_VIEW, url.toUri()
            )
        )
    } catch (ex: ActivityNotFoundException) {
        Logger.e(ex.message)
    }
}

fun View.hideKeyboard(context: Context) {
    val manager = context.getSystemService(
        Context.INPUT_METHOD_SERVICE
    ) as InputMethodManager?
    manager?.hideSoftInputFromWindow(windowToken, 0)
}

fun ImageView.setImageView(
    context: Context,
    urlString: String
) {
    Glide.with(context)
        .load(urlString)
        .dontAnimate()
        .centerCrop()
        .diskCacheStrategy(DiskCacheStrategy.ALL)
        .override(150, 150)
        .into(this)
}

fun ImageView.setImageView(
    context: Context,
    urlString: String,
    width: Int, height: Int
) {
    Glide.with(context)
        .load(urlString)
        .dontAnimate()
        .centerCrop()
        .diskCacheStrategy(DiskCacheStrategy.ALL)
        .override(width, height)
        .into(this)
}

fun Context.openApp(packageName: String) {
    try {
        val intent = this.packageManager.getLaunchIntentForPackage(packageName)
        intent?.let {
            this.startActivity(it)
        } ?: run {
            val playStoreIntent = Intent(Intent.ACTION_VIEW, "market://details?id=$packageName".toUri())
            this.startActivity(playStoreIntent)
        }
    } catch (e: Exception) {
        e.printStackTrace()
    }
}

fun Context.openAppSettings() {
    val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).apply {
        data = Uri.fromParts("package", packageName, null)
        flags = Intent.FLAG_ACTIVITY_NEW_TASK
    }
    this.startActivity(intent)
}

fun View.gone() {
    visibility = View.GONE
}

fun View.invisible() {
    visibility = View.INVISIBLE
}

fun View.visible() {
    visibility = View.VISIBLE
}

fun TextView.setGradientText(
    @ColorInt startColor: Int,
    @ColorInt endColor: Int
) {
    paint.shader = LinearGradient(
        0f,
        0f,
        paint.measureText(text.toString()),
        0f,
        startColor,
        endColor,
        Shader.TileMode.CLAMP
    )
    invalidate()
}
