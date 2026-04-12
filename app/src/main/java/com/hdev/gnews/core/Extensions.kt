package com.hdev.gnews.core


import android.content.Context
import android.content.Intent
import android.os.Parcelable
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.TimeZone
import java.util.concurrent.TimeUnit

inline fun <reified  T: Any> Context.startActivity(vararg params: Pair<String, Any?>) {
    val intent = Intent(this, T::class.java)
    params.forEach { (key, value) ->
        when(value) {
            is Int -> intent.putExtra(key, value)
            is String -> intent.putExtra(key, value)
            is Double -> intent.putExtra(key, value)
            is Boolean -> intent.putExtra(key, value)
            is Parcelable -> intent.putExtra(key, value)
        }
    }
    startActivity(intent)
}

// Di Extensions.kt
inline fun <reified T : Parcelable> Intent.parcelable(key: String): T? = when {
    android.os.Build.VERSION.SDK_INT >= 33 -> getParcelableExtra(key, T::class.java)
    else -> @Suppress("DEPRECATION") getParcelableExtra(key) as? T
}



fun String?.toTimeAgo(): String {
    if (this.isNullOrEmpty()) return ""

    // API date format (ISO 8601)
    val sdf = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'", Locale.ENGLISH)
    sdf.timeZone = TimeZone.getTimeZone("UTC")

    return try {
        val pastDate = sdf.parse(this) ?: return ""
        val now = Date()

        val diffInMillies = now.time - pastDate.time
        val minutes = TimeUnit.MILLISECONDS.toMinutes(diffInMillies)
        val hours = TimeUnit.MILLISECONDS.toHours(diffInMillies)
        val days = TimeUnit.MILLISECONDS.toDays(diffInMillies)

        when {
            minutes < 1 -> "just now"
            minutes < 60 -> "$minutes minutes ago"
            hours < 24 -> "$hours hours ago"
            days < 7 -> "$days days ago"
            else -> {
                // For older dates, show the actual date
                val dateFormat = SimpleDateFormat("dd MMM yyyy", Locale.ENGLISH)
                dateFormat.format(pastDate)
            }
        }
    } catch (e: Exception) {
        ""
    }
}