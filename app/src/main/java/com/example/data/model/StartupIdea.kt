package com.example.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "startup_ideas")
data class StartupIdea(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val title: String,
    val founderName: String,
    val founderRole: String = "Founder & CEO",
    val tagline: String,
    val problem: String,
    val solution: String,
    val category: String, // "AI & ML", "FinTech", "ClimateTech", "HealthTech", "B2B SaaS", "Social"
    val stage: String, // "Idea", "Prototype", "Pre-Seed", "Seed", "Series A"
    val fundingAsk: String, // e.g. "$150,000" or "Mentorship & Advisory"
    val lookingFor: String, // "Angel Investment & Friendly Advisor", "Co-Founder", "Growth Mentor"
    val traction: String = "Active development with 200+ pilot waitlist users",
    val friendlyNote: String = "Let's grab a coffee and chat about reshaping the future together!",
    val likesCount: Int = 18,
    val isLiked: Boolean = false,
    val isBookmarked: Boolean = false,
    val createdAt: Long = System.currentTimeMillis()
)
