package com.kotlinchatapp.core.domain.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class ConversationData(
    val id: String,
    val conversationName: String,
    val personal: Int
): Parcelable