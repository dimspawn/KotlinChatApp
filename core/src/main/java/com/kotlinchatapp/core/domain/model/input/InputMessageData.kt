package com.kotlinchatapp.core.domain.model.input

data class InputMessageData(
    val conversationId: String,
    val userId: String,
    val message: String,
    val type: String
)