package com.kotlinchatapp.core.data.source.remote.response

import com.google.gson.annotations.SerializedName

data class RemoteFilterResponse(
    @field:SerializedName("filters")
    var filters: List<FilterResponse>? = null,
    @field:SerializedName("mFilters")
    var mFilters: List<MultipleFilterResponse>? = null,
    @field:SerializedName("excludes")
    var excludes: List<FilterResponse>? = null
)