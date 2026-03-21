package com.bryan.sharepoint.data.dao

import androidx.lifecycle.LiveData
import androidx.room.*
import com.bryan.sharepoint.data.entity.Comment

@Dao
interface CommentDao {
    @Query("SELECT * FROM comments WHERE tweetId = :tweetId ORDER BY createdAt ASC")
    fun getCommentsForTweet(tweetId: Long): LiveData<List<Comment>>

    @Query("SELECT * FROM comments WHERE tweetId = :tweetId ORDER BY createdAt ASC")
    suspend fun getCommentsForTweetSync(tweetId: Long): List<Comment>

    @Insert
    suspend fun insertComment(comment: Comment): Long

    @Update
    suspend fun updateComment(comment: Comment)

    @Delete
    suspend fun deleteComment(comment: Comment)

    @Query("DELETE FROM comments WHERE id = :commentId")
    suspend fun deleteCommentById(commentId: Long)

    @Query("DELETE FROM comments WHERE tweetId = :tweetId")
    suspend fun deleteCommentsForTweet(tweetId: Long)
}
