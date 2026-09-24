package com.example.cigcounter.data.cigarette

import kotlinx.coroutines.flow.Flow
import java.time.Instant
import java.time.ZoneId
import java.util.UUID

/**
 * Local-only for now: no Firebase Auth exists yet, so every row is written
 * under this fixed placeholder userId. When real Auth is wired in, this is
 * replaced by the signed-in user's Firebase UID and existing local rows can
 * be migrated to it.
 */
class CigaretteRepository(private val dao: CigaretteDao) {

    fun observeToday(): Flow<List<CigaretteEntity>> = dao.observeForDate(todayDateString())

    fun observeForDateRange(startDate: String, endDate: String): Flow<List<CigaretteEntity>> =
        dao.observeForDateRange(startDate, endDate)

    suspend fun addCigarette(timestamp: Long = System.currentTimeMillis()) {
        val now = System.currentTimeMillis()
        dao.upsert(
            CigaretteEntity(
                id = UUID.randomUUID().toString(),
                userId = LOCAL_USER_ID,
                timestamp = timestamp,
                date = dateStringOf(timestamp),
                deletedAt = null,
                updatedAt = now,
                syncState = SyncState.PENDING_UPSERT
            )
        )
    }

    suspend fun updateTimestamp(id: String, newTimestamp: Long) {
        val existing = dao.getById(id) ?: return
        val now = System.currentTimeMillis()
        dao.upsert(
            existing.copy(
                timestamp = newTimestamp,
                date = dateStringOf(newTimestamp),
                updatedAt = now,
                syncState = SyncState.PENDING_UPSERT
            )
        )
    }

    suspend fun softDelete(id: String) {
        val existing = dao.getById(id) ?: return
        val now = System.currentTimeMillis()
        dao.upsert(
            existing.copy(
                deletedAt = now,
                updatedAt = now,
                syncState = SyncState.PENDING_DELETE
            )
        )
    }

    companion object {
        const val LOCAL_USER_ID = "local_user"

        fun todayDateString(): String = dateStringOf(System.currentTimeMillis())

        fun dateStringOf(timestamp: Long): String =
            Instant.ofEpochMilli(timestamp).atZone(ZoneId.systemDefault()).toLocalDate().toString()
    }
}
