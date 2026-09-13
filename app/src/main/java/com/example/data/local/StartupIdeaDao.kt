package com.example.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.data.model.StartupIdea
import kotlinx.coroutines.flow.Flow

@Dao
interface StartupIdeaDao {
    @Query("SELECT * FROM startup_ideas ORDER BY createdAt DESC")
    fun getAllIdeas(): Flow<List<StartupIdea>>

    @Query("SELECT * FROM startup_ideas WHERE isBookmarked = 1 ORDER BY createdAt DESC")
    fun getBookmarkedIdeas(): Flow<List<StartupIdea>>

    @Query("SELECT * FROM startup_ideas WHERE id = :id")
    suspend fun getIdeaById(id: Long): StartupIdea?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertIdea(idea: StartupIdea): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(ideas: List<StartupIdea>)

    @Update
    suspend fun updateIdea(idea: StartupIdea)

    @Query("UPDATE startup_ideas SET isLiked = :isLiked, likesCount = likesCount + :delta WHERE id = :id")
    suspend fun toggleLike(id: Long, isLiked: Boolean, delta: Int)

    @Query("UPDATE startup_ideas SET isBookmarked = :isBookmarked WHERE id = :id")
    suspend fun toggleBookmark(id: Long, isBookmarked: Boolean)

    @Query("DELETE FROM startup_ideas WHERE id = :id")
    suspend fun deleteIdea(id: Long)

    @Query("SELECT COUNT(*) FROM startup_ideas")
    suspend fun getCount(): Int
}
