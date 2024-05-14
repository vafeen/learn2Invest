package ru.surf.learn2invest.noui.logs

import android.util.Log

class Loher {
    /**
     * Log2.0 для простоты, и чтобы можно было централизованно отключить это
     */
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