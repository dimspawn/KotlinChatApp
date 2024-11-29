package com.kotlinchatapp.core.domain.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class MessageData(
    val id: String,
    val conversationId: String,
    val userId: String,
    val message: String,
    val type: String,
    val visited: Int,
    val createdAt: Long,
    val updatedAt: Long
): Parcelable