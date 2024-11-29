package com.kotlinchatapp.core.domain.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class MessageConstraintData(
    val id: String,
    val userId: String,
    val username: String,
    val photoProfileUrl: String?,
    val message: String
): Parcelable