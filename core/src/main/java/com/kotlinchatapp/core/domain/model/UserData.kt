package com.kotlinchatapp.core.domain.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class UserData(
    val id: String,
    val username: String,
    val email: String,
    val photoProfileUrl: String?,
    val isLogin: Boolean
): Parcelable