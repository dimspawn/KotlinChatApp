package com.imaginatic.kotlinchatapp.login

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.switchMap
import com.kotlinchatapp.core.domain.model.UserLoginData
import com.kotlinchatapp.core.domain.usecase.ChatUseCase
import javax.inject.Inject

class LoginViewModel @Inject constructor(private val chatUseCase: ChatUseCase): ViewModel(){
    private val userLogin = MutableLiveData<UserLoginData>()

    val loginChat = userLogin.switchMap {
        chatUseCase.setLoginChat(it).asLiveData()
    }

    fun setLoginChat(userLogin: UserLoginData) {
        this.userLogin.value = userLogin
    }
}