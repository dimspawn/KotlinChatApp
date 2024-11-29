package com.kotlinchatapp.core.ui

import com.kotlinchatapp.core.domain.model.ConversationConstraintData

interface DashboardAdapterClickListener {
    fun onLastMessageClickListener(conversationConstraintData: ConversationConstraintData)
}