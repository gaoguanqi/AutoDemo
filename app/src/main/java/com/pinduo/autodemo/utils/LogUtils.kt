package com.pinduo.autodemo.utils

import android.util.Log

class LogUtils {
    companion object {
        @JvmStatic
        fun logGGQ(s: String?) {
            if (true) {
                Log.i("GGQ", "->>>${s}")
            }
        }

        @JvmStatic
        fun logQ(s: String?) {
            if (true) {
                Log.i("TAGQ", "->>>${s}")
            }
        }
    }
}