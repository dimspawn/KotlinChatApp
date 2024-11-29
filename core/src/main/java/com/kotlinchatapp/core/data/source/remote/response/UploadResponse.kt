package com.kotlinchatapp.core.data.source.remote.response

import com.google.gson.annotations.SerializedName

data class UploadResponse(
    @field:SerializedName("success")
    val success: Boolean? = null,
    @field:SerializedName("file")
    val file: String? = null,
    @field:SerializedName("message")
    val message: String? = null,
)