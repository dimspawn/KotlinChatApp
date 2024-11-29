package com.imaginatic.kotlinchatapp.register

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.bumptech.glide.Glide
import com.imaginatic.kotlinchatapp.MyApplication
import com.imaginatic.kotlinchatapp.R
import com.imaginatic.kotlinchatapp.dashboard.DashboardActivity
import com.imaginatic.kotlinchatapp.databinding.ActivityRegisterBinding
import com.kotlinchatapp.core.data.Resource
import com.kotlinchatapp.core.di.ViewModelFactory
import com.kotlinchatapp.core.domain.model.UserRegisterData
import com.kotlinchatapp.core.utils.Constants
import javax.inject.Inject

class RegisterActivity : AppCompatActivity() {

    @Inject
    lateinit var factory: ViewModelFactory
    private val registerViewModel: RegisterViewModel by viewModels { factory }
    private lateinit var binding: ActivityRegisterBinding
    private var photoUri: Uri? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        (application as MyApplication).appComponent.inject(this)
        super.onCreate(savedInstanceState)
        binding = ActivityRegisterBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(binding.toolbarRegister)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        val launchPicker = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == RESULT_OK && result.data != null) {
                binding.addImageText.visibility = View.INVISIBLE
                Glide.with(this)
                    .load(result.data?.data)
                    .into(binding.selectedPhotoRegister)
                photoUri = result.data?.data
            } else {
                binding.addImageText.visibility = View.VISIBLE
                Glide.with(this)
                    .load(R.drawable.placeholder)
                    .into(binding.selectedPhotoRegister)
                photoUri = null
            }
        }

        registerViewModel.registerChat.observe(this) { userData ->
            userData.let {  userDataRes ->
                when(userDataRes) {
                    is Resource.Loading -> {
                        binding.registerBtn.text = resources.getString(R.string.please_wait)
                        binding.registerBtn.isEnabled = false
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
                        binding.registerBtn.text = resources.getString(R.string.register)
                        binding.registerBtn.isEnabled = true
                        Toast.makeText(this, userData.message, Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }

        binding.registerBtn.setOnClickListener {
            val username = binding.etUsernameText.text.toString()
            val email = binding.etEmailText.text.toString()
            val password = binding.etPasswordText.text.toString()

            if (email.isEmpty() || password.isEmpty() || username.isEmpty())  {
                Toast.makeText(this, "Please enter text in email/pw/username", Toast.LENGTH_SHORT).show()
            } else {
                val userRegisterData = UserRegisterData(username, email, password, photoUri)
                registerViewModel.setRegisterChat(userRegisterData)
            }
        }

        binding.selectedPhotoRegister.setOnClickListener {
            val intent = Intent(Intent.ACTION_PICK)
            intent.type = "image/*"
            launchPicker.launch(intent)
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressedDispatcher.onBackPressed()
        return super.onSupportNavigateUp()
    }
}