package com.bryan.sharepoint.ui.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bryan.sharepoint.R
import com.bryan.sharepoint.data.entity.Tweet
import com.bryan.sharepoint.data.entity.TweetWithComments
import org.json.JSONArray
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.TimeUnit

class TweetAdapter(
    private val onCommentClick: (Tweet) -> Unit,
    private val onEditClick: (Tweet) -> Unit,
    private val onDeleteClick: (Tweet) -> Unit
) : RecyclerView.Adapter<TweetAdapter.TweetViewHolder>() {

    private var tweets: List<TweetWithComments> = emptyList()

    fun setData(newTweets: List<TweetWithComments>) {
        tweets = newTweets
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TweetViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_tweet, parent, false)
        return TweetViewHolder(view)
    }

    override fun onBindViewHolder(holder: TweetViewHolder, position: Int) {
        holder.bind(tweets[position])
    }

    override fun getItemCount(): Int = tweets.size

    inner class TweetViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val tvContent: TextView = itemView.findViewById(R.id.tvContent)
        private val tvTime: TextView = itemView.findViewById(R.id.tvTime)
        private val tvCommentCount: TextView = itemView.findViewById(R.id.tvCommentCount)
        private val rvImages: RecyclerView = itemView.findViewById(R.id.rvImages)
        private val cardLink: CardView = itemView.findViewById(R.id.cardLink)
        private val tvLinkUrl: TextView = itemView.findViewById(R.id.tvLinkUrl)
        private val btnComment: View = itemView.findViewById(R.id.btnComment)
        private val btnEdit: View = itemView.findViewById(R.id.btnEdit)
        private val btnDelete: View = itemView.findViewById(R.id.btnDelete)
        private val btnMore: ImageButton = itemView.findViewById(R.id.btnMore)

        private var imageAdapter: ImageGridAdapter? = null

        fun bind(tweetWithComments: TweetWithComments) {
            val tweet = tweetWithComments.tweet
            val comments = tweetWithComments.comments

            tvContent.text = tweet.content
            tvTime.text = "· ${formatTime(tweet.createdAt)}"
            tvCommentCount.text = comments.size.toString()

            // Setup images
            val images = parseImages(tweet.images)
            if (images.isNotEmpty()) {
                rvImages.visibility = View.VISIBLE
                imageAdapter = ImageGridAdapter(images, false)
                rvImages.adapter = imageAdapter
                rvImages.layoutManager = GridLayoutManager(
                    itemView.context,
                    when {
                        images.size == 1 -> 1
                        images.size == 2 -> 2
                        else -> 2
                    }
                )
            } else {
                rvImages.visibility = View.GONE
            }

            // Setup link
            if (tweet.link.isNotBlank()) {
                cardLink.visibility = View.VISIBLE
                tvLinkUrl.text = tweet.link
            } else {
                cardLink.visibility = View.GONE
            }

            // Click listeners
            btnComment.setOnClickListener { onCommentClick(tweet) }
            btnEdit.setOnClickListener { onEditClick(tweet) }
            btnDelete.setOnClickListener { onDeleteClick(tweet) }
            btnMore.setOnClickListener {
                // Show popup menu
                val popup = android.widget.PopupMenu(itemView.context, btnMore)
                popup.menuInflater.inflate(R.menu.menu_tweet_more, popup.menu)
                popup.setOnMenuItemClickListener { menuItem ->
                    when (menuItem.itemId) {
                        R.id.action_edit -> onEditClick(tweet)
                        R.id.action_delete -> onDeleteClick(tweet)
                    }
                    true
                }
                popup.show()
            }
        }

        private fun parseImages(imagesJson: String): MutableList<String> {
            return try {
                if (imagesJson.isBlank()) return mutableListOf()
                val jsonArray = JSONArray(imagesJson)
                MutableList(jsonArray.length()) { jsonArray.getString(it) }
            } catch (e: Exception) {
                mutableListOf()
            }
        }

        private fun formatTime(timestamp: Long): String {
            val now = System.currentTimeMillis()
            val diff = now - timestamp

            return when {
                diff < TimeUnit.MINUTES.toMillis(1) -> "刚刚"
                diff < TimeUnit.HOURS.toMillis(1) -> "${TimeUnit.MILLISECONDS.toMinutes(diff)}分钟前"
                diff < TimeUnit.DAYS.toMillis(1) -> "${TimeUnit.MILLISECONDS.toHours(diff)}小时前"
                diff < TimeUnit.DAYS.toMillis(7) -> "${TimeUnit.MILLISECONDS.toDays(diff)}天前"
                else -> {
                    val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
                    sdf.format(Date(timestamp))
                }
            }
        }
    }
}
