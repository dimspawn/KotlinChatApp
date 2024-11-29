package com.imaginatic.kotlinchatapp.startscreen

import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import com.kotlinchatapp.core.domain.usecase.ChatUseCase
import javax.inject.Inject

class MainViewModel @Inject constructor(chatUseCase: ChatUseCase): ViewModel() {
    val checkLogin = chatUseCase.getCheckLogin().asLiveData()
}