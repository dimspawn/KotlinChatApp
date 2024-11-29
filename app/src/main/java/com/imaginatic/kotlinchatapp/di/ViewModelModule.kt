package com.imaginatic.kotlinchatapp.di

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.imaginatic.kotlinchatapp.addchat.AddChatViewModel
import com.imaginatic.kotlinchatapp.chat.ChatViewModel
import com.imaginatic.kotlinchatapp.dashboard.DashboardViewModel
import com.imaginatic.kotlinchatapp.login.LoginViewModel
import com.imaginatic.kotlinchatapp.register.RegisterViewModel
import com.imaginatic.kotlinchatapp.startscreen.MainViewModel
import com.kotlinchatapp.core.di.ViewModelFactory
import dagger.Binds
import dagger.Module
import dagger.multibindings.IntoMap

@Suppress("unused")
@Module
abstract class ViewModelModule {
    @Binds
    @IntoMap
    @ViewModelKey(MainViewModel::class)
    abstract fun bindStartScreenViewModel(viewModel: MainViewModel): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(RegisterViewModel::class)
    abstract fun bindRegisterViewModel(viewModel: RegisterViewModel): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(DashboardViewModel::class)
    abstract fun bindLatestMessagesViewModel(viewModel: DashboardViewModel): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(LoginViewModel::class)
    abstract fun bindLoginViewModel(viewModel: LoginViewModel): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(AddChatViewModel::class)
    abstract fun bindNewMessageViewModel(viewModel: AddChatViewModel): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(ChatViewModel::class)
    abstract fun bindChatViewModel(viewModel: ChatViewModel): ViewModel

    @Binds
    abstract fun bindViewModelFactory(factory: ViewModelFactory): ViewModelProvider.Factory
}