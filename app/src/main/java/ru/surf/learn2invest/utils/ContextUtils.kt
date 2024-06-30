package ru.surf.learn2invest.utils

import android.content.Context
import androidx.annotation.StringRes

fun Context.getStringOrNull(@StringRes resId: Int?): String? {
    return resId?.let {
        getString(it)
    }
}