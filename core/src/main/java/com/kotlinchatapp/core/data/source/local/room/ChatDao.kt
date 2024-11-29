package com.kotlinchatapp.core.data.source.local.room

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.kotlinchatapp.core.data.source.local.entity.ConversationEntity
import com.kotlinchatapp.core.data.source.local.entity.MessageEntity
import com.kotlinchatapp.core.data.source.local.entity.ParticipantEntity
import com.kotlinchatapp.core.data.source.local.entity.UserEntity
import com.kotlinchatapp.core.data.source.local.entity.join.ConversationConstraintEntity
import com.kotlinchatapp.core.data.source.local.entity.join.MessageConstraintEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface ChatDao {
    @Query("SELECT * FROM users")
    fun getAllUsersNoFlow(): List<UserEntity>

    @Query("SELECT * FROM users WHERE id != :id")
    fun getUsersExceptUser(id: String): Flow<List<UserEntity>>

    @Query("SELECT * FROM users WHERE isLogin = :login")
    fun getActiveUser(login: Boolean): Flow<List<UserEntity>>

    @Query("SELECT * FROM users WHERE isLogin = :login")
    fun getActiveUserNoFlow(login: Boolean): List<UserEntity>

    @Query("SELECT * FROM participants")
    fun getAllParticipantsNoFlow(): List<ParticipantEntity>

    @Query("""
        SELECT p.* FROM participants AS p
        LEFT JOIN conversations AS c ON p.conversationId = c.id
        WHERE p.conversationId IN(SELECT conversationId FROM participants WHERE userId = :userId)        
        AND c.personal = :personal
        AND p.userId = :opponentId
    """)
    fun getOpponentParticipantsNoFlow(userId: String, opponentId: String, personal: Boolean): List<ParticipantEntity>

    @Query("SELECT * FROM conversations")
    fun getAllConversationsNoFlow(): List<ConversationEntity>

    @Query("""
        SELECT
            pu.id AS userId,
            c.id AS conversationId,
            m.userId AS senderId,
            m.message AS lastMessage,
            pu.username AS username,
            pu.photoProfileUrl AS photoUri
        FROM participants AS p
        LEFT JOIN (
            SELECT * FROM participants WHERE userId != :userId GROUP BY conversationId
        ) AS pc ON p.conversationId = pc.conversationId
        LEFT JOIN conversations AS c ON p.conversationId = c.id
        LEFT JOIN messages AS m ON p.latestMessageId = m.id
        LEFT JOIN users AS pu ON pc.userId = pu.id
        WHERE p.userId = :userId
        AND p.latestMessageId IS NOT NULL
        ORDER BY p.updatedAt
    """)
    fun getConversationsConstraintById(userId: String): Flow<List<ConversationConstraintEntity>>

    @Query("""
        SELECT
            pu.id AS userId,
            c.id AS conversationId,
            m.userId AS senderId,
            m.message AS lastMessage,
            pu.username AS username,
            pu.photoProfileUrl AS photoUri
        FROM participants AS p
        LEFT JOIN (
            SELECT * FROM participants WHERE conversationId = :cId AND userId != :userId GROUP BY conversationId
        ) AS pc ON p.conversationId = pc.conversationId
        LEFT JOIN conversations AS c ON p.conversationId = c.id
        LEFT JOIN messages AS m ON p.latestMessageId = m.id
        LEFT JOIN users AS pu ON pc.userId = pu.id
        AND p.latestMessageId IS NOT NULL
        WHERE p.conversationId = :cId
        GROUP BY p.conversationId
    """)
    fun getConversationsConstraintNoFlow(userId: String, cId: String): List<ConversationConstraintEntity>

    @Query("SELECT * FROM messages")
    fun getAllMessagesNoFlow(): List<MessageEntity>

    @Query("SELECT * FROM messages WHERE conversationId = :conId ORDER BY createdAt")
    fun getMessagesByConversationIdNoFlow(conId: String): List<MessageEntity>

    @Query("""
        SELECT
            m.id AS id,
            m.userId AS userId,
            u.username AS username,
            u.photoProfileUrl AS photoProfileUrl,
            m.message AS message
        FROM messages AS m
        LEFT JOIN users AS u ON m.userId = u.id
        WHERE m.conversationId = :conversationId
        ORDER BY m.createdAt
    """)
    fun getMessagesConstraintsByConversationId(conversationId: String): Flow<List<MessageConstraintEntity>>

    @Query("""
        SELECT
            m.id AS id,
            m.userId AS userId,
            u.username AS username,
            u.photoProfileUrl AS photoProfileUrl,
            m.message AS message
        FROM messages AS m
        LEFT JOIN users AS u ON m.userId = u.id
        WHERE m.id = :id
        ORDER BY m.createdAt
    """)
    fun getMessagesConstraintsByIdNoFlow(id: String): List<MessageConstraintEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertUsers(users: List<UserEntity>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMessage(message: MessageEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMessages(messages: List<MessageEntity>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertConversations(conversations: List<ConversationEntity>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertParticipants(participants: List<ParticipantEntity>)

    @Query("UPDATE users SET isLogin = :login")
    suspend fun setForceLogout(login: Boolean)
}