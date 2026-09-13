package com.example.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "chat_messages")
data class ChatMessage(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val peerId: Long, // Investor id or Peer id
    val peerName: String,
    val isFromMe: Boolean,
    val message: String,
    val timestamp: Long = System.currentTimeMillis(),
    val isCoffeeInvite: Boolean = false,
    val inviteAccepted: Boolean = false
)
