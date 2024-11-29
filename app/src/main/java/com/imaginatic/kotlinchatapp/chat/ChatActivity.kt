package com.imaginatic.kotlinchatapp.chat

import android.os.Build
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.gson.Gson
import com.google.gson.JsonObject
import com.imaginatic.kotlinchatapp.MyApplication
import com.imaginatic.kotlinchatapp.databinding.ActivityChatBinding
import com.kotlinchatapp.core.data.Resource
import com.kotlinchatapp.core.data.source.remote.response.MessageResponse
import com.kotlinchatapp.core.di.ViewModelFactory
import com.kotlinchatapp.core.domain.model.MessageConstraintData
import com.kotlinchatapp.core.domain.model.MessageData
import com.kotlinchatapp.core.domain.model.UserData
import com.kotlinchatapp.core.domain.model.input.InputMessageData
import com.kotlinchatapp.core.domain.model.input.InputMessagesData
import com.kotlinchatapp.core.ui.ChatAdapter
import com.kotlinchatapp.core.utils.Constants
import com.kotlinchatapp.core.utils.DataMapper
import com.kotlinchatapp.core.utils.SocketHandler
import io.socket.client.Socket
import io.socket.emitter.Emitter
import javax.inject.Inject

class ChatActivity : AppCompatActivity() {
    @Inject
    lateinit var factory: ViewModelFactory
    @Inject
    lateinit var socketHandler: SocketHandler
    private val chatViewModel: ChatViewModel by viewModels { factory }
    private lateinit var binding: ActivityChatBinding
    private var socket: Socket? = null
    private lateinit var userChat: ChatAdapter
    private var ownData: UserData? = null
    private var opponentUsername: String? = null
    private var conversationId: String? = null
    private var socketOnce: Boolean = true

    private val chatsObserver: Observer<Resource<List<MessageConstraintData>>> = Observer { constraintMessages ->
        constraintMessages.let { conMessageRes ->
            when(conMessageRes) {
                is Resource.Loading -> {}
                is Resource.Success -> {
                    conMessageRes.data?.let {
                        if (it.isNotEmpty()){
                            userChat.insertAll(it)
                        }
                        if (socketOnce) {
                            conversationId?.let { cId ->
                                socket?.on(cId, onMessage)
                                socketOnce = false
                            }
                        }
                    }
                }
                is Resource.Error -> {
                    Toast.makeText(this, conMessageRes.message.toString(), Toast.LENGTH_SHORT).show()
                }
            }
        }
    }
    private val sendMessageObserver: Observer<Resource<MessageData?>> = Observer { mData ->
        mData.let { mDataRes ->
            when(mDataRes) {
                is Resource.Loading -> {}
                is Resource.Success -> {
                    binding.etText.text.clear()
                    setAfterObserving()
                    mDataRes.data?.let {
                        //emit the socket
                        val gson = Gson()
                        val messageResponse = DataMapper.mapSafetyMessageDomainToResponse(it)
                        Toast.makeText(this, "message = ${mDataRes.data?.message}", Toast.LENGTH_SHORT).show()
                        val response = gson.toJson(messageResponse)
                        socket?.emit("message", response)
                    }
                }
                is Resource.Error -> {
                    setAfterObserving()
                    Toast.makeText(this, mDataRes.message.toString(), Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private val messageSocketObserver: Observer<Resource<List<MessageConstraintData>>> = Observer { msgSocket ->
        msgSocket.let { msgSocketRes ->
            when(msgSocketRes) {
                is Resource.Loading -> {}
                is Resource.Success -> {
                    msgSocketRes.data?.let {
                        if (it.isNotEmpty()) {
                            userChat.insertAll(it)
                        }
                    }
                }
                is Resource.Error -> {
                    Toast.makeText(this, msgSocketRes.message.toString(), Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private val onMessage = Emitter.Listener { args ->
        runOnUiThread {
            val gson = Gson()
            val data = args[0] as JsonObject
            val message = gson.fromJson(data, MessageResponse::class.java)

            chatViewModel.setMessageData(message)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        (application as MyApplication).appComponent.inject(this)
        super.onCreate(savedInstanceState)
        binding = ActivityChatBinding.inflate(layoutInflater)
        setContentView(binding.root)

        socketHandler.setSocket()
        socketHandler.establishConnection()
        socket = socketHandler.getSocket()
        setSupportActionBar(binding.toolbarChat)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        ownData = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            intent?.getParcelableExtra(Constants.USER_DATA, UserData::class.java)
        } else {
            @Suppress("DEPRECATION")
            intent?.getParcelableExtra(Constants.USER_DATA) as? UserData
        }

        opponentUsername = intent?.getStringExtra(Constants.OPPONENT_USERNAME)
        conversationId = intent?.getStringExtra(Constants.CONVERSATION_ID)

        opponentUsername?.let { oUsername ->
            supportActionBar?.title = oUsername
        }

        if (ownData != null && conversationId != null) {
            val data = ownData as UserData
            val conId = conversationId as String
            chatViewModel.chat.observe(this, sendMessageObserver)
            chatViewModel.messageSocket.observe(this, messageSocketObserver)

            binding.etText.addTextChangedListener(object : TextWatcher {
                override fun afterTextChanged(s: Editable?) {}
                override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                    binding.sendBtn.isEnabled = s.toString().trim().isNotEmpty()
                }
            })

            binding.sendBtn.setOnClickListener {
                val msg = binding.etText.text.toString()
                conversationId?.let { conId ->
                    val inputMessageData = InputMessageData(
                        conversationId = conId,
                        userId = ownData?.id ?: "chatMessageIdError",
                        message = msg,
                        type = Constants.MESSAGE
                    )
                    chatViewModel.setMessage(inputMessageData)
                    setAfterSending()
                }
            }

            userChat = ChatAdapter(data.id)
            chatViewModel.chats.observe(this, chatsObserver)

            with(binding.rvChat) {
                layoutManager = LinearLayoutManager(context)
                setHasFixedSize(true)
                adapter = userChat
            }

            val inputMessagesData = InputMessagesData(
                userId = data.id,
                conversationId = conId
            )
            chatViewModel.getAllMessages(inputMessagesData)
        } else {
            Toast.makeText(this, "FATAL ERROR!! NO USER FOUND", Toast.LENGTH_SHORT).show()
        }
    }

    private fun setAfterSending(){
        binding.sendBtn.isEnabled = false
        binding.etText.isEnabled = false
    }

    private fun setAfterObserving() {
        binding.etText.isEnabled = true
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressedDispatcher.onBackPressed()
        return super.onSupportNavigateUp()
    }
}