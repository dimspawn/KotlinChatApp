package com.kotlinchatapp.core.data.source.local

import com.kotlinchatapp.core.data.source.local.entity.ConversationEntity
import com.kotlinchatapp.core.data.source.local.entity.MessageEntity
import com.kotlinchatapp.core.data.source.local.entity.ParticipantEntity
import com.kotlinchatapp.core.data.source.local.entity.UserEntity
import com.kotlinchatapp.core.data.source.local.entity.join.ConversationConstraintEntity
import com.kotlinchatapp.core.data.source.local.entity.join.MessageConstraintEntity
import com.kotlinchatapp.core.data.source.local.room.ChatDao
import com.kotlinchatapp.core.di.CoreScope
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

@CoreScope
class LocalDataSource @Inject constructor(private val chatDao: ChatDao){
    fun getAllUsersNoFlow(): List<UserEntity> = chatDao.getAllUsersNoFlow()
    fun getUsersExcept(id: String): Flow<List<UserEntity>> = chatDao.getUsersExceptUser(id)
    fun getActiveUser(login: Boolean): Flow<List<UserEntity>> = chatDao.getActiveUser(login)
    fun getActiveUserNoFLow(login: Boolean): List<UserEntity> = chatDao.getActiveUserNoFlow(login)
    fun getAllParticipantsNoFlow(): List<ParticipantEntity> = chatDao.getAllParticipantsNoFlow()
    fun getOpponentParticipantsNoFlow(userId: String, opponentId: String, personal: Boolean): List<ParticipantEntity> = chatDao.getOpponentParticipantsNoFlow(userId, opponentId, personal)
    fun getAllConversationsNoFlow(): List<ConversationEntity> = chatDao.getAllConversationsNoFlow()
    fun getConversationsConstraintById(userId: String): Flow<List<ConversationConstraintEntity>> = chatDao.getConversationsConstraintById(userId)
    fun getConversationsConstraintNoFlow(userId: String, cId: String): List<ConversationConstraintEntity> = chatDao.getConversationsConstraintNoFlow(userId, cId)
    fun getAllMessagesNoFlow(): List<MessageEntity> = chatDao.getAllMessagesNoFlow()
    fun getMessagesByConversationIdNoFlow(conId: String): List<MessageEntity> = chatDao.getMessagesByConversationIdNoFlow(conId)
    fun getMessagesConstraintsByConversationId(conversationId: String): Flow<List<MessageConstraintEntity>> = chatDao.getMessagesConstraintsByConversationId(conversationId)
    fun getMessagesConstraintsByIdNoFlow(id: String): List<MessageConstraintEntity> = chatDao.getMessagesConstraintsByIdNoFlow(id)
    suspend fun insertUsers(users: List<UserEntity>) { chatDao.insertUsers(users) }
    suspend fun insertMessage(message: MessageEntity) { chatDao.insertMessage(message) }
    suspend fun insertMessages(messages: List<MessageEntity>) { chatDao.insertMessages(messages) }
    suspend fun insertConversations(conversations: List<ConversationEntity>) { chatDao.insertConversations(conversations) }
    suspend fun insertParticipants(participants: List<ParticipantEntity>){ chatDao.insertParticipants(participants) }
    suspend fun setForceLogout(login: Boolean) { chatDao.setForceLogout(login) }
}