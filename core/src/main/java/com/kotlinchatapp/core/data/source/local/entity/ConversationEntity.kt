package com.kotlinchatapp.core.data.source.local.entity

import android.os.Parcelable
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.parcelize.Parcelize

@Parcelize
@Entity(tableName = "conversations")
data class ConversationEntity(
    @PrimaryKey
    @ColumnInfo(name = "id")
    var id: String,
    @ColumnInfo(name = "conversationName")
    var conversationName: String,
    @ColumnInfo(name = "personal")
    var personal: Int
): Parcelable