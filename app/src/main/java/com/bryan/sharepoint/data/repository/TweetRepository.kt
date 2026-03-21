package com.bryan.sharepoint.data.repository

import android.content.Context
import androidx.lifecycle.LiveData
import com.bryan.sharepoint.data.dao.CommentDao
import com.bryan.sharepoint.data.dao.TweetDao
import com.bryan.sharepoint.data.entity.Comment
import com.bryan.sharepoint.data.entity.Tweet
import com.bryan.sharepoint.data.entity.TweetWithComments
import com.bryan.sharepoint.util.ImageStorage
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class TweetRepository(
    private val context: Context,
    private val tweetDao: TweetDao, 
    private val commentDao: CommentDao
) {
    
    val allTweets: LiveData<List<Tweet>> = tweetDao.getAllTweets()
    val allTweetsWithComments: LiveData<List<TweetWithComments>> = tweetDao.getAllTweetsWithComments()

    suspend fun insertTweet(tweet: Tweet): Long = withContext(Dispatchers.IO) {
        tweetDao.insertTweet(tweet)
    }

    suspend fun updateTweet(tweet: Tweet) = withContext(Dispatchers.IO) {
        tweetDao.updateTweet(tweet.copy(updatedAt = System.currentTimeMillis()))
    }

    suspend fun deleteTweet(tweet: Tweet) = withContext(Dispatchers.IO) {
        // 删除关联的图片文件
        deleteTweetImages(tweet.images)
        tweetDao.deleteTweet(tweet)
    }

    suspend fun deleteTweetById(tweetId: Long) = withContext(Dispatchers.IO) {
        // 先获取推文，删除图片，再删除记录
        val tweet = tweetDao.getTweetById(tweetId)
        tweet?.let {
            deleteTweetImages(it.images)
            tweetDao.deleteTweetById(tweetId)
        }
    }

    private fun deleteTweetImages(imagesJson: String) {
        if (imagesJson.isBlank()) return
        try {
            val jsonArray = org.json.JSONArray(imagesJson)
            for (i in 0 until jsonArray.length()) {
                val imagePath = jsonArray.getString(i)
                ImageStorage.deleteImage(imagePath)
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    suspend fun getTweetById(tweetId: Long): Tweet? = withContext(Dispatchers.IO) {
        tweetDao.getTweetById(tweetId)
    }

    suspend fun getTweetWithComments(tweetId: Long): TweetWithComments? = withContext(Dispatchers.IO) {
        tweetDao.getTweetWithComments(tweetId)
    }

    // Comment operations
    suspend fun addComment(comment: Comment): Long = withContext(Dispatchers.IO) {
        commentDao.insertComment(comment)
    }

    suspend fun updateComment(comment: Comment) = withContext(Dispatchers.IO) {
        commentDao.updateComment(comment)
    }

    suspend fun deleteComment(comment: Comment) = withContext(Dispatchers.IO) {
        commentDao.deleteComment(comment)
    }

    suspend fun deleteCommentById(commentId: Long) = withContext(Dispatchers.IO) {
        commentDao.deleteCommentById(commentId)
    }

    fun getCommentsForTweet(tweetId: Long): LiveData<List<Comment>> {
        return commentDao.getCommentsForTweet(tweetId)
    }
}
