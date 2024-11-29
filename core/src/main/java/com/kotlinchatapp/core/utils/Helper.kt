package com.kotlinchatapp.core.utils

import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

object Helper {
    fun dateNowPretty(): String {
        val formatter = SimpleDateFormat("yyyy-MM-dd hh:mm:ss", Locale.getDefault())
        val formattedDate = formatter.format(Date())
        return formattedDate
    }
}