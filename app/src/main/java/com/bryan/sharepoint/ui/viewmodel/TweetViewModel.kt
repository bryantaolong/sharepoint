package com.bryan.sharepoint.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.viewModelScope
import com.bryan.sharepoint.data.database.AppDatabase
import com.bryan.sharepoint.data.entity.Comment
import com.bryan.sharepoint.data.entity.Tweet
import com.bryan.sharepoint.data.entity.TweetWithComments
import com.bryan.sharepoint.data.repository.TweetRepository
import kotlinx.coroutines.launch

class TweetViewModel(application: Application) : AndroidViewModel(application) {
    private val repository: TweetRepository
    val allTweets: LiveData<List<Tweet>>
    val allTweetsWithComments: LiveData<List<TweetWithComments>>

    init {
        val tweetDao = AppDatabase.getDatabase(application).tweetDao()
        val commentDao = AppDatabase.getDatabase(application).commentDao()
        repository = TweetRepository(application, tweetDao, commentDao)
        allTweets = repository.allTweets
        allTweetsWithComments = repository.allTweetsWithComments
    }

    fun insertTweet(tweet: Tweet, onComplete: ((Long) -> Unit)? = null) = viewModelScope.launch {
        val id = repository.insertTweet(tweet)
        onComplete?.invoke(id)
    }

    fun updateTweet(tweet: Tweet) = viewModelScope.launch {
        repository.updateTweet(tweet)
    }

    fun deleteTweet(tweet: Tweet) = viewModelScope.launch {
        repository.deleteTweet(tweet)
    }

    fun deleteTweetById(tweetId: Long) = viewModelScope.launch {
        repository.deleteTweetById(tweetId)
    }

    fun addComment(comment: Comment) = viewModelScope.launch {
        repository.addComment(comment)
    }

    fun updateComment(comment: Comment) = viewModelScope.launch {
        repository.updateComment(comment)
    }

    fun deleteComment(comment: Comment) = viewModelScope.launch {
        repository.deleteComment(comment)
    }

    fun deleteCommentById(commentId: Long) = viewModelScope.launch {
        repository.deleteCommentById(commentId)
    }

    fun getCommentsForTweet(tweetId: Long): LiveData<List<Comment>> {
        return repository.getCommentsForTweet(tweetId)
    }
}
