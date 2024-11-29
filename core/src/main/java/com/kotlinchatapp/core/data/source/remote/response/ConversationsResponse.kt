package com.kotlinchatapp.core.data.source.remote.response

import com.google.gson.annotations.SerializedName

data class ConversationsResponse(
    @field:SerializedName("success")
    val success: Boolean? = null,
    @field:SerializedName("data")
    val data: List<ConversationResponse>? = null,
    @field:SerializedName("message")
    val message: String? = null
)