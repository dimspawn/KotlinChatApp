package com.kotlinchatapp.core.data.source.local.entity.join

import android.os.Parcelable
import androidx.room.ColumnInfo
import kotlinx.parcelize.Parcelize

@Parcelize
data class ConversationConstraintEntity(
    @ColumnInfo(name = "userId")
    var userId: String?,
    @ColumnInfo(name = "conversationId")
    var conversationId: String?,
    @ColumnInfo(name = "senderId")
    var senderId: String?,
    @ColumnInfo(name = "lastMessage")
    var lastMessage: String?,
    @ColumnInfo(name = "username")
    var username: String?,
    @ColumnInfo(name = "photoUri")
    var photoUri: String?
): Parcelable