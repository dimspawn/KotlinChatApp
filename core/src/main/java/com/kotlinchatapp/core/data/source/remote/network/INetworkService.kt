package com.kotlinchatapp.core.data.source.remote.network

import android.net.Uri
import com.kotlinchatapp.core.data.source.remote.response.ConversationResponse
import com.kotlinchatapp.core.data.source.remote.response.MessageResponse
import com.kotlinchatapp.core.data.source.remote.response.ParticipantResponse
import com.kotlinchatapp.core.data.source.remote.response.RemoteFilterResponse
import com.kotlinchatapp.core.data.source.remote.response.UserResponse

interface INetworkService {
    suspend fun userInsertOrUpdate(data: UserResponse): String
    suspend fun conversationInsertOrUpdate(data: ConversationResponse): String
    suspend fun participantInsertOrUpdate(data: ParticipantResponse): String
    suspend fun messageInsertOrUpdate(data: MessageResponse): String
    suspend fun updateParticipants(conId: String, mId: String)
    suspend fun getUserListData(remoteFilters : RemoteFilterResponse): List<UserResponse>
    suspend fun getParticipantListData(remoteFilters : RemoteFilterResponse): List<ParticipantResponse>
    suspend fun getConversationListData(remoteFilters : RemoteFilterResponse): List<ConversationResponse>
    suspend fun getMessageListData(remoteFilters : RemoteFilterResponse): List<MessageResponse>
    suspend fun registerUser(username: String, email: String, password: String, photoUri: Uri?): List<UserResponse>
    suspend fun loginUser(email: String, password: String): List<UserResponse>
}