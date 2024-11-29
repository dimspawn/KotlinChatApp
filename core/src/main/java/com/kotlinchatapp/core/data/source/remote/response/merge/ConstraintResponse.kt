package com.kotlinchatapp.core.data.source.remote.response.merge

import com.google.gson.annotations.SerializedName
import com.kotlinchatapp.core.data.source.remote.response.ConversationResponse
import com.kotlinchatapp.core.data.source.remote.response.MessageResponse
import com.kotlinchatapp.core.data.source.remote.response.ParticipantResponse
import com.kotlinchatapp.core.data.source.remote.response.UserResponse

data class ConstraintResponse(
    @field:SerializedName("users")
    val users: List<UserResponse>? = null,
    @field:SerializedName("conversations")
    val conversations: List<ConversationResponse>? = null,
    @field:SerializedName("participants")
    val participants: List<ParticipantResponse>? = null,
    @field:SerializedName("messages")
    val messages: List<MessageResponse>? = null
)