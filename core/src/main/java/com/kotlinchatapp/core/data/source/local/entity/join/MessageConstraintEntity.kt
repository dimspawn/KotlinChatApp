package com.kotlinchatapp.core.data.source.local.entity.join

import android.os.Parcelable
import androidx.room.ColumnInfo
import kotlinx.parcelize.Parcelize

@Parcelize
data class MessageConstraintEntity (
    @ColumnInfo(name = "id")
    var id: String?,
    @ColumnInfo(name = "userId")
    var userId: String?,
    @ColumnInfo(name = "username")
    var username: String?,
    @ColumnInfo(name = "photoProfileUrl")
    var photoProfileUrl: String?,
    @ColumnInfo(name = "message")
    var message: String?
): Parcelable