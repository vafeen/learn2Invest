package ru.surf.learn2invest.utils

import android.app.Activity
import android.view.View
import android.view.inputmethod.InputMethodManager
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

fun String.isThisContainsSequenceOrEmpty(): Boolean {
    for (number in 0..6) {
        if (contains(
                "$number${number + 1}${number + 2}${number + 3}"
            ) || isEmpty()
        ) {
            return true
        }
    }
    return false
}

fun String.isThisContains3NumbersOfEmpty(): Boolean {
    if (this == "") {
        return true
    }
    for (number in 0..9) {
        if (contains("$number".repeat(3))) {
            return true
        }
    }
    return false
}

fun View.showKeyboard() = ViewCompat.getWindowInsetsController(this)
    ?.show(WindowInsetsCompat.Type.ime())

fun View.hideKeyboard(activity: Activity) {
    val inputMethodManager =
        activity.getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
    inputMethodManager.hideSoftInputFromWindow(windowToken, 0)
}