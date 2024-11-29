package com.kotlinchatapp.core.data.source.remote.response

import com.google.gson.annotations.SerializedName
import com.kotlinchatapp.core.data.source.remote.response.rules.IRemoteData

data class UserResponse(
    @field:SerializedName("id")
    override var id: String? = null,
    @field:SerializedName("username")
    var username: String? = null,
    @field:SerializedName("email")
    var email: String? = null,
    @field:SerializedName("photo_profile_url")
    var photoProfileUrl: String? = null
): IRemoteData