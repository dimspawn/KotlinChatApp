package com.imaginatic.kotlinchatapp.addchat

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import com.imaginatic.kotlinchatapp.MyApplication
import com.imaginatic.kotlinchatapp.R
import com.imaginatic.kotlinchatapp.chat.ChatActivity
import com.imaginatic.kotlinchatapp.databinding.ActivityAddChatBinding
import com.kotlinchatapp.core.data.Resource
import com.kotlinchatapp.core.di.ViewModelFactory
import com.kotlinchatapp.core.domain.model.ParticipantData
import com.kotlinchatapp.core.domain.model.UserData
import com.kotlinchatapp.core.domain.model.input.InputUserIdsData
import com.kotlinchatapp.core.ui.UserAdapter
import com.kotlinchatapp.core.ui.UserAdapterClickListener
import com.kotlinchatapp.core.utils.Constants
import javax.inject.Inject

class AddChatActivity : AppCompatActivity(), UserAdapterClickListener {
    @Inject
    lateinit var factory: ViewModelFactory
    private val addChatViewModel: AddChatViewModel by viewModels{ factory }
    private lateinit var binding: ActivityAddChatBinding
    private lateinit var userAdapter: UserAdapter

    private var userData: UserData? = null
    private var opponentData: UserData? = null

    private val usersObserver: Observer<Resource<List<UserData>>> = Observer { users ->
        users.let { userRes ->
            when(userRes) {
                is Resource.Loading -> {
                    binding.pbNewMessage.visibility = View.VISIBLE
                    binding.noUserFound.visibility = View.GONE
                }
                is Resource.Success -> {
                    binding.pbNewMessage.visibility = View.INVISIBLE
                    userRes.data?.let {
                        if (it.isNotEmpty()) {
                            userAdapter.setData(it)
                        } else {
                            binding.noUserFound.text = resources.getText(R.string.no_user_found)
                            binding.noUserFound.visibility = View.VISIBLE
                        }
                    }
                }
                is Resource.Error -> {
                    binding.pbNewMessage.visibility = View.INVISIBLE
                    binding.noUserFound.text = userRes.message.toString()
                    binding.noUserFound.visibility = View.VISIBLE
                }
            }
        }
    }

    private val personalChatObserver: Observer<Resource<List<ParticipantData>>> = Observer { parties ->
        parties.let { partiesRes ->
            when(partiesRes) {
                is Resource.Loading -> {
                    binding.pbNewMessage.visibility = View.VISIBLE
                    binding.rvNewMessage.visibility = View.INVISIBLE
                }
                is Resource.Success -> {

                    binding.pbNewMessage.visibility = View.GONE
                    binding.rvNewMessage.visibility = View.VISIBLE
                    partiesRes.data?.let {
                        if (it.isNotEmpty()) {
                            val intent = Intent(this, ChatActivity::class.java)
                            intent.putExtra(Constants.USER_DATA, userData)
                            intent.putExtra(Constants.OPPONENT_USERNAME, opponentData?.username)
                            intent.putExtra(Constants.CONVERSATION_ID, it[0].conversationId)
                            startActivity(intent)
                        }
                    }
                }
                is Resource.Error -> {
                    binding.pbNewMessage.visibility = View.GONE
                    binding.rvNewMessage.visibility = View.VISIBLE
                    Toast.makeText(this, partiesRes.message.toString(), Toast.LENGTH_SHORT).show()
                }
            }
        }
    }



    override fun onCreate(savedInstanceState: Bundle?) {
        (application as MyApplication).appComponent.inject(this)
        super.onCreate(savedInstanceState)
        binding = ActivityAddChatBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(binding.toolbarNew)
        supportActionBar?.title = "Select User"
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        userAdapter = UserAdapter(this)

        addChatViewModel.users.observe(this, usersObserver)

        with(binding.rvNewMessage) {
            layoutManager = LinearLayoutManager(context)
            setHasFixedSize(true)
            adapter = userAdapter
        }

        @Suppress("DEPRECATION")
        userData = intent?.getParcelableExtra(Constants.USER_DATA) as? UserData
        userData?.let {
            addChatViewModel.setUserList(it.id)
        }

        addChatViewModel.personalChat.observe(this, personalChatObserver)
    }

    override fun onUserClickListener(userData: UserData) {
        opponentData = userData
        this.userData?.let {
            val inputUserIdsData = InputUserIdsData(
                userId = it.id,
                opponentId = userData.id
            )
            addChatViewModel.setUserIds(inputUserIdsData)
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressedDispatcher.onBackPressed()
        return super.onSupportNavigateUp()
    }
}