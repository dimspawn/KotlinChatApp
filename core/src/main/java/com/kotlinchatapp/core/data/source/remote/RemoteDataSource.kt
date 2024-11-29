package com.kotlinchatapp.core.data.source.remote

import android.net.Uri
import android.util.Log
import com.kotlinchatapp.core.data.source.remote.network.ApiResponse
import com.kotlinchatapp.core.data.source.remote.network.INetworkService
import com.kotlinchatapp.core.data.source.remote.response.ConversationResponse
import com.kotlinchatapp.core.data.source.remote.response.FilterResponse
import com.kotlinchatapp.core.data.source.remote.response.MessageResponse
import com.kotlinchatapp.core.data.source.remote.response.MultipleFilterResponse
import com.kotlinchatapp.core.data.source.remote.response.ParticipantResponse
import com.kotlinchatapp.core.data.source.remote.response.RemoteFilterResponse
import com.kotlinchatapp.core.data.source.remote.response.UserResponse
import com.kotlinchatapp.core.data.source.remote.response.merge.ConstraintResponse
import com.kotlinchatapp.core.data.source.remote.response.merge.ParticipantsConversationsResponse
import com.kotlinchatapp.core.di.CoreScope
import com.kotlinchatapp.core.domain.model.AvailableData
import com.kotlinchatapp.core.domain.model.input.InputMessageData
import com.kotlinchatapp.core.utils.Constants
import com.kotlinchatapp.core.utils.Logger
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import java.util.Date
import javax.inject.Inject

@CoreScope
class RemoteDataSource @Inject constructor(
    private val networkService: INetworkService,
    private val androidLogger: Logger
){
    companion object {
        const val TAG = "RemoteDataSource"
    }

    fun registerUser(username: String, email: String, password: String, photoUri: Uri?): Flow<ApiResponse<List<UserResponse>>> {
        return flow {
            try {
                val results = networkService.registerUser(username, email, password, photoUri)
                emit(ApiResponse.Success(results))
            } catch (e: Exception){
                emit(ApiResponse.Error(e.message.toString()))
                androidLogger.e(TAG, e.toString())
            }
        }.flowOn(Dispatchers.IO)
    }

    fun setLoginUser(email: String, password: String): Flow<ApiResponse<List<UserResponse>>> {
        return flow {
            try {
                val users = networkService.loginUser(email, password)
                emit(ApiResponse.Success(users))
            } catch (e: Exception) {
                emit(ApiResponse.Error(e.message.toString()))
                androidLogger.e(TAG, e.message.toString())
            }
        }.flowOn(Dispatchers.IO)
    }

    fun getUsers(userId: String): Flow<ApiResponse<List<UserResponse>>> {
        return flow {
            try {
                val exFilters = FilterResponse(
                    key = Constants.ID,
                    value = userId
                )
                val remoteFilter = RemoteFilterResponse(excludes = listOf(exFilters))
                val users = networkService.getUserListData(remoteFilters = remoteFilter)
                    .map {
                        it.email = it.email?.lowercase()
                        it
                    }
                emit(ApiResponse.Success(users))
            } catch (e: Exception) {
                emit(ApiResponse.Error(e.message.toString()))
                androidLogger.e(TAG, e.message.toString())
            }
        }.flowOn(Dispatchers.IO)
    }

    fun setLogOut(): Flow<ApiResponse<Boolean>> {
        return flow<ApiResponse<Boolean>> {
            // not yet implemented
            emit(ApiResponse.Success(true))
        }.flowOn(Dispatchers.IO)
    }

    fun setMessage(inputMessageData: InputMessageData): Flow<ApiResponse<MessageResponse>> {
        return flow {
            try {
                val messageResponse = MessageResponse(
                    conversationId = inputMessageData.conversationId,
                    userId = inputMessageData.userId,
                    message = inputMessageData.message,
                    type = inputMessageData.type,
                    visited = 0,
                    createdAt = Date().time,
                    updatedAt = Date().time
                )

                Log.d(TAG, "conId = ${inputMessageData.conversationId}")

                val mid = networkService.messageInsertOrUpdate(messageResponse)
                messageResponse.id = mid

                //updating information
                networkService.updateParticipants(conId = inputMessageData.conversationId, mId = mid)
                emit(ApiResponse.Success(messageResponse))
            }catch (e: Exception) {
                emit(ApiResponse.Error(e.message.toString()))
                androidLogger.e(TAG, e.message.toString())
            }
        }.flowOn(Dispatchers.IO)
    }

    fun createPersonalChat(userId: String, opponentId: String): Flow<ApiResponse<ParticipantsConversationsResponse>> {
        return flow {
            val res = try {
                val conversations = mutableListOf<ConversationResponse>()
                val participants = mutableListOf<ParticipantResponse>()
                var createNewChat = true

                val pmRemoteFilters = RemoteFilterResponse(mFilters = listOf(MultipleFilterResponse(key = Constants.USER_ID, values = listOf(userId, opponentId))))
                val participantsByIds = networkService.getParticipantListData(pmRemoteFilters)
                val conIds = participantsByIds.filter { it.userId == userId }.mapNotNull { it.conversationId }.distinct()
                val opponentParties = participantsByIds.filter { conIds.contains(it.conversationId) && it.userId == opponentId }
                if (opponentParties.isNotEmpty()) {
                    val combineConIds = opponentParties.mapNotNull { it.conversationId }
                    if (combineConIds.isNotEmpty()) {
                        val cRemoteFilters = RemoteFilterResponse(
                            filters = listOf(FilterResponse(key = Constants.PERSONAL, value = true)),
                            mFilters = listOf(MultipleFilterResponse(key = Constants.ID, values = combineConIds))
                        )
                        val conversationByCombineConIds = networkService.getConversationListData(cRemoteFilters)
                        if (conversationByCombineConIds.isNotEmpty()) {
                            createNewChat = false
                            conversations.addAll(conversationByCombineConIds)
                            val newConIds = conversationByCombineConIds.mapNotNull { it.id }
                            val parties = participantsByIds.filter { newConIds.contains(it.conversationId) }
                            participants.addAll(parties)
                        }
                    }
                }

                if (createNewChat) {
                    val conversationResponse = ConversationResponse(
                        conversationName = "chat${userId}with$opponentId",
                        personal = 1,
                    )

                    val converseId = networkService.conversationInsertOrUpdate(data = conversationResponse)
                    conversationResponse.id = converseId
                    conversations.add(conversationResponse)

                    val participantResponse1 = ParticipantResponse(
                        conversationId = converseId,
                        userId = userId,
                        blocked = 0,
                        status = "init",
                        createdAt = Date().time,
                        updatedAt = Date().time
                    )

                    participantResponse1.id = networkService.participantInsertOrUpdate(data = participantResponse1)
                    participants.add(participantResponse1)

                    val participantResponse2 = ParticipantResponse(
                        conversationId = converseId,
                        userId = opponentId,
                        blocked = 0,
                        status = "init",
                        createdAt = Date().time,
                        updatedAt = Date().time
                    )

                    participantResponse2.id = networkService.participantInsertOrUpdate(data = participantResponse2)
                    participants.add(participantResponse2)
                }
                val participantsConversationsResponse = ParticipantsConversationsResponse(
                    conversations = conversations,
                    participants = participants
                )
                ApiResponse.Success(participantsConversationsResponse)
            } catch (e: Exception) {
                androidLogger.e(TAG, e.message.toString())
                ApiResponse.Error(e.message.toString())
            }
            emit(res)
        }.flowOn(Dispatchers.IO)
    }

    fun getMessages(availableData: AvailableData, conversationId: String): Flow<ApiResponse<ConstraintResponse>> {
        return flow {
            try {
                val availUserIds = availableData.users.map { it.id }
                val messages = mutableListOf<MessageResponse>()
                val needUserIds = mutableListOf<String>()
                val users = mutableListOf<UserResponse>()

                val mFilters = RemoteFilterResponse(filters = listOf(FilterResponse(key = Constants.CONVERSATION_ID, value = conversationId)))
                val msg = networkService.getMessageListData(mFilters)
                messages.addAll(msg)

                for (message in messages) {
                    message.userId?.let {
                        if (!availUserIds.contains(it) && !needUserIds.contains(it)) {
                            needUserIds.add(it)
                        }
                    }
                }

                for (neededUserId in needUserIds) {
                    val uFilters = RemoteFilterResponse(filters = listOf(FilterResponse(key = Constants.ID, value = neededUserId)))
                    val usr = networkService.getUserListData(uFilters)
                    users.addAll(usr)
                }

                val constraintResponse = ConstraintResponse(
                    users = users,
                    messages = messages
                )
                emit(ApiResponse.Success(constraintResponse))
            } catch (e: Exception) {
                emit(ApiResponse.Error(e.message.toString()))
                androidLogger.e(TAG, e.message.toString())
            }
        }.flowOn(Dispatchers.IO)
    }

    fun getConstraintData(userId: String): Flow<ApiResponse<ConstraintResponse>> {
        return flow {
            try {
                val conversationsResult = mutableListOf<ConversationResponse>()
                val participantsResult = mutableListOf<ParticipantResponse>()
                val messageResult = mutableListOf<MessageResponse>()
                val userResult = mutableListOf<UserResponse>()

                val remoteFilters = RemoteFilterResponse(filters = listOf(FilterResponse(key = Constants.USER_ID, value = userId)))
                val participantsByUserId = networkService.getParticipantListData(remoteFilters)

                for(participantByUserId in participantsByUserId) {
                    participantsResult.add(participantByUserId)
                    participantByUserId.conversationId?.let { cId ->
                        val cFilters = RemoteFilterResponse(filters = listOf(FilterResponse(key = Constants.ID, value = cId)))
                        val convByPRes = networkService.getConversationListData(remoteFilters = cFilters)
                        conversationsResult.addAll(convByPRes)
                    }

                    participantByUserId.latestMessageId?.let { lastId ->
                        val lFilters = RemoteFilterResponse(filters = listOf(FilterResponse(key = Constants.ID, value = lastId)))
                        val msgByPRes = networkService.getMessageListData(remoteFilters = lFilters)
                        messageResult.addAll(msgByPRes)
                    }
                }

                for (cResult in conversationsResult) {
                    cResult.id?.let { id ->
                        val pFilters = RemoteFilterResponse(
                            filters = listOf(FilterResponse(key = Constants.CONVERSATION_ID, value = id)),
                            excludes = listOf(FilterResponse(key = Constants.USER_ID, value = userId))
                        )
                        val partyByOpponent = networkService.getParticipantListData(remoteFilters = pFilters)
                        participantsResult.addAll(partyByOpponent)
                    }
                }

                val uIds = participantsResult.mapNotNull { it.userId }.distinct()
                for (uid in uIds) {
                    if (uid != userId) {
                        val uFilters = RemoteFilterResponse(filters = listOf(FilterResponse(key = Constants.ID, value = uid)))
                        val userByPRes = networkService.getUserListData(remoteFilters = uFilters)
                        userResult.addAll(userByPRes)
                    }
                }

                val constraintResponse = ConstraintResponse(
                    users = userResult,
                    conversations = conversationsResult,
                    participants = participantsResult,
                    messages = messageResult
                )
                emit(ApiResponse.Success(constraintResponse))
            } catch (e: Exception) {
                androidLogger.e(TAG, e.message.toString())
                emit(ApiResponse.Error(errorMessage = e.message.toString()))
            }
        }.flowOn(Dispatchers.IO)
    }
}