package com.kotlinchatapp.core.data.source.local.entity

import android.os.Parcelable
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.parcelize.Parcelize

@Parcelize
@Entity(tableName = "users")
data class UserEntity(
    @PrimaryKey
    @ColumnInfo(name = "id")
    var id: String,
    @ColumnInfo(name = "username")
    var username: String,
    @ColumnInfo(name = "email")
    var email: String,
    @ColumnInfo(name = "photoProfileUrl")
    var photoProfileUrl: String? = null,
    @ColumnInfo(name = "isLogin")
    var isLogin: Boolean = false
): Parcelable