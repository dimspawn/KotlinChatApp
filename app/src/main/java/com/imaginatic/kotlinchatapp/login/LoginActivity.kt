package com.imaginatic.kotlinchatapp.login

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.imaginatic.kotlinchatapp.MyApplication
import com.imaginatic.kotlinchatapp.R
import com.imaginatic.kotlinchatapp.dashboard.DashboardActivity
import com.imaginatic.kotlinchatapp.databinding.ActivityLoginBinding
import com.imaginatic.kotlinchatapp.register.RegisterActivity
import com.kotlinchatapp.core.data.Resource
import com.kotlinchatapp.core.di.ViewModelFactory
import com.kotlinchatapp.core.domain.model.UserLoginData
import com.kotlinchatapp.core.utils.Constants
import javax.inject.Inject

class LoginActivity : AppCompatActivity() {

    @Inject
    lateinit var factory: ViewModelFactory
    private val loginViewModel: LoginViewModel by viewModels { factory }
    private lateinit var binding: ActivityLoginBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        (application as MyApplication).appComponent.inject(this)
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        loginViewModel.loginChat.observe(this) { userData ->
            userData.let {  userDataRes ->
                when (userDataRes) {
                    is Resource.Loading -> {
                        binding.loginBtn.text = resources.getString(R.string.please_wait)
                        binding.loginBtn.isEnabled = false
                    }
                    is Resource.Success -> {
                        userDataRes.data?.let {
                            if (it.isNotEmpty()) {
                                val intent = Intent(this, DashboardActivity::class.java)
                                intent.putExtra(Constants.USER_DATA, it[0])
                                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK)
                                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                                startActivity(intent)
                            }
                        }
                    }
                    is Resource.Error -> {
                        binding.loginBtn.text = resources.getString(R.string.login)
                        binding.loginBtn.isEnabled = true
                        Toast.makeText(this, userData.message, Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }

        binding.loginBtn.setOnClickListener {
            val email = binding.tieEmailLoginText.text.toString()
            val password = binding.tiePasswordLoginText.text.toString()

            if (email.isEmpty() || password.isEmpty()) {
                Toast.makeText(this, "Please Insert Email/Password", Toast.LENGTH_SHORT).show()
            } else {
                val userLoginData = UserLoginData(email, password)
                loginViewModel.setLoginChat(userLoginData)
            }
        }

        binding.donTHave.setOnClickListener {
            val intent = Intent(this, RegisterActivity::class.java)
            startActivity(intent)
        }
    }
}