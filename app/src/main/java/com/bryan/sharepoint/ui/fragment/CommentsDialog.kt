package com.bryan.sharepoint.ui.fragment

import android.app.Dialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.TextView
import androidx.fragment.app.DialogFragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bryan.sharepoint.R
import com.bryan.sharepoint.data.entity.Comment
import com.bryan.sharepoint.data.entity.Tweet
import com.bryan.sharepoint.ui.adapter.CommentAdapter
import com.bryan.sharepoint.ui.viewmodel.TweetViewModel

class CommentsDialog : DialogFragment() {

    private lateinit var viewModel: TweetViewModel
    private var tweet: Tweet? = null

    private lateinit var rvComments: RecyclerView
    private lateinit var etComment: EditText
    private lateinit var btnSend: Button
    private lateinit var btnClose: ImageButton
    private lateinit var tvCommentCount: TextView

    private lateinit var commentAdapter: CommentAdapter

    fun setTweet(tweet: Tweet) {
        this.tweet = tweet
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val dialog = super.onCreateDialog(savedInstanceState)
        dialog.window?.setLayout(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.MATCH_PARENT
        )
        return dialog
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel = ViewModelProvider(requireActivity())[TweetViewModel::class.java]
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.dialog_comments, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initViews(view)
        setupListeners()
        setupRecyclerView()
        observeComments()
    }

    private fun initViews(view: View) {
        rvComments = view.findViewById(R.id.rvComments)
        etComment = view.findViewById(R.id.etComment)
        btnSend = view.findViewById(R.id.btnSend)
        btnClose = view.findViewById(R.id.btnClose)

        // Find comment count text in the included tweet layout
        val tweetPreview = view.findViewById<View>(R.id.tweetPreview)
        tvCommentCount = tweetPreview.findViewById(R.id.tvCommentCount)
    }

    private fun setupListeners() {
        btnClose.setOnClickListener { dismiss() }

        btnSend.setOnClickListener {
            postComment()
        }

        etComment.addTextChangedListener(object : android.text.TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
            override fun afterTextChanged(s: android.text.Editable?) {
                btnSend.isEnabled = !s.isNullOrBlank()
            }
        })
    }

    private fun setupRecyclerView() {
        commentAdapter = CommentAdapter { comment ->
            deleteComment(comment)
        }
        rvComments.layoutManager = LinearLayoutManager(context)
        rvComments.adapter = commentAdapter
    }

    private fun observeComments() {
        tweet?.let { t ->
            viewModel.getCommentsForTweet(t.id).observe(viewLifecycleOwner) { comments ->
                commentAdapter.setData(comments)
                tvCommentCount.text = comments.size.toString()
            }
        }
    }

    private fun postComment() {
        val content = etComment.text.toString().trim()
        if (content.isBlank() || tweet == null) return

        val comment = Comment(
            tweetId = tweet!!.id,
            content = content
        )
        viewModel.addComment(comment)
        etComment.text.clear()
    }

    private fun deleteComment(comment: Comment) {
        androidx.appcompat.app.AlertDialog.Builder(requireContext())
            .setTitle("删除评论")
            .setMessage("确定要删除这条评论吗？")
            .setPositiveButton("删除") { _, _ ->
                viewModel.deleteComment(comment)
            }
            .setNegativeButton("取消", null)
            .show()
    }

    companion object {
        const val TAG = "CommentsDialog"
    }
}
