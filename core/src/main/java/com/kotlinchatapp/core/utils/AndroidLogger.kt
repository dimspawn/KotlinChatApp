package com.kotlinchatapp.core.utils

import android.util.Log

class AndroidLogger: Logger {
    override fun e(tag: String, msg: String) { Log.e(tag, msg) }
    override fun d(tag: String, msg: String) { Log.d(tag, msg) }
}