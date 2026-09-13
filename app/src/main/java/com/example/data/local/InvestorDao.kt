package com.example.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.data.model.Investor
import kotlinx.coroutines.flow.Flow

@Dao
interface InvestorDao {
    @Query("SELECT * FROM investors ORDER BY id ASC")
    fun getAllInvestors(): Flow<List<Investor>>

    @Query("SELECT * FROM investors WHERE isConnected = 1")
    fun getConnectedInvestors(): Flow<List<Investor>>

    @Query("SELECT * FROM investors WHERE id = :id")
    suspend fun getInvestorById(id: Long): Investor?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertInvestor(investor: Investor): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(investors: List<Investor>)

    @Update
    suspend fun updateInvestor(investor: Investor)

    @Query("UPDATE investors SET connectionStatus = :status, isConnected = :isConnected WHERE id = :id")
    suspend fun updateConnectionStatus(id: Long, status: String, isConnected: Boolean)

    @Query("SELECT COUNT(*) FROM investors")
    suspend fun getCount(): Int
}
