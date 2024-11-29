package com.imaginatic.kotlinchatapp.dashboard

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.switchMap
import com.kotlinchatapp.core.domain.model.input.InputUserIdMessageResponse
import com.kotlinchatapp.core.domain.usecase.ChatUseCase
import javax.inject.Inject

class DashboardViewModel @Inject constructor(private val chatUseCase: ChatUseCase): ViewModel(){
    private val userId = MutableLiveData<String>()
    private val inputUserIdMessageResponse = MutableLiveData<InputUserIdMessageResponse>()
    val logOut = chatUseCase.setLogout().asLiveData()

    val conversationsConstraint = userId.switchMap {
        chatUseCase.getConstraintConversations(it).asLiveData()
    }

    val conversationSocket = inputUserIdMessageResponse.switchMap {
        chatUseCase.setSocketMessageDataFromDashboard(it.userId, it.messageResponse).asLiveData()
    }

    fun getConversationsConstraint(userId: String) {
        this.userId.value = userId
    }

    fun setMessageData(inputUserIdMessageResponse: InputUserIdMessageResponse) {
        this.inputUserIdMessageResponse.value = inputUserIdMessageResponse
    }
}