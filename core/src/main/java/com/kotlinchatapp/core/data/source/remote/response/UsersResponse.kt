package com.kotlinchatapp.core.data.source.remote.response

import com.google.gson.annotations.SerializedName

data class UsersResponse(
    @field:SerializedName("success")
    val success: Boolean? = null,
    @field:SerializedName("data")
    val data: List<UserResponse>? = null,
    @field:SerializedName("message")
    val message: String? = null
)
