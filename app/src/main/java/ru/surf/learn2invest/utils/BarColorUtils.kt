package ru.surf.learn2invest.utils

import android.content.Context
import android.content.res.Configuration
import android.view.Window
import androidx.core.content.ContextCompat

fun setStatusBarColor(
    window: Window,
    context: Context,
    statusBarColorLight: Int,
    statusBarColorDark: Int
) {
    window.statusBarColor = ContextCompat.getColor(
        context,
        if (context.resources.configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK == Configuration.UI_MODE_NIGHT_YES) {
            statusBarColorDark
        } else {
            statusBarColorLight
        }
    )
}

fun setNavigationBarColor(
    window: Window,
    context: Context,
    navigationBarColorLight: Int,
    navigationBarColorDark: Int
) {
    window.navigationBarColor = ContextCompat.getColor(
        context,
        if (context.resources.configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK == Configuration.UI_MODE_NIGHT_YES) {
            navigationBarColorDark
        } else {
            navigationBarColorLight
        }
    )
}