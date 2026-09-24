package com.example.cigcounter.data.cigarette

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface CigaretteDao {

    @Query("SELECT * FROM cigarettes WHERE date = :date AND deletedAt IS NULL ORDER BY timestamp ASC")
    fun observeForDate(date: String): Flow<List<CigaretteEntity>>

    @Query("SELECT * FROM cigarettes WHERE date BETWEEN :startDate AND :endDate AND deletedAt IS NULL ORDER BY timestamp ASC")
    fun observeForDateRange(startDate: String, endDate: String): Flow<List<CigaretteEntity>>

    @Query("SELECT * FROM cigarettes WHERE id = :id")
    suspend fun getById(id: String): CigaretteEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsert(entity: CigaretteEntity)

    @Query("SELECT * FROM cigarettes WHERE syncState != 'SYNCED'")
    suspend fun getPendingSync(): List<CigaretteEntity>

    @Query("SELECT COUNT(*) FROM cigarettes WHERE syncState != 'SYNCED'")
    fun observePendingCount(): Flow<Int>

    @Query("UPDATE cigarettes SET syncState = 'SYNCED' WHERE id = :id AND updatedAt = :updatedAt")
    suspend fun markSynced(id: String, updatedAt: Long)

    @Query("DELETE FROM cigarettes")
    suspend fun clearAll()
}
