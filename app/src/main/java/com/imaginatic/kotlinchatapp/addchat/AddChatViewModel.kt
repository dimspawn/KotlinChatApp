package com.imaginatic.kotlinchatapp.addchat

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.switchMap
import com.kotlinchatapp.core.domain.model.input.InputUserIdsData
import com.kotlinchatapp.core.domain.usecase.ChatUseCase
import javax.inject.Inject

class AddChatViewModel @Inject constructor(private val chatUseCase: ChatUseCase): ViewModel() {
    private val userId = MutableLiveData<String>()
    private val uIds = MutableLiveData<InputUserIdsData>()

    val users = userId.switchMap { uid ->
        chatUseCase.getUsers(uid).asLiveData()
    }

    val personalChat = uIds.switchMap {
        chatUseCase.createPersonalChat(it).asLiveData()
    }

    fun setUserList(userId: String) {
        this.userId.value = userId
    }

    fun setUserIds(uIds: InputUserIdsData) {
        this.uIds.value = uIds
    }
}