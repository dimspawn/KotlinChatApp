package com.kotlinchatapp.core.domain.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class ConversationConstraintData(
    val userId: String?,
    val conversationId: String?,
    val senderId: String?,
    val lastMessage: String?,
    val username: String?,
    val photoUri: String?
): Parcelable