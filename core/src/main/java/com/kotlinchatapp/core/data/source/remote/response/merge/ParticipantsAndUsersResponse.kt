package com.kotlinchatapp.core.data.source.remote.response.merge

import com.google.gson.annotations.SerializedName
import com.kotlinchatapp.core.data.source.remote.response.ParticipantResponse
import com.kotlinchatapp.core.data.source.remote.response.UserResponse

data class ParticipantsAndUsersResponse(
    @field:SerializedName("participants")
    val participants: List<ParticipantResponse>,
    @field:SerializedName("users")
    val users: List<UserResponse>
)