package com.kotlinchatapp.core.data.source.remote.response

import com.google.gson.annotations.SerializedName

data class RegisterResponse(
    @field:SerializedName("success")
    val success: Boolean? = null,
    @field:SerializedName("id")
    val id: String? = null,
    @field:SerializedName("message")
    val message: String? = null
)