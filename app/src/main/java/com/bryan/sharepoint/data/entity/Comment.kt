package com.bryan.sharepoint.data.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "comments",
    foreignKeys = [
        ForeignKey(
            entity = Tweet::class,
            parentColumns = ["id"],
            childColumns = ["tweetId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index("tweetId")]
)
data class Comment(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val tweetId: Long,
    val content: String,
    val createdAt: Long = System.currentTimeMillis()
)
