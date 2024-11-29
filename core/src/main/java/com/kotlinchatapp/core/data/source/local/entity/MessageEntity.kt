package com.kotlinchatapp.core.data.source.local.entity

import android.os.Parcelable
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.parcelize.Parcelize

@Parcelize
@Entity(tableName = "messages")
data class MessageEntity(
    @PrimaryKey
    @ColumnInfo(name = "id")
    var id: String,
    @ColumnInfo(name = "conversationId")
    var conversationId: String,
    @ColumnInfo(name = "userId")
    var userId: String,
    @ColumnInfo(name = "message")
    var message: String,
    @ColumnInfo(name = "type")
    var type: String,
    @ColumnInfo(name = "visited")
    var visited: Int,
    @ColumnInfo(name = "createdAt")
    var createdAt: Long,
    @ColumnInfo(name = "updatedAt")
    var updatedAt: Long
): Parcelable