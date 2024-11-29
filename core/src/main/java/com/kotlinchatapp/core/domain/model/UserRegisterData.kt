package com.kotlinchatapp.core.domain.model

import android.net.Uri

data class UserRegisterData(
    val username: String,
    val email: String,
    val password: String,
    val photoUri: Uri?
)