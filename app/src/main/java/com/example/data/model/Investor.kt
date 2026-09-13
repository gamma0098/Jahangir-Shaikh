package com.example.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "investors")
data class Investor(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val name: String,
    val title: String, // "Angel Investor & Ex-Founder", "General Partner"
    val firm: String, // "Horizon Capital", "Independent Angel"
    val focusSectors: String, // "AI, Climate, B2B SaaS"
    val checkSize: String, // "$25k - $150k"
    val bio: String,
    val friendlyPhilosophy: String, // "I invest in honest, kind founders. A 30-min coffee chat can build lifelong trust."
    val favoriteCoffee: String = "Double Espresso ☕",
    val location: String = "San Francisco, CA & Remote",
    val isConnected: Boolean = false,
    val connectionStatus: String = "none" // "none", "requested", "connected", "coffee_scheduled"
)
