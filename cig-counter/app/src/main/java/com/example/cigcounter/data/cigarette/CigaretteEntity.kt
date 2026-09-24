package com.example.cigcounter.data.cigarette

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "cigarettes")
data class CigaretteEntity(
    @PrimaryKey val id: String,
    val userId: String,
    val timestamp: Long,
    val date: String,
    val deletedAt: Long?,
    val updatedAt: Long,
    val syncState: SyncState
)
