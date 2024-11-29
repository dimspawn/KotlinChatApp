package com.imaginatic.kotlinchatapp.register

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.switchMap
import com.kotlinchatapp.core.domain.model.UserRegisterData
import com.kotlinchatapp.core.domain.usecase.ChatUseCase
import javax.inject.Inject

class RegisterViewModel @Inject constructor(private val chatUseCase: ChatUseCase): ViewModel() {
    private val userRegister = MutableLiveData<UserRegisterData>()

    val registerChat = userRegister.switchMap { userRegisterData ->
        chatUseCase.setRegisterChat(userRegisterData.username, userRegisterData.email, userRegisterData.password, userRegisterData.photoUri).asLiveData()
    }

    fun setRegisterChat(userRegister: UserRegisterData) {
        this.userRegister.value = userRegister
    }
}