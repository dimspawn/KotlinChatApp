package com.kotlinchatapp.core.domain.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class ParticipantWithUserData(
    val participantData: ParticipantData,
    val user: UserData
): Parcelable