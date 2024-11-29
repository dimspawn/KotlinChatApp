package com.kotlinchatapp.core.data.source.remote.response

import com.google.gson.annotations.SerializedName

data class LoginDataResponse(
    @field:SerializedName("email")
    var email: String? = null,
    @field:SerializedName("password")
    var password: String? = null
)
