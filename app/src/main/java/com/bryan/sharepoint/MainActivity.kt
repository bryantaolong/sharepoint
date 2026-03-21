package com.bryan.sharepoint

import android.os.Bundle
import android.view.View
import android.widget.LinearLayout
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.bryan.sharepoint.data.entity.Tweet
import com.bryan.sharepoint.ui.adapter.TweetAdapter
import com.bryan.sharepoint.ui.fragment.CommentsDialog
import com.bryan.sharepoint.ui.fragment.ComposeTweetDialog
import com.bryan.sharepoint.ui.viewmodel.TweetViewModel
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.tabs.TabLayout

class MainActivity : AppCompatActivity(), ComposeTweetDialog.OnTweetListener {

    private lateinit var viewModel: TweetViewModel
    private lateinit var tweetAdapter: TweetAdapter
    private lateinit var swipeRefresh: SwipeRefreshLayout
    private lateinit var rvTweets: RecyclerView
    private lateinit var layoutEmpty: LinearLayout
    private lateinit var fabCompose: FloatingActionButton
    private lateinit var tabLayout: TabLayout

    private var currentTab = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        initViews()
        setupViewModel()
        setupRecyclerView()
        setupListeners()
        observeTweets()
    }

    private fun initViews() {
        swipeRefresh = findViewById(R.id.swipeRefresh)
        rvTweets = findViewById(R.id.rvTweets)
        layoutEmpty = findViewById(R.id.layoutEmpty)
        fabCompose = findViewById(R.id.fabCompose)
        tabLayout = findViewById(R.id.tabLayout)
    }

    private fun setupViewModel() {
        viewModel = ViewModelProvider(this)[TweetViewModel::class.java]
    }

    private fun setupRecyclerView() {
        tweetAdapter = TweetAdapter(
            onCommentClick = { tweet -> showCommentsDialog(tweet) },
            onEditClick = { tweet -> showEditDialog(tweet) },
            onDeleteClick = { tweet -> confirmDeleteTweet(tweet) }
        )
        rvTweets.layoutManager = LinearLayoutManager(this)
        rvTweets.adapter = tweetAdapter
    }

    private fun setupListeners() {
        fabCompose.setOnClickListener {
            showComposeDialog()
        }

        swipeRefresh.setOnRefreshListener {
            swipeRefresh.isRefreshing = false
        }

        tabLayout.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab?) {
                currentTab = tab?.position ?: 0
                filterTweets()
            }
            override fun onTabUnselected(tab: TabLayout.Tab?) {}
            override fun onTabReselected(tab: TabLayout.Tab?) {}
        })
    }

    private fun observeTweets() {
        viewModel.allTweetsWithComments.observe(this) { tweets ->
            if (tweets.isEmpty()) {
                layoutEmpty.visibility = View.VISIBLE
                rvTweets.visibility = View.GONE
            } else {
                layoutEmpty.visibility = View.GONE
                rvTweets.visibility = View.VISIBLE
                filterTweets()
            }
        }
    }

    private fun filterTweets() {
        val allTweets = viewModel.allTweetsWithComments.value ?: return
        val filtered = when (currentTab) {
            1 -> allTweets.filter { it.tweet.images.isNotBlank() }
            else -> allTweets
        }
        tweetAdapter.setData(filtered)
    }

    private fun showComposeDialog() {
        val dialog = ComposeTweetDialog()
        dialog.setOnTweetListener(this)
        dialog.show(supportFragmentManager, ComposeTweetDialog.TAG)
    }

    private fun showEditDialog(tweet: Tweet) {
        val dialog = ComposeTweetDialog()
        dialog.setEditingTweet(tweet)
        dialog.setOnTweetListener(this)
        dialog.show(supportFragmentManager, ComposeTweetDialog.TAG)
    }

    private fun showCommentsDialog(tweet: Tweet) {
        val dialog = CommentsDialog()
        dialog.setTweet(tweet)
        dialog.show(supportFragmentManager, CommentsDialog.TAG)
    }

    private fun confirmDeleteTweet(tweet: Tweet) {
        AlertDialog.Builder(this)
            .setTitle("删除推文")
            .setMessage("确定要删除这条推文吗？此操作不可撤销。")
            .setPositiveButton("删除") { _, _ ->
                viewModel.deleteTweet(tweet)
                Toast.makeText(this, "推文已删除", Toast.LENGTH_SHORT).show()
            }
            .setNegativeButton("取消", null)
            .show()
    }

    override fun onTweetPosted(tweet: Tweet) {
        viewModel.insertTweet(tweet)
        Toast.makeText(this, "推文已发布", Toast.LENGTH_SHORT).show()
    }

    override fun onTweetUpdated(tweet: Tweet) {
        viewModel.updateTweet(tweet)
        Toast.makeText(this, "推文已更新", Toast.LENGTH_SHORT).show()
    }
}
