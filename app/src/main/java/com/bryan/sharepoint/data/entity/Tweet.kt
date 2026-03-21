package com.bryan.sharepoint.data.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.Date

@Entity(tableName = "tweets")
data class Tweet(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val content: String,
    val images: String = "", // JSON array of image URIs
    val link: String = "",
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis()
)
