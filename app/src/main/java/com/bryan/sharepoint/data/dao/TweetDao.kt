package com.bryan.sharepoint.data.dao

import androidx.lifecycle.LiveData
import androidx.room.*
import com.bryan.sharepoint.data.entity.Tweet
import com.bryan.sharepoint.data.entity.TweetWithComments

@Dao
interface TweetDao {
    @Query("SELECT * FROM tweets ORDER BY createdAt DESC")
    fun getAllTweets(): LiveData<List<Tweet>>

    @Query("SELECT * FROM tweets ORDER BY createdAt DESC")
    suspend fun getAllTweetsSync(): List<Tweet>

    @Query("SELECT * FROM tweets WHERE id = :tweetId")
    suspend fun getTweetById(tweetId: Long): Tweet?

    @Transaction
    @Query("SELECT * FROM tweets WHERE id = :tweetId")
    suspend fun getTweetWithComments(tweetId: Long): TweetWithComments?

    @Transaction
    @Query("SELECT * FROM tweets ORDER BY createdAt DESC")
    fun getAllTweetsWithComments(): LiveData<List<TweetWithComments>>

    @Insert
    suspend fun insertTweet(tweet: Tweet): Long

    @Update
    suspend fun updateTweet(tweet: Tweet)

    @Delete
    suspend fun deleteTweet(tweet: Tweet)

    @Query("DELETE FROM tweets WHERE id = :tweetId")
    suspend fun deleteTweetById(tweetId: Long)
}
