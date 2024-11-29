package com.kotlinchatapp.core.data.source.remote.response

import com.google.gson.annotations.SerializedName
import com.kotlinchatapp.core.data.source.remote.response.rules.IRemoteData

data class ParticipantResponse(
    @field:SerializedName("id")
    override var id: String? = null,
    @field:SerializedName("conversation_id")
    var conversationId: String? = null,
    @field:SerializedName("user_id")
    var userId: String? = null,
    @field:SerializedName("blocked")
    var blocked: Int? = null,
    @field:SerializedName("latest_message_id")
    var latestMessageId: String? = null,
    @field:SerializedName("status")
    var status: String? = null,
    @field:SerializedName("created_at")
    var createdAt: Long? = null,
    @field:SerializedName("updated_at")
    var updatedAt: Long? = null
): IRemoteData