package com.kotlinchatapp.core.ui

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.content.res.AppCompatResources
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.kotlinchatapp.core.R
import com.kotlinchatapp.core.databinding.ItemListOpponentChatBinding
import com.kotlinchatapp.core.databinding.ItemListOwnChatBinding
import com.kotlinchatapp.core.domain.model.MessageConstraintData

class ChatAdapter(private val userId: String): RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private var chats = ArrayList<MessageConstraintData>()

    private fun insertOrUpdate(chat: MessageConstraintData) {
        val dex = chats.indexOfFirst { it.id == chat.id }
        if (dex != -1) {
            chats[dex] = chat
            notifyItemChanged(dex)
        } else {
            chats.add(chat)
            notifyItemInserted(chats.size)
        }
    }

    fun insertAll(chats: List<MessageConstraintData>) {
        for (chat in chats) {
            insertOrUpdate(chat)
        }
    }

    inner class ChatOwnViewHolder(itemView: View): RecyclerView.ViewHolder(itemView) {
        private val binding = ItemListOwnChatBinding.bind(itemView)
        fun bind(data: MessageConstraintData){
            with(itemView) {
                val image = resources.getString(R.string.base_url) + "/uploads/" + data.photoProfileUrl
                if (data.photoProfileUrl != null) {
                    Glide.with(context)
                        .load(image)
                        .into(binding.civOwnId)
                } else {
                    Glide.with(context)
                        .load(AppCompatResources.getDrawable(context, R.drawable.placeholder))
                        .into(binding.civOwnId)
                }
                binding.tvOwnName.text = data.username
                binding.tvOwnChat.text = data.message
            }
        }
    }

    inner class ChatOpponentViewHolder(itemView: View): RecyclerView.ViewHolder(itemView) {
        private val binding = ItemListOpponentChatBinding.bind(itemView)
        fun bind(data: MessageConstraintData){
            with(itemView) {
                val image = resources.getString(R.string.base_url) + "/uploads/" + data.photoProfileUrl
                if (data.photoProfileUrl != null) {
                    Glide.with(context)
                        .load(image)
                        .into(binding.civOpponentChat)
                } else {
                    Glide.with(context)
                        .load(AppCompatResources.getDrawable(context, R.drawable.placeholder))
                        .into(binding.civOpponentChat)
                }
                binding.tvOpponentName.text = data.username
                binding.tvOpponentChat.text = data.message
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val bindingOpponent = ItemListOpponentChatBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        val bindingOwn = ItemListOwnChatBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return when(viewType) {
            0 -> ChatOpponentViewHolder(bindingOpponent.root)
            1 -> ChatOwnViewHolder(bindingOwn.root)
            else -> ChatOpponentViewHolder(bindingOpponent.root)
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val data = chats[position]
        when(holder.itemViewType) {
            0 -> {
                val opponentViewHolder = holder as ChatOpponentViewHolder
                opponentViewHolder.bind(data)
            }
            1 -> {
                val ownViewHolder = holder as ChatOwnViewHolder
                ownViewHolder.bind(data)
            }
        }
    }

    override fun getItemCount(): Int = chats.size

    override fun getItemViewType(position: Int): Int {
        return if (userId == chats[position].userId) 1 else 0
    }
}