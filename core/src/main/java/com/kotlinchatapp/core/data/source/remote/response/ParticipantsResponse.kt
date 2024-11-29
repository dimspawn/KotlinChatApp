package com.kotlinchatapp.core.data.source.remote.response

import com.google.gson.annotations.SerializedName

data class ParticipantsResponse(
    @field:SerializedName("success")
    val success: Boolean? = null,
    @field:SerializedName("data")
    val data: List<ParticipantResponse>? = null,
    @field:SerializedName("message")
    val message: String? = null
)