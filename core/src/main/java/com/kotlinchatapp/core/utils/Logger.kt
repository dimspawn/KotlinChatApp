package com.kotlinchatapp.core.utils

interface Logger {
    fun e(tag: String, msg: String)
    fun d(tag: String, msg: String)
}