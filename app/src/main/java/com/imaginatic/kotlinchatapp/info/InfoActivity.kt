package com.imaginatic.kotlinchatapp.info

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.imaginatic.kotlinchatapp.databinding.ActivityInfoBinding
import com.kotlinchatapp.core.R
import com.kotlinchatapp.core.domain.model.UserData

class InfoActivity : AppCompatActivity() {

    private lateinit var binding: ActivityInfoBinding

    companion object {
        const val EXTRA_DATA = "extra_data_info"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityInfoBinding.inflate(layoutInflater)
        setContentView(binding.root)

        @Suppress("DEPRECATION") val userData = intent?.getParcelableExtra<UserData>(EXTRA_DATA) as UserData
        if(!userData.photoProfileUrl.isNullOrEmpty()) {
            Glide.with(this)
                .load(getString(R.string.base_url) + "/uploads/" + userData.photoProfileUrl)
                .into(binding.civImagesInfo)
        }
        binding.tvUsernameInfo.text = userData.username
        binding.tvEmailInfo.text = userData.email
    }
}