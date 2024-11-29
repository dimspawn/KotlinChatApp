package com.kotlinchatapp.core.data.source.remote.response.merge

import com.google.gson.annotations.SerializedName
import com.kotlinchatapp.core.data.source.remote.response.ConversationResponse
import com.kotlinchatapp.core.data.source.remote.response.ParticipantResponse

data class ParticipantsConversationsResponse(
    @field:SerializedName("conversations")
    val conversations: List<ConversationResponse>,
    @field:SerializedName("participants")
    val participants: List<ParticipantResponse>,
)