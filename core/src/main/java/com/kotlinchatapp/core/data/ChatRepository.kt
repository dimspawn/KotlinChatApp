package com.kotlinchatapp.core.data

import android.net.Uri
import com.kotlinchatapp.core.data.source.local.LocalDataSource
import com.kotlinchatapp.core.data.source.remote.RemoteDataSource
import com.kotlinchatapp.core.data.source.remote.network.ApiResponse
import com.kotlinchatapp.core.data.source.remote.response.MessageResponse
import com.kotlinchatapp.core.data.source.remote.response.UserResponse
import com.kotlinchatapp.core.data.source.remote.response.merge.ConstraintResponse
import com.kotlinchatapp.core.data.source.remote.response.merge.ParticipantsConversationsResponse
import com.kotlinchatapp.core.di.CoreScope
import com.kotlinchatapp.core.domain.model.AvailableData
import com.kotlinchatapp.core.domain.model.ConversationConstraintData
import com.kotlinchatapp.core.domain.model.MessageConstraintData
import com.kotlinchatapp.core.domain.model.MessageData
import com.kotlinchatapp.core.domain.model.ParticipantData
import com.kotlinchatapp.core.domain.model.UserData
import com.kotlinchatapp.core.domain.model.UserLoginData
import com.kotlinchatapp.core.domain.model.input.InputMessageData
import com.kotlinchatapp.core.domain.model.input.InputUserIdsData
import com.kotlinchatapp.core.domain.repository.IChatRepository
import com.kotlinchatapp.core.utils.DataMapper
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import javax.inject.Inject

@CoreScope
class ChatRepository @Inject constructor(
    private val localDataSource: LocalDataSource,
    private val remoteDataSource: RemoteDataSource,
) : IChatRepository {
    override fun setRegisterChat(
        username: String,
        email: String,
        password: String,
        photoUri: Uri?
    ): Flow<Resource<List<UserData>>> = object : NetworkBoundResource<List<UserData>, List<UserData>, List<UserResponse>>() {
        override fun loadFromDBFirst(): Flow<List<UserData>> {
            return localDataSource.getActiveUser(true).map { DataMapper.mapUserEntitiesToDomains(it) }
        }

        override fun loadFromDB(): Flow<List<UserData>> {
            return localDataSource.getActiveUser(true).map { DataMapper.mapUserEntitiesToDomains(it) }
        }

        override fun shouldFetch(data: List<UserData>, inspect: List<UserData>): Boolean = true

        override suspend fun prepareForFetch(data: List<UserData>, inspect: List<UserData>) {
            if (data.isNotEmpty()) { localDataSource.setForceLogout(false) }
            super.prepareForFetch(data, inspect)
        }

        override fun createCall(data: List<UserData>, inspect: List<UserData>): Flow<ApiResponse<List<UserResponse>>> = remoteDataSource.registerUser(username, email, password, photoUri)

        override suspend fun saveCallResult(data: List<UserResponse>) {
            val userList = DataMapper.mapUserResponsesToEntities(data, true)
            localDataSource.insertUsers(userList)
        }
    }.asFlow()

    override fun getCheckLogin(): Flow<List<UserData>> = flow {
        val users =  localDataSource.getActiveUser(true).map { DataMapper.mapUserEntitiesToDomains(it) }.first()
        emit(users)
    }.flowOn(Dispatchers.IO)

    override fun setLogout(): Flow<Resource<Boolean>> = object : NetworkBoundResource<Boolean, Boolean, Boolean> (){
        override fun loadFromDBFirst(): Flow<Boolean> = flow {
            val activeUser = localDataSource.getActiveUserNoFLow(true)
            emit(activeUser.isNotEmpty())
        }.flowOn(Dispatchers.IO)

        override fun loadFromDB(): Flow<Boolean> = flow {
            val activeUser = localDataSource.getActiveUserNoFLow(true)
            emit(activeUser.isNotEmpty())
        }.flowOn(Dispatchers.IO)

        override fun shouldFetch(data: Boolean, inspect: Boolean): Boolean = data

        override fun createCall(data: Boolean, inspect: Boolean): Flow<ApiResponse<Boolean>> = remoteDataSource.setLogOut()

        override suspend fun saveCallResult(data: Boolean) {
            localDataSource.setForceLogout(false)
        }
    }.asFlow()

    override fun getUsers(userId: String): Flow<Resource<List<UserData>>> = object : NetworkBoundResource<List<UserData>, List<UserData>, List<UserResponse>>(){
        override fun loadFromDBFirst(): Flow<List<UserData>> {
            return localDataSource.getUsersExcept(userId).map { DataMapper.mapUserEntitiesToDomains(it) }
        }

        override fun loadFromDB(): Flow<List<UserData>> {
            return localDataSource.getUsersExcept(userId).map { DataMapper.mapUserEntitiesToDomains(it) }
        }

        override fun shouldFetch(data: List<UserData>, inspect: List<UserData>): Boolean = true

        override fun createCall(data: List<UserData>, inspect: List<UserData>): Flow<ApiResponse<List<UserResponse>>> = remoteDataSource.getUsers(userId)

        override suspend fun saveCallResult(data: List<UserResponse>) {
            if (data.isNotEmpty()) {
                val userList = DataMapper.mapUserResponsesToEntities(data)
                localDataSource.insertUsers(userList)
            }
        }
    }.asFlow()

    override fun setLoginChat(userLoginData: UserLoginData): Flow<Resource<List<UserData>>> = object : NetworkBoundResource<List<UserData>, List<UserData>, List<UserResponse>>() {
        override fun loadFromDBFirst(): Flow<List<UserData>> {
            return localDataSource.getActiveUser(true).map { DataMapper.mapUserEntitiesToDomains(it) }
        }

        override fun loadFromDB(): Flow<List<UserData>> {
            return localDataSource.getActiveUser(true).map { DataMapper.mapUserEntitiesToDomains(it) }
        }

        override fun shouldFetch(data: List<UserData>, inspect: List<UserData>): Boolean = true

        override suspend fun prepareForFetch(data: List<UserData>, inspect: List<UserData>) {
            if (data.isNotEmpty()) { localDataSource.setForceLogout(false) }
            super.prepareForFetch(data, inspect)
        }

        override fun createCall(data: List<UserData>, inspect: List<UserData>): Flow<ApiResponse<List<UserResponse>>> = remoteDataSource.setLoginUser(userLoginData.email, userLoginData.password)

        override suspend fun saveCallResult(data: List<UserResponse>) {
            if (data.isNotEmpty()) {
                val userList = DataMapper.mapUserResponsesToEntities(data, true)
                localDataSource.insertUsers(userList)
            }
        }
    }.asFlow()

    override fun sendMessage(inputMessageData: InputMessageData): Flow<Resource<MessageData?>> = object : NetworkBoundResource<MessageData?, MessageData?, MessageResponse>() {
        override fun loadFromDBFirst(): Flow<MessageData?> = flow {
            var message: MessageData? = null
            val messages = localDataSource.getMessagesByConversationIdNoFlow(inputMessageData.conversationId)
            if (messages.isNotEmpty()) {
                val mData = DataMapper.mapSafetyMessageEntityToDomain(messages.last())
                message = mData
            }
            emit(message)
        }.flowOn(Dispatchers.IO)

        override fun loadFromDB(): Flow<MessageData?> = flow {
            var message: MessageData? = null
            val messages = localDataSource.getMessagesByConversationIdNoFlow(inputMessageData.conversationId)
            if (messages.isNotEmpty()) {
                val mData = DataMapper.mapSafetyMessageEntityToDomain(messages.last())
                message = mData
            }
            emit(message)
        }.flowOn(Dispatchers.IO)

        override fun shouldFetch(data: MessageData?, inspect: MessageData?): Boolean = true

        override fun createCall(data: MessageData?, inspect: MessageData?): Flow<ApiResponse<MessageResponse>> = remoteDataSource.setMessage(inputMessageData)

        override suspend fun saveCallResult(data: MessageResponse) {
            val message = DataMapper.mapMessageResponseToEntity(data)
            localDataSource.insertMessage(message)
        }
    }.asFlow()

    override fun createPersonalChat(inputUserIdsData: InputUserIdsData): Flow<Resource<List<ParticipantData>>> = object : NetworkBoundResource<List<ParticipantData>, List<ParticipantData>, ParticipantsConversationsResponse>() {
        override fun loadFromDBFirst(): Flow<List<ParticipantData>> = flow {
            val participants = localDataSource.getOpponentParticipantsNoFlow(inputUserIdsData.userId, inputUserIdsData.opponentId, true).map { DataMapper.mapParticipantEntityToDomain(it) }
            emit(participants)
        }.flowOn(Dispatchers.IO)

        override fun loadFromDB(): Flow<List<ParticipantData>> = flow {
            val participants = localDataSource.getOpponentParticipantsNoFlow(inputUserIdsData.userId, inputUserIdsData.opponentId, true).map { DataMapper.mapParticipantEntityToDomain(it) }
            emit(participants)
        }.flowOn(Dispatchers.IO)

        override fun shouldFetch(data: List<ParticipantData>, inspect: List<ParticipantData>): Boolean = inspect.isEmpty()

        override fun createCall(data: List<ParticipantData>, inspect: List<ParticipantData>): Flow<ApiResponse<ParticipantsConversationsResponse>> = remoteDataSource.createPersonalChat(inputUserIdsData.userId, inputUserIdsData.opponentId)

        override suspend fun saveCallResult(data: ParticipantsConversationsResponse) {
            if (data.conversations.isNotEmpty()) {
                val converses = DataMapper.mapConversationResponsesToEntities(data.conversations)
                localDataSource.insertConversations(converses)
            }
            if (data.participants.isNotEmpty()) {
                val parties = DataMapper.mapParticipantResponsesToEntities(data.participants)
                localDataSource.insertParticipants(parties)
            }
        }
    }.asFlow()

    override fun getMessages(userId: String, conversationId: String): Flow<Resource<List<MessageConstraintData>>> = object : NetworkBoundResource<List<MessageConstraintData>, AvailableData, ConstraintResponse>() {
        override fun loadFromDBFirst(): Flow<AvailableData> = flow {
            val avails = AvailableData(
                conversations = listOf(),
                users = localDataSource.getAllUsersNoFlow().map { DataMapper.mapUserEntityToDomain(it) },
                participants = listOf(),
                message = listOf()
            )
            emit(avails)
        }.flowOn(Dispatchers.IO)

        override fun loadFromDB(): Flow<List<MessageConstraintData>> {
            return localDataSource.getMessagesConstraintsByConversationId(conversationId).map { DataMapper.mapMessageConstraintEntitiesToDomains(it) }
        }

        override fun shouldFetch(data: List<MessageConstraintData>, inspect: AvailableData): Boolean = true

        override fun createCall(data: List<MessageConstraintData>, inspect: AvailableData): Flow<ApiResponse<ConstraintResponse>> = remoteDataSource.getMessages(inspect, conversationId)

        override suspend fun saveCallResult(data: ConstraintResponse) {
            data.messages?.let {
                if (it.isNotEmpty()) {
                    val messages = DataMapper.mapMessageResponsesToEntities(it)
                    localDataSource.insertMessages(messages)
                }
            }

            data.users?.let {
                if (it.isNotEmpty()) {
                    val users = DataMapper.mapUserResponsesToEntities(it).map { user ->
                        if (user.id == userId) { user.isLogin = true }
                        user
                    }
                    localDataSource.insertUsers(users)
                }
            }
        }
    }.asFlow()

    override fun getConstraintConversations(userId: String): Flow<Resource<List<ConversationConstraintData>>> = object : NetworkBoundResource<List<ConversationConstraintData>, AvailableData, ConstraintResponse>() {
        override fun loadFromDBFirst(): Flow<AvailableData> = flow {
            val avails = AvailableData(
                users = localDataSource.getAllUsersNoFlow().map { DataMapper.mapUserEntityToDomain(it) },
                participants = localDataSource.getAllParticipantsNoFlow().map { DataMapper.mapParticipantEntityToDomain(it) },
                conversations = localDataSource.getAllConversationsNoFlow().map { DataMapper.mapConversationEntityToDomain(it) },
                message = localDataSource.getAllMessagesNoFlow().map { DataMapper.mapMessageEntityToDomain(it) }
            )
            emit(avails)
        }.flowOn(Dispatchers.IO)

        override fun loadFromDB(): Flow<List<ConversationConstraintData>> {
            return localDataSource.getConversationsConstraintById(userId).map { DataMapper.mapConversationConstraintEntitiesToDomains(it) }
        }

        override fun shouldFetch(
            data: List<ConversationConstraintData>,
            inspect: AvailableData
        ): Boolean = true

        override fun createCall(
            data: List<ConversationConstraintData>,
            inspect: AvailableData
        ): Flow<ApiResponse<ConstraintResponse>> = remoteDataSource.getConstraintData(userId)

        override suspend fun saveCallResult(data: ConstraintResponse) {
            data.users?.let {
                val uEntities = DataMapper.mapUserResponsesToEntities(it)
                localDataSource.insertUsers(uEntities)
            }
            data.conversations?.let {
                val cEntities = DataMapper.mapConversationResponsesToEntities(it)
                localDataSource.insertConversations(cEntities)
            }
            data.participants?.let {
                val pEntities = DataMapper.mapParticipantResponsesToEntities(it)
                localDataSource.insertParticipants(pEntities)
            }
            data.messages?.let {
                val mEntities = DataMapper.mapMessageResponsesToEntities(it)
                localDataSource.insertMessages(mEntities)
            }
        }
    }.asFlow()

    override fun setSocketMessageDataFromDashboard(
        userId: String,
        messageResponse: MessageResponse
    ): Flow<Resource<List<ConversationConstraintData>>> = object : NetworkBoundResource<List<ConversationConstraintData>, List<ConversationConstraintData>, ConstraintResponse>() {
        override suspend fun prepare() {
            super.prepare()
            val entity = DataMapper.mapMessageResponseToEntity(messageResponse)
            localDataSource.insertMessage(entity)
        }
        override fun loadFromDBFirst(): Flow<List<ConversationConstraintData>> = flow {
            val constraintList = mutableListOf<ConversationConstraintData>()
            messageResponse.conversationId?.let { cId ->
                val data = localDataSource.getConversationsConstraintNoFlow(userId, cId).map { DataMapper.mapConversationConstraintEntityToDomain(it)  }
                constraintList.addAll(data)
            }
            emit(constraintList)
        }.flowOn(Dispatchers.IO)

        override fun loadFromDB(): Flow<List<ConversationConstraintData>> = flow {
            val constraintList = mutableListOf<ConversationConstraintData>()
            messageResponse.conversationId?.let { cId ->
                val data = localDataSource.getConversationsConstraintNoFlow(userId, cId).map { DataMapper.mapConversationConstraintEntityToDomain(it)  }
                constraintList.addAll(data)
            }
            emit(constraintList)
        }.flowOn(Dispatchers.IO)

        override fun shouldFetch(
            data: List<ConversationConstraintData>,
            inspect: List<ConversationConstraintData>
        ): Boolean = false

        override fun createCall(
            data: List<ConversationConstraintData>,
            inspect: List<ConversationConstraintData>
        ): Flow<ApiResponse<ConstraintResponse>> = remoteDataSource.getConstraintData(userId)

        override suspend fun saveCallResult(data: ConstraintResponse) {}
    }.asFlow()

    override fun setSocketMessageDataFromChat(messageResponse: MessageResponse): Flow<Resource<List<MessageConstraintData>>> = object : NetworkBoundResource<List<MessageConstraintData>, List<MessageConstraintData>, ConstraintResponse>() {
        override suspend fun prepare() {
            super.prepare()
            val entity = DataMapper.mapMessageResponseToEntity(messageResponse)
            localDataSource.insertMessage(entity)
        }
        override fun loadFromDBFirst(): Flow<List<MessageConstraintData>> = flow {
            val messageList = mutableListOf<MessageConstraintData>()
            messageResponse.id?.let { mId ->
                val data = localDataSource.getMessagesConstraintsByIdNoFlow(mId).map { DataMapper.mapMessageConstraintEntityToDomain(it)  }
                messageList.addAll(data)
            }
            emit(messageList)
        }.flowOn(Dispatchers.IO)

        override fun loadFromDB(): Flow<List<MessageConstraintData>> = flow {
            val messageList = mutableListOf<MessageConstraintData>()
            messageResponse.id?.let { mId ->
                val data = localDataSource.getMessagesConstraintsByIdNoFlow(mId).map { DataMapper.mapMessageConstraintEntityToDomain(it)  }
                messageList.addAll(data)
            }
            emit(messageList)
        }.flowOn(Dispatchers.IO)

        override fun shouldFetch(
            data: List<MessageConstraintData>,
            inspect: List<MessageConstraintData>
        ): Boolean = false

        override fun createCall(
            data: List<MessageConstraintData>,
            inspect: List<MessageConstraintData>
        ): Flow<ApiResponse<ConstraintResponse>> = remoteDataSource.getConstraintData("")

        override suspend fun saveCallResult(data: ConstraintResponse) {}
    }.asFlow()
}