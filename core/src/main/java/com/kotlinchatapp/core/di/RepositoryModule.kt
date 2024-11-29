package com.kotlinchatapp.core.di

import android.util.Log
import com.kotlinchatapp.core.data.ChatRepository
import com.kotlinchatapp.core.data.source.remote.network.INetworkService
import com.kotlinchatapp.core.data.source.remote.network.NetworkService
import com.kotlinchatapp.core.domain.repository.IChatRepository
import com.kotlinchatapp.core.domain.usecase.ChatInteract
import com.kotlinchatapp.core.domain.usecase.ChatUseCase
import com.kotlinchatapp.core.utils.AndroidLogger
import com.kotlinchatapp.core.utils.Logger
import dagger.Binds
import dagger.Module

@Module(includes = [DatabaseModule::class, NetworkModule::class])
abstract class RepositoryModule {
    @Binds
    abstract fun provideRepository(chatRepository: ChatRepository): IChatRepository

    @Binds
    abstract fun provideChatUseCase(chatInteract: ChatInteract): ChatUseCase

    @Binds
    abstract fun provideFirebaseService(networkService: NetworkService): INetworkService

    @Binds
    abstract fun provideLogger(androidLogger: AndroidLogger): Logger
}