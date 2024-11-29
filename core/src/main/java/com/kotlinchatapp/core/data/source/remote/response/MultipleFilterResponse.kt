package com.kotlinchatapp.core.data.source.remote.response

import com.google.gson.annotations.SerializedName

data class MultipleFilterResponse(
    @field:SerializedName("key")
    var key: String? = null,
    @field:SerializedName("values")
    var values: List<Any>? = null
)