package com.kotlinchatapp.core.domain.model.input

import com.kotlinchatapp.core.data.source.remote.response.MessageResponse

data class InputUserIdMessageResponse(
    val userId: String,
    val messageResponse: MessageResponse
)