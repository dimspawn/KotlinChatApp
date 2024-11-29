package com.kotlinchatapp.core.domain.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class ParticipantData(
    val id: String,
    val conversationId: String,
    val userId: String,
    val blocked: Int,
    val latestMessageId: String?,
    val status: String,
    val updatedAt: Long,
    val createdAt: Long
): Parcelable