package com.kotlinchatapp.core.domain.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class AvailableData(
    val users: List<UserData>,
    val conversations: List<ConversationData>,
    val participants: List<ParticipantData>,
    val message: List<MessageData>
): Parcelable
