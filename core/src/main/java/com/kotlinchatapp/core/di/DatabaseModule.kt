package com.kotlinchatapp.core.di

import android.content.Context
import androidx.room.Room
import com.kotlinchatapp.core.data.source.local.room.ChatDao
import com.kotlinchatapp.core.data.source.local.room.ChatDatabase
import dagger.Module
import dagger.Provides

@Module
class DatabaseModule {
    @CoreScope
    @Provides
    fun provideDatabase(context: Context): ChatDatabase {
        return Room.databaseBuilder(
            context,
            ChatDatabase::class.java, "ChatLocal.db"
        ).fallbackToDestructiveMigration()
            .build()
    }

    @Provides
    fun provideChatDao(database: ChatDatabase): ChatDao = database.chatDao()
}