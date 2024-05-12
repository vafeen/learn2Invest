package ru.surf.learn2invest.noui.logs

import android.util.Log

class Loher {

    companion object {
        val tag = "Loher"
        fun d(msg: String) {
            Log.d(tag, msg)
        }

        fun e(msg: String) {
            Log.e(tag, msg)
        }
    }
}