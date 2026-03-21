package com.bryan.sharepoint.data.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.bryan.sharepoint.data.dao.CommentDao
import com.bryan.sharepoint.data.dao.TweetDao
import com.bryan.sharepoint.data.entity.Comment
import com.bryan.sharepoint.data.entity.Tweet

@Database(
    entities = [Tweet::class, Comment::class],
    version = 1,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun tweetDao(): TweetDao
    abstract fun commentDao(): CommentDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "sharepoint_database"
                )
                    .fallbackToDestructiveMigration()
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}
