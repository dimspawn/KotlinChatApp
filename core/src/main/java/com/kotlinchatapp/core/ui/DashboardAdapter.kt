package com.kotlinchatapp.core.ui

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.content.res.AppCompatResources
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.kotlinchatapp.core.R
import com.kotlinchatapp.core.databinding.ItemListDashboardMessagesBinding
import com.kotlinchatapp.core.domain.model.ConversationConstraintData

class DashboardAdapter(private val userId: String, private val listener: DashboardAdapterClickListener): RecyclerView.Adapter<DashboardAdapter.LastMessageViewHolder>() {
    private var converseMessage = mutableListOf<ConversationConstraintData>()

    private fun insertOrUpdate(newConMessages: ConversationConstraintData) {
        val dex = converseMessage.indexOfFirst { it.conversationId == newConMessages.conversationId }
        if (dex == 0) {
            converseMessage[0] = newConMessages
            notifyItemChanged(0)
        } else if (dex > 0) {
            converseMessage.removeAt(dex)
            converseMessage.add(0, newConMessages)
            notifyItemRangeChanged(0, dex+1)
        } else {
            converseMessage.add(0, newConMessages)
            notifyItemInserted(0)
        }
    }

    fun insertAll(conData: List<ConversationConstraintData>) {
        for (con in conData) {
            insertOrUpdate(con)
        }
    }

    inner class LastMessageViewHolder(itemView: View): RecyclerView.ViewHolder(itemView) {
        private val binding = ItemListDashboardMessagesBinding.bind(itemView)
        fun bind(data: ConversationConstraintData) {
            with(itemView){
                if (data.photoUri != null) {
                    val image = resources.getString(R.string.base_url) + "/uploads/" + data.photoUri
                    Glide.with(context).load(image).into(binding.civLastMessages)
                } else {
                    Glide.with(context).load(AppCompatResources.getDrawable(context, R.drawable.placeholder)).into(binding.civLastMessages)
                }
                binding.tvNameLastMessage.text = data.username
                binding.tvMessageLastMessage.text = if (data.senderId == userId) {
                    "You: ${data.lastMessage}"
                } else{
                    data.lastMessage
                }
                setOnClickListener {
                    listener.onLastMessageClickListener(data)
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): LastMessageViewHolder {
        val binding = ItemListDashboardMessagesBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return LastMessageViewHolder(binding.root)
    }

    override fun onBindViewHolder(holder: LastMessageViewHolder, position: Int) {
        val data = converseMessage[position]
        holder.bind(data)
    }

    override fun getItemCount(): Int = converseMessage.size
}