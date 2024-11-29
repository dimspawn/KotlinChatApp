package com.kotlinchatapp.core.utils

import com.kotlinchatapp.core.data.source.local.entity.ConversationEntity
import com.kotlinchatapp.core.data.source.local.entity.MessageEntity
import com.kotlinchatapp.core.data.source.local.entity.ParticipantEntity
import com.kotlinchatapp.core.data.source.local.entity.UserEntity
import com.kotlinchatapp.core.data.source.local.entity.join.ConversationConstraintEntity
import com.kotlinchatapp.core.data.source.local.entity.join.MessageConstraintEntity
import com.kotlinchatapp.core.data.source.remote.response.ConversationResponse
import com.kotlinchatapp.core.data.source.remote.response.MessageResponse
import com.kotlinchatapp.core.data.source.remote.response.ParticipantResponse
import com.kotlinchatapp.core.data.source.remote.response.UserResponse
import com.kotlinchatapp.core.domain.model.ConversationConstraintData
import com.kotlinchatapp.core.domain.model.ConversationData
import com.kotlinchatapp.core.domain.model.MessageConstraintData
import com.kotlinchatapp.core.domain.model.MessageData
import com.kotlinchatapp.core.domain.model.ParticipantData
import com.kotlinchatapp.core.domain.model.UserData

object DataMapper {
    fun mapUserEntityToDomain(userEntity: UserEntity): UserData = UserData(
        id = userEntity.id,
        username = userEntity.username,
        email = userEntity.email,
        photoProfileUrl = userEntity.photoProfileUrl,
        isLogin = userEntity.isLogin
    )
    fun mapUserEntitiesToDomains(userEntities: List<UserEntity>): List<UserData> = userEntities.map {
        mapUserEntityToDomain(it)
    }

    fun mapUserResponsesToEntities(userResponses: List<UserResponse>, isLogin: Boolean = false): List<UserEntity> = userResponses.map {
        UserEntity(
            id = it.id ?: "[id]",
            username = it.username ?: "[username]",
            email = it.email ?: "[email]",
            photoProfileUrl = it.photoProfileUrl,
            isLogin = isLogin
        )
    }

    fun mapConversationEntityToDomain(conversationEntity: ConversationEntity): ConversationData = ConversationData(
        id = conversationEntity.id,
        conversationName = conversationEntity.conversationName,
        personal = conversationEntity.personal
    )

    fun mapConversationEntitiesToDomains(conEntities: List<ConversationEntity>): List<ConversationData> = conEntities.map {
        mapConversationEntityToDomain(it)
    }

    private fun mapConversationResponseToEntity(conversationResponse: ConversationResponse): ConversationEntity = ConversationEntity(
        id = conversationResponse.id ?: "[id]",
        conversationName = conversationResponse.conversationName ?: "[conversation name]",
        personal = conversationResponse.personal ?: 0
    )

    fun mapConversationResponsesToEntities(conversationResponses: List<ConversationResponse>): List<ConversationEntity> = conversationResponses.map {
        mapConversationResponseToEntity(it)
    }

    fun mapConversationResponseToDomain(conversationResponse: ConversationResponse): ConversationData = ConversationData(
        id = conversationResponse.id ?: "[id]",
        conversationName = conversationResponse.conversationName ?: "[conversation name]",
        personal = conversationResponse.personal ?: 0
    )

    private fun mapConversationDomainToEntity(conversationData: ConversationData): ConversationEntity = ConversationEntity(
        id = conversationData.id,
        conversationName = conversationData.conversationName,
        personal = conversationData.personal,
    )

    fun mapConversationDomainsToEntities(conversations: List<ConversationData>): List<ConversationEntity> = conversations.map {
        mapConversationDomainToEntity(it)
    }

    private fun mapParticipantResponseToEntity(participantResponse: ParticipantResponse): ParticipantEntity = ParticipantEntity(
        id = participantResponse.id ?: "[id]",
        conversationId = participantResponse.conversationId ?: "[conversation id]",
        userId = participantResponse.userId ?: "[user id]",
        blocked = participantResponse.blocked ?: 0,
        latestMessageId = participantResponse.latestMessageId,
        status = participantResponse.status ?: "[status]",
        updatedAt = participantResponse.updatedAt ?: 0,
        createdAt = participantResponse.createdAt ?: 0
    )

    fun mapParticipantResponsesToEntities(participantResponses: List<ParticipantResponse>): List<ParticipantEntity> = participantResponses.map {
        mapParticipantResponseToEntity(it)
    }

    fun mapParticipantEntityToDomain(participantEntity: ParticipantEntity): ParticipantData = ParticipantData(
        id = participantEntity.id,
        conversationId = participantEntity.conversationId,
        userId = participantEntity.userId,
        blocked = participantEntity.blocked,
        latestMessageId = participantEntity.latestMessageId,
        status = participantEntity.status,
        updatedAt = participantEntity.updatedAt,
        createdAt = participantEntity.createdAt
    )

    private fun mapParticipantDomainToEntity(participantData: ParticipantData): ParticipantEntity = ParticipantEntity(
        id = participantData.id,
        conversationId = participantData.conversationId,
        userId = participantData.userId,
        blocked = participantData.blocked,
        latestMessageId = participantData.latestMessageId,
        status = participantData.status,
        updatedAt = participantData.updatedAt,
        createdAt = participantData.createdAt
    )

    fun mapParticipantDomainsToEntities(participants: List<ParticipantData>): List<ParticipantEntity> = participants.map {
        mapParticipantDomainToEntity(it)
    }

//    fun mapParticipantResponseToDomain(participantResponse: ParticipantResponse): ParticipantData = ParticipantData(
//        id = participantResponse.id ?: "[id]",
//        conversationId = participantResponse.conversationId ?: "[conversation id]",
//        userId = participantResponse.userId ?: "[user id]",
//        blocked = participantResponse.blocked ?: 0,
//        latestMessageId = participantResponse.latestMessageId,
//        status = participantResponse.status ?: "[status]",
//        updatedAt = participantResponse.updatedAt ?: "1970-01-01 00:00:00",
//        createdAt = participantResponse.createdAt ?: "1870-01-01 00:00:00"
//    )

    fun mapMessageDomainToEntity(messageData: MessageData): MessageEntity = MessageEntity(
        id = messageData.id,
        conversationId = messageData.conversationId,
        userId = messageData.userId,
        message = messageData.message,
        type = messageData.type,
        visited = messageData.visited,
        createdAt = messageData.createdAt,
        updatedAt = messageData.updatedAt
    )

    fun mapSafetyMessageDomainToResponse(messageData: MessageData?): MessageResponse = MessageResponse(
        id = messageData?.id,
        conversationId = messageData?.conversationId,
        userId = messageData?.userId,
        message = messageData?.message,
        type = messageData?.type,
        visited = messageData?.visited,
        createdAt = messageData?.createdAt,
        updatedAt = messageData?.updatedAt
    )

    fun mapMessageEntityToDomain(messageEntity: MessageEntity): MessageData = MessageData(
        id = messageEntity.id,
        conversationId = messageEntity.conversationId,
        userId = messageEntity.userId,
        message = messageEntity.message,
        type = messageEntity.type,
        visited = messageEntity.visited,
        createdAt = messageEntity.createdAt,
        updatedAt = messageEntity.updatedAt
    )

    fun mapMessageResponseToEntity(messageResponse: MessageResponse): MessageEntity = MessageEntity(
        id = messageResponse.id ?: "[message id]",
        conversationId = messageResponse.conversationId ?: "[conversation id]",
        userId = messageResponse.userId ?: "[user id]",
        message = messageResponse.message ?: "[message]",
        type = messageResponse.type ?: "[type]",
        visited = messageResponse.visited ?: 0,
        createdAt = messageResponse.createdAt ?: 0,
        updatedAt = messageResponse.updatedAt ?: 0
    )

//    fun mapMessageResponseToDomain(messageResponse: MessageResponse): MessageData = MessageData(
//        id = messageResponse.id ?: "[message id]",
//        conversationId = messageResponse.conversationId ?: "[conversation id]",
//        userId = messageResponse.userId ?: "[user id]",
//        message = messageResponse.message ?: "[message]",
//        type = messageResponse.type ?: "[type]",
//        visited = messageResponse.visited ?: 0,
//        createdAt = messageResponse.createdAt ?: "1970-01-01 00:00:00",
//        updatedAt = messageResponse.updatedAt ?: "1970-01-01 00:00:00"
//    )

    fun mapMessageConstraintEntityToDomain(messageConstraintEntity: MessageConstraintEntity): MessageConstraintData = MessageConstraintData(
        id = messageConstraintEntity.id ?: "[id]",
        userId = messageConstraintEntity.userId ?: "[userId]",
        username = messageConstraintEntity.username ?: "[username]",
        photoProfileUrl = messageConstraintEntity.photoProfileUrl,
        message = messageConstraintEntity.message ?: "[message]"
    )

    fun mapMessageConstraintEntitiesToDomains(messageConstraintEntities: List<MessageConstraintEntity>): List<MessageConstraintData> = messageConstraintEntities.map {
        mapMessageConstraintEntityToDomain(it)
    }

    fun mapSafetyMessageEntityToDomain(messageEntity: MessageEntity?): MessageData? {
        return messageEntity?.let {
            MessageData(
                id = it.id,
                conversationId = it.conversationId,
                userId = it.userId,
                message = it.message,
                type = it.type,
                visited = it.visited,
                createdAt = it.createdAt,
                updatedAt = it.updatedAt
            )
        }
    }

    fun mapMessageResponsesToEntities(messageResponses: List<MessageResponse>): List<MessageEntity> = messageResponses.map {
        mapMessageResponseToEntity(it)
    }

    fun mapConversationConstraintEntityToDomain(conversationConstraintEntity: ConversationConstraintEntity): ConversationConstraintData = ConversationConstraintData(
        userId = conversationConstraintEntity.userId,
        conversationId = conversationConstraintEntity.conversationId,
        senderId = conversationConstraintEntity.senderId,
        lastMessage = conversationConstraintEntity.lastMessage,
        username = conversationConstraintEntity.username,
        photoUri = conversationConstraintEntity.photoUri
    )

    fun mapConversationConstraintEntitiesToDomains(conversationsConstraint: List<ConversationConstraintEntity>): List<ConversationConstraintData> = conversationsConstraint.map {
        mapConversationConstraintEntityToDomain(it)
    }
}