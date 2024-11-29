package com.kotlinchatapp.core.ui

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.content.res.AppCompatResources
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.kotlinchatapp.core.R
import com.kotlinchatapp.core.databinding.ItemListUsersBinding
import com.kotlinchatapp.core.domain.model.UserData

class UserAdapter(private val listener: UserAdapterClickListener): RecyclerView.Adapter<UserAdapter.UsersViewHolder>() {

    private var listUsers = ArrayList<UserData>()

    @SuppressLint("NotifyDataSetChanged")
    fun setData(newListUsers: List<UserData>?) {
        if (newListUsers == null) return
        listUsers.clear()
        listUsers.addAll(newListUsers)
        notifyDataSetChanged()
    }

    inner class UsersViewHolder(itemView: View): RecyclerView.ViewHolder(itemView) {
        private val binding = ItemListUsersBinding.bind(itemView)
        fun bind(data: UserData) {
            with(itemView) {
                if (!data.photoProfileUrl.isNullOrEmpty()) {
                    val image = resources.getString(R.string.base_url) + "/uploads/" + data.photoProfileUrl
                    Glide.with(context)
                        .load(image)
                        .into(binding.civImages)
                } else {
                    Glide.with(context)
                        .load(AppCompatResources.getDrawable(context, R.drawable.placeholder))
                        .into(binding.civImages)
                }
                binding.tvUsername.text = data.username
                setOnClickListener {
                    listener.onUserClickListener(data)
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UsersViewHolder {
        val binding = ItemListUsersBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return UsersViewHolder(binding.root)
    }

    override fun onBindViewHolder(holder: UsersViewHolder, position: Int) {
        val data = listUsers[position]
        holder.bind(data)
    }

    override fun getItemCount(): Int = listUsers.size
}