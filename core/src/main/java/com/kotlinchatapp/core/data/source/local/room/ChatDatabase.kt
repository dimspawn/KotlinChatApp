package com.kotlinchatapp.core.data.source.local.room

import androidx.room.Database
import androidx.room.RoomDatabase
import com.kotlinchatapp.core.data.source.local.entity.ConversationEntity
import com.kotlinchatapp.core.data.source.local.entity.MessageEntity
import com.kotlinchatapp.core.data.source.local.entity.ParticipantEntity
import com.kotlinchatapp.core.data.source.local.entity.UserEntity

@Database(entities = [UserEntity::class, MessageEntity::class, ParticipantEntity::class, ConversationEntity::class], version = 7, exportSchema = false)
abstract class ChatDatabase: RoomDatabase() {
    abstract fun chatDao(): ChatDao
}