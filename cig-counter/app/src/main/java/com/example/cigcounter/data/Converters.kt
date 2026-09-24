package com.example.cigcounter.data

import androidx.room.TypeConverter
import com.example.cigcounter.data.cigarette.SyncState

class Converters {
    @TypeConverter
    fun fromSyncState(value: SyncState): String = value.name

    @TypeConverter
    fun toSyncState(value: String): SyncState = SyncState.valueOf(value)
}
