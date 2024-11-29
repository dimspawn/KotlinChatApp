package com.kotlinchatapp.core.data.source.remote.network

import android.content.ContentResolver
import android.content.Context
import android.database.Cursor
import android.net.Uri
import android.provider.OpenableColumns
import com.kotlinchatapp.core.data.source.remote.response.ConversationResponse
import com.kotlinchatapp.core.data.source.remote.response.FilterResponse
import com.kotlinchatapp.core.data.source.remote.response.InsertResponse
import com.kotlinchatapp.core.data.source.remote.response.LoginDataResponse
import com.kotlinchatapp.core.data.source.remote.response.MessageResponse
import com.kotlinchatapp.core.data.source.remote.response.ParticipantResponse
import com.kotlinchatapp.core.data.source.remote.response.RegisterDataResponse
import com.kotlinchatapp.core.data.source.remote.response.RemoteFilterResponse
import com.kotlinchatapp.core.data.source.remote.response.UserResponse
import com.kotlinchatapp.core.utils.Constants
import com.kotlinchatapp.core.utils.Logger
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import java.io.File
import java.io.FileOutputStream
import java.io.InputStream
import java.util.Date
import javax.inject.Inject

class NetworkService @Inject constructor(
    private val context: Context,
    private val apiService: ApiService,
    private val androidLogger: Logger
): INetworkService {
    companion object {
        const val TAG = "APIService"
    }

    private fun getFileName(context: Context, uri: Uri): String? {
        var result: String? = null
        if (uri.scheme == "content") {
            val cursor: Cursor? = context.contentResolver.query(uri, null, null, null, null)
            cursor?.use {
                if (cursor.moveToFirst()) {
                    result = cursor.getString(cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME))
                }
            }
        }
        if (result == null) {
            result = uri.path
            val cut = result?.lastIndexOf('/')
            if (cut != -1) {
                result = result?.substring(cut!! + 1)
            }
        }
        return result
    }

    private fun uriToFile(uri: Uri, context: Context): File {
        val fName = getFileName(context, uri) ?: "default.jpg"
        val contentResolver: ContentResolver = context.contentResolver
        val inputStream: InputStream? = contentResolver.openInputStream(uri)
        val file = File(context.cacheDir, "tempFile_" + System.currentTimeMillis() + fName)

        val outputStream = FileOutputStream(file)
        inputStream?.copyTo(outputStream)
        outputStream.close()
        inputStream?.close()

        return file
    }

    private fun createMultipartBodyPart(context: Context, uri: Uri): MultipartBody.Part {
        val contentResolver: ContentResolver = context.contentResolver
        val file = uriToFile(uri, context)
        val mimeType = contentResolver.getType(uri)
        val requestBody = file.asRequestBody(mimeType?.toMediaTypeOrNull())
        return MultipartBody.Part.createFormData("image", file.name, requestBody)
    }

    private fun responding(response: InsertResponse): String {
        if (response.success == true) {
            if (response.id != null) {
                return response.id
            } else {
                throw Exception("Upsert Success but id not found")
            }
        } else {
            if (response.message != null) {
                throw Exception(response.message)
            } else {
                throw Exception("Something wrong with upsert")
            }
        }
    }

    override suspend fun userInsertOrUpdate(data: UserResponse): String {
        val response = apiService.insertOrUpdateUser(data)
        return responding(response)
    }

    override suspend fun conversationInsertOrUpdate(data: ConversationResponse): String {
        val response = apiService.insertOrUpdateConversation(data)
        return responding(response)
    }

    override suspend fun participantInsertOrUpdate(data: ParticipantResponse): String {
        val response = apiService.insertOrUpdateParticipant(data)
        return responding(response)
    }

    override suspend fun messageInsertOrUpdate(data: MessageResponse): String {
        val response = apiService.insertOrUpdateMessage(data)
        return responding(response)
    }

    override suspend fun updateParticipants(conId: String, mId: String) {
        val participantResponse = ParticipantResponse(
            conversationId = conId,
            latestMessageId = mId,
            updatedAt = Date().time
        )
        apiService.updateParticipants(participantResponse)
    }

    override suspend fun getUserListData(remoteFilters: RemoteFilterResponse): List<UserResponse> {
        val results = mutableListOf<UserResponse>()
        val userResults = apiService.getUserListData(remoteFilters)
        if (userResults.success == false) {
            throw Exception(userResults.message)
        }
        userResults.data?.let {
            results.addAll(it)
        }
        return results
    }

    override suspend fun getParticipantListData(remoteFilters: RemoteFilterResponse): List<ParticipantResponse> {
        val results = mutableListOf<ParticipantResponse>()
        val participantResults = apiService.getParticipantListData(remoteFilters)
        if (participantResults.success == false) {
            throw Exception(participantResults.message)
        }
        participantResults.data?.let {
            results.addAll(it)
        }
        return results
    }

    override suspend fun getConversationListData(remoteFilters: RemoteFilterResponse): List<ConversationResponse> {
        val results = mutableListOf<ConversationResponse>()
        val conversationResults = apiService.getConversationListData(remoteFilters)
        if (conversationResults.success == false) {
            throw Exception(conversationResults.message)
        }
        conversationResults.data?.let {
            results.addAll(it)
        }
        return results
    }

    override suspend fun getMessageListData(remoteFilters: RemoteFilterResponse): List<MessageResponse> {
        val results = mutableListOf<MessageResponse>()
        val messageResults = apiService.getMessageListData(remoteFilters)
        if (messageResults.success == false) {
            throw Exception(messageResults.message)
        }
        messageResults.data?.let {
            results.addAll(it)
        }
        return results
    }

    override suspend fun registerUser(username: String, email: String, password: String, photoUri: Uri?): List<UserResponse> {
        val registerDataResponse = RegisterDataResponse(
            username = username,
            email = email,
            password = password
        )
        val result = apiService.setRegisterUser(registerDataResponse)
        if (result.success == true) {
            val userRegId = result.id ?: ""
            if (userRegId != "") {
                val user = UserResponse(
                    id = userRegId,
                    username = username,
                    email = email
                )
                //insert image
                photoUri?.let {
                    val image = createMultipartBodyPart(context, it)
                    val imgResult = apiService.setUploadImage(image)
                    if (imgResult.success == true) {
                        imgResult.file?.let { fileName ->
                            user.photoProfileUrl = fileName
                            userInsertOrUpdate(data = user)
                        }
                    } else {
                        imgResult.message?.let { err ->
                            androidLogger.d(TAG, err)
                        } ?: run {
                            androidLogger.d(TAG, "Something Wrong")
                        }
                    }
                }

                return listOf(user)
                //return user list
            } else {
                throw Exception("RegId is Gone :(")
            }
        } else {
            result.message?.let {
                throw (Exception(it))
            }
            throw Exception("Something wrong!")
        }
    }

    override suspend fun loginUser(email: String, password: String): List<UserResponse> {
        val loginData = LoginDataResponse(email, password)
        val results = apiService.setLoginUser(loginData)
        if (results.success == true) {
            val users = mutableListOf<UserResponse>()
            val filter = FilterResponse(
                key = Constants.EMAIL,
                value = email
            )
            val remoteFilter = RemoteFilterResponse(
                filters = listOf(filter)
            )
            val resultUsers = apiService.getUserListData(remoteFilter)
            if (resultUsers.success == true) {
                resultUsers.data?.let {
                    if (it.isNotEmpty()) {
                        users.addAll(it)
                    }
                }
            } else {
                resultUsers.message?.let {
                    throw Exception(it)
                }
                throw Exception("Failed To Login")
            }
            return users
        } else {
            throw Exception(results.message)
        }
    }
}
