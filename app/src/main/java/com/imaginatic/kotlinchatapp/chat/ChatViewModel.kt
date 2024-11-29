package com.imaginatic.kotlinchatapp.chat

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.switchMap
import com.kotlinchatapp.core.data.source.remote.response.MessageResponse
import com.kotlinchatapp.core.domain.model.input.InputMessageData
import com.kotlinchatapp.core.domain.model.input.InputMessagesData
import com.kotlinchatapp.core.domain.usecase.ChatUseCase
import javax.inject.Inject

class ChatViewModel @Inject constructor(private val chatUseCase: ChatUseCase): ViewModel() {

    private val inputMessageData = MutableLiveData<InputMessageData>()
    private val inputMessagesData = MutableLiveData<InputMessagesData>()
    private val messageResponse = MutableLiveData<MessageResponse>()

    val chat = inputMessageData.switchMap {
        chatUseCase.sendMessage(it).asLiveData()
    }

    val chats = inputMessagesData.switchMap {
        chatUseCase.getMessages(it.userId, it.conversationId).asLiveData()
    }

    val messageSocket = messageResponse.switchMap {
        chatUseCase.setSocketMessageDataFromChat(it).asLiveData()
    }

    fun setMessage(inputMessageData: InputMessageData) {
        this.inputMessageData.value = inputMessageData
    }

    fun getAllMessages(inputMessagesData: InputMessagesData) {
        this.inputMessagesData.value = inputMessagesData
    }

    fun setMessageData(messageResponse: MessageResponse) {
        this.messageResponse.value = messageResponse
    }
}