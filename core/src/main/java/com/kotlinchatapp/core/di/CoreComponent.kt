package com.kotlinchatapp.core.di

import android.content.Context
import com.kotlinchatapp.core.domain.repository.IChatRepository
import com.kotlinchatapp.core.domain.usecase.ChatUseCase
import com.kotlinchatapp.core.utils.Logger
import dagger.BindsInstance
import dagger.Component

@CoreScope
@Component(modules = [RepositoryModule::class])
interface CoreComponent {
    @Component.Builder
    interface Builder {
        fun context(@BindsInstance context: Context): Builder
        fun build(): CoreComponent
    }

    fun provideChatUseCase(): ChatUseCase
    fun provideRepository(): IChatRepository
    fun provideLogger(): Logger
}