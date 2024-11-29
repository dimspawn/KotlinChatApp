package com.kotlinchatapp.core.domain.usecase

import android.net.Uri
import com.kotlinchatapp.core.data.ChatRepository
import com.kotlinchatapp.core.data.Resource
import com.kotlinchatapp.core.data.source.remote.response.MessageResponse
import com.kotlinchatapp.core.domain.model.ConversationConstraintData
import com.kotlinchatapp.core.domain.model.MessageConstraintData
import com.kotlinchatapp.core.domain.model.MessageData
import com.kotlinchatapp.core.domain.model.ParticipantData
import com.kotlinchatapp.core.domain.model.UserData
import com.kotlinchatapp.core.domain.model.UserLoginData
import com.kotlinchatapp.core.domain.model.input.InputMessageData
import com.kotlinchatapp.core.domain.model.input.InputUserIdsData
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class ChatInteract @Inject constructor(private val chatRepository: ChatRepository): ChatUseCase{
    override fun setRegisterChat(username: String, email: String, password: String, photoUri: Uri?): Flow<Resource<List<UserData>>> = chatRepository.setRegisterChat(username, email, password, photoUri)
    override fun getCheckLogin(): Flow<List<UserData>> = chatRepository.getCheckLogin()
    override fun setLogout(): Flow<Resource<Boolean>> = chatRepository.setLogout()
    override fun getUsers(userId: String): Flow<Resource<List<UserData>>> = chatRepository.getUsers(userId)
    override fun setLoginChat(userLoginData: UserLoginData): Flow<Resource<List<UserData>>> = chatRepository.setLoginChat(userLoginData)
    override fun sendMessage(inputMessageData: InputMessageData): Flow<Resource<MessageData?>> = chatRepository.sendMessage(inputMessageData)
    override fun createPersonalChat(inputUserIdsData: InputUserIdsData): Flow<Resource<List<ParticipantData>>> = chatRepository.createPersonalChat(inputUserIdsData)
    override fun getMessages(userId: String, conversationId: String): Flow<Resource<List<MessageConstraintData>>> = chatRepository.getMessages(userId, conversationId)
    override fun getConstraintConversations(userId: String): Flow<Resource<List<ConversationConstraintData>>> = chatRepository.getConstraintConversations(userId)
    override fun setSocketMessageDataFromDashboard(userId: String, messageResponse: MessageResponse): Flow<Resource<List<ConversationConstraintData>>> = chatRepository.setSocketMessageDataFromDashboard(userId, messageResponse)
    override fun setSocketMessageDataFromChat(messageResponse: MessageResponse): Flow<Resource<List<MessageConstraintData>>> = chatRepository.setSocketMessageDataFromChat(messageResponse)
}