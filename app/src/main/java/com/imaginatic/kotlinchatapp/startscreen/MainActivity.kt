package com.imaginatic.kotlinchatapp.startscreen

import android.content.Intent
import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import com.imaginatic.kotlinchatapp.MyApplication
import com.imaginatic.kotlinchatapp.dashboard.DashboardActivity
import com.imaginatic.kotlinchatapp.databinding.ActivityMainBinding
import com.imaginatic.kotlinchatapp.login.LoginActivity
import com.kotlinchatapp.core.di.ViewModelFactory
import com.kotlinchatapp.core.domain.model.UserData
import com.kotlinchatapp.core.utils.Constants
import javax.inject.Inject

class MainActivity : AppCompatActivity() {
    @Inject
    lateinit var factory: ViewModelFactory
    //private val startScreenViewModel: StartScreenViewModel by viewModels { factory }
    private val mainViewModel: MainViewModel by viewModels { factory }
    private lateinit var binding: ActivityMainBinding

    private val loginObserver: Observer<List<UserData>> = Observer { users ->
        removeObserver()
        if(users.isEmpty()) {
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
            finish()
        } else {
            val intent = Intent(this, DashboardActivity::class.java)
            intent.putExtra(Constants.USER_DATA, users[0])
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK)
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            startActivity(intent)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        (application as MyApplication).appComponent.inject(this)
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.ivLogo.alpha = 0f
        binding.ivLogo.animate().setDuration(1500).alpha(1f).withEndAction {
            mainViewModel.checkLogin.observe(this, loginObserver)
        }
    }

    private fun removeObserver() {
        mainViewModel.checkLogin.removeObserver(loginObserver)
    }
}
//class MainActivity : AppCompatActivity() {
//    @Inject
//    lateinit var factory: ViewModelFactory
//    private val mainActivityModel: MainViewModel
//    private lateinit var binding: ActivityMainBinding
//    //private lateInit var socket: Socket
//    override fun onCreate(savedInstanceState: Bundle?) {
//        super.onCreate(savedInstanceState)
//        binding = ActivityMainBinding.inflate(layoutInflater)
//        setContentView(binding.root)
//
////        SocketHandler.setSocket()
////        SocketHandler.establishConnection()
////        socket = SocketHandler.getSocket()
////        socket.on("my-converse-id", onNewMessage)
//
////        binding.messageSend.setOnClickListener {
////            val data = JSONObject()
////            data.put("converseId", "my-converse-id")
////            data.put("username", "Dimas")
////            data.put("message", "Hai Dimas")
////            socket.emit("message", data)
////        }
//    }
//
////    private val onNewMessage = Emitter.Listener { args ->
////        runOnUiThread {
////            val data = args[0] as JSONObject
////            val username = data.getString("username")
////            val message = data.getString("message")
////            Toast.makeText(this, "$username is $message", Toast.LENGTH_SHORT).show()
////        }
////    }
//
////    override fun onDestroy() {
////        super.onDestroy()
////        SocketHandler.closeConnection()
////    }
//}