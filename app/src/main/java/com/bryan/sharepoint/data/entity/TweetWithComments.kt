package com.bryan.sharepoint.data.entity

import androidx.room.Embedded
import androidx.room.Relation

data class TweetWithComments(
    @Embedded val tweet: Tweet,
    @Relation(
        parentColumn = "id",
        entityColumn = "tweetId"
    )
    val comments: List<Comment>
)
