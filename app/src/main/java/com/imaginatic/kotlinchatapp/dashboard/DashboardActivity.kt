package com.imaginatic.kotlinchatapp.dashboard

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.gson.Gson
import com.google.gson.JsonObject
import com.imaginatic.kotlinchatapp.MyApplication
import com.imaginatic.kotlinchatapp.R
import com.imaginatic.kotlinchatapp.addchat.AddChatActivity
import com.imaginatic.kotlinchatapp.chat.ChatActivity
import com.imaginatic.kotlinchatapp.databinding.ActivityDashboardBinding
import com.imaginatic.kotlinchatapp.info.InfoActivity
import com.imaginatic.kotlinchatapp.login.LoginActivity
import com.kotlinchatapp.core.data.Resource
import com.kotlinchatapp.core.data.source.remote.response.MessageResponse
import com.kotlinchatapp.core.di.ViewModelFactory
import com.kotlinchatapp.core.domain.model.ConversationConstraintData
import com.kotlinchatapp.core.domain.model.UserData
import com.kotlinchatapp.core.domain.model.input.InputUserIdMessageResponse
import com.kotlinchatapp.core.ui.DashboardAdapter
import com.kotlinchatapp.core.ui.DashboardAdapterClickListener
import com.kotlinchatapp.core.utils.Constants
import com.kotlinchatapp.core.utils.SocketHandler
import io.socket.client.Socket
import io.socket.emitter.Emitter
import javax.inject.Inject

class DashboardActivity : AppCompatActivity(), DashboardAdapterClickListener {

    @Inject
    lateinit var factory: ViewModelFactory
    @Inject
    lateinit var socketHandler: SocketHandler
    private val dashboardViewModel: DashboardViewModel by viewModels { factory }
    private lateinit var binding: ActivityDashboardBinding
    private var socket: Socket? = null
    private var userData: UserData? = null
    private lateinit var dashboardAdapter: DashboardAdapter
    private lateinit var conIds: MutableList<String>

    private val logOutObserver: Observer<Resource<Boolean>> = Observer{ logOut ->
        logOut.let { logOutRes ->
            when(logOutRes) {
                is Resource.Loading -> {}
                is Resource.Success -> {
                    logOutRes.data?.let {
                        removeLogOutObserver()
                        if (!it) {
                            val intent = Intent(this, LoginActivity::class.java)
                            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK)
                            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                            startActivity(intent)
                            finish()
                        }
                    }
                }
                is Resource.Error -> {}
            }
        }
    }

    private val conversationConstraintObserver: Observer<Resource<List<ConversationConstraintData>>> = Observer { convCons ->
        convCons.let { convConsRes ->
            when(convConsRes) {
                is Resource.Loading -> {
                    binding.pbDashboardMessage.visibility = View.VISIBLE
                }
                is Resource.Success -> {
                    binding.pbDashboardMessage.visibility = View.INVISIBLE
                    convConsRes.data?.let {
                        if (it.isNotEmpty()) {
                            dashboardAdapter.insertAll(it)
                            for(ccData in it) {
                                ccData.conversationId?.let { cId ->
                                    if (!conIds.contains(cId)) {
                                        conIds.add(cId)
                                        socket?.on(cId, onMessage)
                                    }
                                }
                            }
                        }
                    }
                }
                is Resource.Error -> {
                    binding.pbDashboardMessage.visibility = View.INVISIBLE
                    Toast.makeText(this, convConsRes.message, Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private val constraintSocketObserver: Observer<Resource<List<ConversationConstraintData>>> = Observer { convConsSocket ->
        convConsSocket.let { conSocketRes ->
            when(conSocketRes) {
                is Resource.Loading -> {}
                is Resource.Success -> {
                    conSocketRes.data?.let {
                        if (it.isNotEmpty()) {
                            dashboardAdapter.insertAll(it)
                        }
                    }
                }
                is Resource.Error -> {
                    Toast.makeText(this, conSocketRes.message, Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private val onMessage = Emitter.Listener { args ->
        runOnUiThread {
            val gson = Gson()
            val data = args[0] as JsonObject
            val message = gson.fromJson(data, MessageResponse::class.java)

            userData?.let {
                val messageWithUserId = InputUserIdMessageResponse(
                    userId = it.id,
                    messageResponse = message
                )
                dashboardViewModel.setMessageData(messageWithUserId)
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        (application as MyApplication).appComponent.inject(this)
        super.onCreate(savedInstanceState)
        binding = ActivityDashboardBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(binding.toolbarDashboard)

        conIds = mutableListOf()
        socketHandler.setSocket()
        socketHandler.establishConnection()
        socket = socketHandler.getSocket()

        userData = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            intent?.getParcelableExtra(Constants.USER_DATA, UserData::class.java)
        } else {
            @Suppress("DEPRECATION")
            intent?.getParcelableExtra(Constants.USER_DATA) as? UserData
        }

        if (userData == null) {
            dashboardViewModel.logOut.observe(this, logOutObserver)
        } else {
            binding.fabAddNewChat.setOnClickListener {
                val intent = Intent(this, AddChatActivity::class.java)
                intent.putExtra(Constants.USER_DATA, userData)
                startActivity(intent)
            }

            userData?.let {
                dashboardAdapter = DashboardAdapter(it.id, this)
                with(binding.rvDashboardMessage) {
                    layoutManager = LinearLayoutManager(context)
                    setHasFixedSize(true)
                    adapter = dashboardAdapter
                }

                dashboardViewModel.conversationsConstraint.observe(this, conversationConstraintObserver)
                dashboardViewModel.conversationSocket.observe(this, constraintSocketObserver)
                dashboardViewModel.getConversationsConstraint(it.id)
            }
        }
    }

    override fun onLastMessageClickListener(conversationConstraintData: ConversationConstraintData) {
        val intent = Intent(this, ChatActivity::class.java)
        intent.putExtra(Constants.USER_DATA, userData)
        intent.putExtra(Constants.OPPONENT_USERNAME, conversationConstraintData.username)
        intent.putExtra(Constants.CONVERSATION_ID, conversationConstraintData.conversationId)
        startActivity(intent)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item.itemId) {
            R.id.menu_info -> {
                val intent = Intent(this, InfoActivity::class.java)
                intent.putExtra(InfoActivity.EXTRA_DATA, userData)
                startActivity(intent)
            }
            R.id.menu_sign_out -> {
                dashboardViewModel.logOut.observe(this, logOutObserver)
            }
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.nav_menu, menu)
        return super.onCreateOptionsMenu(menu)
    }

    private fun removeLogOutObserver() {
        dashboardViewModel.logOut.removeObserver(logOutObserver)
    }

    override fun onDestroy() {
        super.onDestroy()
        socketHandler.closeConnection()
    }
}