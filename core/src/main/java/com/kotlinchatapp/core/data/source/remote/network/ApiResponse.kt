package com.kotlinchatapp.core.data.source.remote.network

sealed class ApiResponse<out T> {
    data class Success<out T>(val data: T): ApiResponse<T>()
    data class Error(val errorMessage: String): ApiResponse<Nothing>()
    data object Empty: ApiResponse<Nothing>()
}