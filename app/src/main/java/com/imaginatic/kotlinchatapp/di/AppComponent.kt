package com.imaginatic.kotlinchatapp.di

import com.imaginatic.kotlinchatapp.addchat.AddChatActivity
import com.imaginatic.kotlinchatapp.chat.ChatActivity
import com.imaginatic.kotlinchatapp.dashboard.DashboardActivity
import com.imaginatic.kotlinchatapp.login.LoginActivity
import com.imaginatic.kotlinchatapp.register.RegisterActivity
import com.imaginatic.kotlinchatapp.startscreen.MainActivity
import com.kotlinchatapp.core.di.CoreComponent
import com.kotlinchatapp.core.domain.usecase.ChatUseCase
import dagger.Component

@AppScope
@Component(modules = [ViewModelModule::class], dependencies = [CoreComponent::class])
interface AppComponent {
    @Component.Builder
    interface Builder {
        fun coreComponent(coreComponent: CoreComponent): Builder
        fun build(): AppComponent
    }

    fun provideChatUseCase(): ChatUseCase

    fun inject(activity: MainActivity)
    fun inject(activity: LoginActivity)
    fun inject(activity: RegisterActivity)
    fun inject(activity: DashboardActivity)
    fun inject(activity: AddChatActivity)
    fun inject(activity: ChatActivity)
}