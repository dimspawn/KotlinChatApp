package com.kotlinchatapp.core.data.source.remote.response

import com.google.gson.annotations.SerializedName

data class FilterResponse(
    @field:SerializedName("key")
    var key: String? = null,
    @field:SerializedName("value")
    var value: Any? = null
)