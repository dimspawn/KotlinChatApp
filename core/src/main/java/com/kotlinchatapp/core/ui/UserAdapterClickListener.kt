package com.kotlinchatapp.core.ui

import com.kotlinchatapp.core.domain.model.UserData

interface UserAdapterClickListener {
    fun onUserClickListener(userData: UserData)
}