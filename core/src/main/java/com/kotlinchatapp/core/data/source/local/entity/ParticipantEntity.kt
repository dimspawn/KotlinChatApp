package com.kotlinchatapp.core.data.source.local.entity

import android.os.Parcelable
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.parcelize.Parcelize

@Parcelize
@Entity(tableName = "participants")
data class ParticipantEntity(
    @PrimaryKey
    @ColumnInfo(name = "id")
    var id: String,
    @ColumnInfo(name = "conversationId")
    var conversationId: String,
    @ColumnInfo(name = "userId")
    var userId: String,
    @ColumnInfo(name = "blocked")
    var blocked: Int,
    @ColumnInfo(name = "latestMessageId")
    var latestMessageId: String?,
    @ColumnInfo(name = "status")
    var status: String,
    @ColumnInfo(name = "updatedAt")
    var updatedAt: Long,
    @ColumnInfo(name = "createdAt")
    var createdAt: Long
): Parcelable