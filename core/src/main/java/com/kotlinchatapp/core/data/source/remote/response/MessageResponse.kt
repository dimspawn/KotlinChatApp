package com.kotlinchatapp.core.data.source.remote.response

import com.google.gson.annotations.SerializedName
import com.kotlinchatapp.core.data.source.remote.response.rules.IRemoteData

data class MessageResponse(
    @field:SerializedName("id")
    override var id: String? = null,
    @field:SerializedName("conversation_id")
    var conversationId: String? = null,
    @field:SerializedName("user_id")
    var userId: String? = null,
    @field:SerializedName("message")
    var message: String? = null,
    @field:SerializedName("type")
    var type: String? = null,
    @field:SerializedName("visited")
    var visited: Int? = null,
    @field:SerializedName("created_at")
    var createdAt: Long? = null,
    @field:SerializedName("updated_at")
    var updatedAt: Long? = null
): IRemoteData