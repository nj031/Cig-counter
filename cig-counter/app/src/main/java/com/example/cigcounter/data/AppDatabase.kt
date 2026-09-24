package com.example.cigcounter.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.example.cigcounter.data.cigarette.CigaretteDao
import com.example.cigcounter.data.cigarette.CigaretteEntity

@Database(entities = [CigaretteEntity::class], version = 1, exportSchema = false)
@TypeConverters(Converters::class)
abstract class AppDatabase : RoomDatabase() {

    abstract fun cigaretteDao(): CigaretteDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getInstance(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                INSTANCE ?: Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "cigcounter.db"
                ).build().also { INSTANCE = it }
            }
        }
    }
}
