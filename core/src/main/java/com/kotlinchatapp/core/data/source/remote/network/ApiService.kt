package com.kotlinchatapp.core.data.source.remote.network

import com.kotlinchatapp.core.data.source.remote.response.ConversationResponse
import com.kotlinchatapp.core.data.source.remote.response.ConversationsResponse
import com.kotlinchatapp.core.data.source.remote.response.InsertResponse
import com.kotlinchatapp.core.data.source.remote.response.LoginDataResponse
import com.kotlinchatapp.core.data.source.remote.response.LoginResponse
import com.kotlinchatapp.core.data.source.remote.response.MessageResponse
import com.kotlinchatapp.core.data.source.remote.response.MessagesResponse
import com.kotlinchatapp.core.data.source.remote.response.ParticipantResponse
import com.kotlinchatapp.core.data.source.remote.response.ParticipantsResponse
import com.kotlinchatapp.core.data.source.remote.response.RegisterDataResponse
import com.kotlinchatapp.core.data.source.remote.response.RegisterResponse
import com.kotlinchatapp.core.data.source.remote.response.RemoteFilterResponse
import com.kotlinchatapp.core.data.source.remote.response.UploadResponse
import com.kotlinchatapp.core.data.source.remote.response.UserResponse
import com.kotlinchatapp.core.data.source.remote.response.UsersResponse
import okhttp3.MultipartBody
import retrofit2.http.Body
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part

interface ApiService {
    @Multipart
    @POST("/upload")
    suspend fun setUploadImage(@Part image: MultipartBody.Part): UploadResponse

    @POST("/register")
    suspend fun setRegisterUser(@Body registerData: RegisterDataResponse): RegisterResponse

    @POST("/login")
    suspend fun setLoginUser(@Body loginData: LoginDataResponse): LoginResponse

    @POST("/users/filter")
    suspend fun getUserListData(@Body remoteFilters: RemoteFilterResponse): UsersResponse

    @POST("/participants/filter")
    suspend fun getParticipantListData(@Body remoteFilters: RemoteFilterResponse): ParticipantsResponse

    @POST("/conversations/filter")
    suspend fun getConversationListData(@Body remoteFilters: RemoteFilterResponse): ConversationsResponse

    @POST("/messages/filter")
    suspend fun getMessageListData(@Body remoteFilters: RemoteFilterResponse): MessagesResponse

    @POST("/users/upsert")
    suspend fun insertOrUpdateUser(@Body data: UserResponse): InsertResponse

    @POST("/participants/upsert")
    suspend fun insertOrUpdateParticipant(@Body data: ParticipantResponse): InsertResponse

    @POST("/conversations/upsert")
    suspend fun insertOrUpdateConversation(@Body data: ConversationResponse): InsertResponse

    @POST("/messages/upsert")
    suspend fun insertOrUpdateMessage(@Body data: MessageResponse): InsertResponse

    @POST("/participants/update")
    suspend fun updateParticipants(@Body data: ParticipantResponse): InsertResponse
}