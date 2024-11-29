package com.kotlinchatapp.core.data.source.remote.response

import com.google.gson.annotations.SerializedName
import com.kotlinchatapp.core.data.source.remote.response.rules.IRemoteData

data class ConversationResponse(
    @field:SerializedName("id")
    override var id: String? = null,
    @field:SerializedName("conversation_name")
    var conversationName: String? = null,
    @field:SerializedName("personal")
    var personal: Int? = null
): IRemoteData