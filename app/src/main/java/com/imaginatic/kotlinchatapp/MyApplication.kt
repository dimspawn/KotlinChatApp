package com.imaginatic.kotlinchatapp

import android.app.Application
import com.imaginatic.kotlinchatapp.di.AppComponent
import com.imaginatic.kotlinchatapp.di.DaggerAppComponent
import com.kotlinchatapp.core.di.DaggerCoreComponent

class MyApplication: Application() {
    private val coreComponent by lazy {
        DaggerCoreComponent.builder().context(this).build()
    }

    val appComponent: AppComponent by lazy {
        DaggerAppComponent.builder().coreComponent(coreComponent).build()
    }
}