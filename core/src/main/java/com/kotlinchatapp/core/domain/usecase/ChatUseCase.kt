package com.kotlinchatapp.core.domain.usecase

import android.net.Uri
import com.kotlinchatapp.core.data.Resource
import com.kotlinchatapp.core.data.source.remote.response.MessageResponse
import com.kotlinchatapp.core.domain.model.ConversationConstraintData
import com.kotlinchatapp.core.domain.model.MessageData
import com.kotlinchatapp.core.domain.model.MessageConstraintData
import com.kotlinchatapp.core.domain.model.ParticipantData
import com.kotlinchatapp.core.domain.model.UserData
import com.kotlinchatapp.core.domain.model.UserLoginData
import com.kotlinchatapp.core.domain.model.input.InputMessageData
import com.kotlinchatapp.core.domain.model.input.InputUserIdsData
import kotlinx.coroutines.flow.Flow

interface ChatUseCase {
    fun setRegisterChat(username: String, email: String, password: String, photoUri: Uri?): Flow<Resource<List<UserData>>>
    fun getCheckLogin(): Flow<List<UserData>>
    fun setLogout(): Flow<Resource<Boolean>>
    fun getUsers(userId: String): Flow<Resource<List<UserData>>>
    fun setLoginChat(userLoginData: UserLoginData): Flow<Resource<List<UserData>>>
    fun sendMessage(inputMessageData: InputMessageData): Flow<Resource<MessageData?>>
    fun createPersonalChat(inputUserIdsData: InputUserIdsData): Flow<Resource<List<ParticipantData>>>
    fun getMessages(userId: String, conversationId: String): Flow<Resource<List<MessageConstraintData>>>
    fun getConstraintConversations(userId: String): Flow<Resource<List<ConversationConstraintData>>>
    fun setSocketMessageDataFromDashboard(userId: String, messageResponse: MessageResponse): Flow<Resource<List<ConversationConstraintData>>>
    fun setSocketMessageDataFromChat(messageResponse: MessageResponse): Flow<Resource<List<MessageConstraintData>>>
}