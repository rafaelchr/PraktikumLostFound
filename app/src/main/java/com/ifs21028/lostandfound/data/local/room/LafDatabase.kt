package com.ifs21028.lostandfound.data.local.room

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.ifs21028.lostandfound.data.local.entity.LafEntity

@Database(entities = [LafEntity::class], version = 1, exportSchema = false)
abstract class LafDatabase : RoomDatabase() {

    abstract fun lafDao(): ILafDao

    companion object {
        private const val Database_NAME = "LostFound.db"

        @Volatile
        private var INSTANCE: LafDatabase? = null

        @JvmStatic
        fun getInstance(context: Context): LafDatabase {
            if (INSTANCE == null) {
                synchronized(LafDatabase::class.java) {
                    INSTANCE = Room.databaseBuilder(
                        context.applicationContext,
                        LafDatabase::class.java,
                        Database_NAME
                    ).build()
                }
            }
            return INSTANCE as LafDatabase
        }
    }
}
