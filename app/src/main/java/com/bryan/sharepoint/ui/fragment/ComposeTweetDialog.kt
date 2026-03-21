package com.bryan.sharepoint.ui.fragment

import android.app.Activity
import android.app.Dialog
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.DialogFragment
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bryan.sharepoint.R
import com.bryan.sharepoint.data.entity.Tweet
import com.bryan.sharepoint.ui.adapter.ImageGridAdapter
import com.bryan.sharepoint.util.ImageStorage
import org.json.JSONArray

class ComposeTweetDialog : DialogFragment() {

    interface OnTweetListener {
        fun onTweetPosted(tweet: Tweet)
        fun onTweetUpdated(tweet: Tweet)
    }

    private var listener: OnTweetListener? = null
    private var editingTweet: Tweet? = null

    private lateinit var etContent: EditText
    private lateinit var etLink: EditText
    private lateinit var btnPost: Button
    private lateinit var btnClose: ImageButton
    private lateinit var btnAddImage: ImageButton
    private lateinit var btnAddLink: ImageButton
    private lateinit var btnRemoveLink: ImageButton
    private lateinit var tvCharCount: TextView
    private lateinit var rvImages: RecyclerView
    private lateinit var layoutLink: LinearLayout

    private lateinit var imageAdapter: ImageGridAdapter

    private val pickImageLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            result.data?.data?.let { uri ->
                // 复制图片到应用私有目录
                context?.let { ctx ->
                    val privatePath = ImageStorage.copyToPrivateStorage(ctx, uri)
                    if (privatePath != null) {
                        imageAdapter.addImage(privatePath)
                        updateImagesVisibility()
                    } else {
                        Toast.makeText(context, "图片保存失败", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }
    }

    fun setOnTweetListener(listener: OnTweetListener) {
        this.listener = listener
    }

    fun setEditingTweet(tweet: Tweet) {
        this.editingTweet = tweet
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val dialog = super.onCreateDialog(savedInstanceState)
        dialog.window?.setLayout(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        )
        return dialog
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.dialog_compose_tweet, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initViews(view)
        setupListeners()
        setupImageAdapter()

        // If editing, populate fields
        editingTweet?.let { tweet ->
            etContent.setText(tweet.content)
            if (tweet.link.isNotBlank()) {
                etLink.setText(tweet.link)
                layoutLink.visibility = View.VISIBLE
            }
            val images = parseImages(tweet.images)
            images.forEach { imageAdapter.addImage(it) }
            updateImagesVisibility()
            btnPost.text = "保存"
        }

        updateCharCount()
        updatePostButton()
    }

    private fun initViews(view: View) {
        etContent = view.findViewById(R.id.etContent)
        etLink = view.findViewById(R.id.etLink)
        btnPost = view.findViewById(R.id.btnPost)
        btnClose = view.findViewById(R.id.btnClose)
        btnAddImage = view.findViewById(R.id.btnAddImage)
        btnAddLink = view.findViewById(R.id.btnAddLink)
        btnRemoveLink = view.findViewById(R.id.btnRemoveLink)
        tvCharCount = view.findViewById(R.id.tvCharCount)
        rvImages = view.findViewById(R.id.rvImages)
        layoutLink = view.findViewById(R.id.layoutLink)
    }

    private fun setupListeners() {
        btnClose.setOnClickListener { dismiss() }

        btnPost.setOnClickListener {
            postTweet()
        }

        btnAddImage.setOnClickListener {
            if (imageAdapter.getImages().size >= 4) {
                Toast.makeText(context, "最多只能添加4张图片", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            val intent = Intent(Intent.ACTION_PICK).apply {
                type = "image/*"
            }
            pickImageLauncher.launch(intent)
        }

        btnAddLink.setOnClickListener {
            layoutLink.visibility = View.VISIBLE
        }

        btnRemoveLink.setOnClickListener {
            etLink.text.clear()
            layoutLink.visibility = View.GONE
        }

        etContent.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
            override fun afterTextChanged(s: Editable?) {
                updateCharCount()
                updatePostButton()
            }
        })
    }

    private fun setupImageAdapter() {
        imageAdapter = ImageGridAdapter(mutableListOf(), true) { position ->
            // 获取要删除的图片路径，删除文件
            val imagePath = imageAdapter.getImages().getOrNull(position)
            imagePath?.let { ImageStorage.deleteImage(it) }
            imageAdapter.removeImage(position)
            updateImagesVisibility()
        }
        rvImages.adapter = imageAdapter
        rvImages.layoutManager = GridLayoutManager(context, 2)
    }

    private fun updateImagesVisibility() {
        rvImages.visibility = if (imageAdapter.getImages().isNotEmpty()) View.VISIBLE else View.GONE
    }

    private fun updateCharCount() {
        val length = etContent.text?.length ?: 0
        tvCharCount.text = "$length/500"
        tvCharCount.setTextColor(
            if (length > 500) android.graphics.Color.RED
            else resources.getColor(R.color.gray, null)
        )
    }

    private fun updatePostButton() {
        val content = etContent.text?.toString()?.trim() ?: ""
        btnPost.isEnabled = content.isNotBlank() && content.length <= 500
    }

    private fun postTweet() {
        val content = etContent.text.toString().trim()
        val link = etLink.text.toString().trim()
        val newImages = imageAdapter.getImages()
        val images = JSONArray(newImages).toString()

        // 如果是编辑模式，删除被移除的旧图片
        editingTweet?.let { oldTweet ->
            val oldImages = parseImages(oldTweet.images)
            val removedImages = oldImages.filter { it !in newImages }
            removedImages.forEach { ImageStorage.deleteImage(it) }
        }

        val tweet = editingTweet?.copy(
            content = content,
            link = link,
            images = images
        ) ?: Tweet(
            content = content,
            link = link,
            images = images
        )

        if (editingTweet != null) {
            listener?.onTweetUpdated(tweet)
        } else {
            listener?.onTweetPosted(tweet)
        }
        dismiss()
    }

    private fun parseImages(imagesJson: String): List<String> {
        return try {
            if (imagesJson.isBlank()) return emptyList()
            val jsonArray = JSONArray(imagesJson)
            List(jsonArray.length()) { jsonArray.getString(it) }
        } catch (e: Exception) {
            emptyList()
        }
    }

    companion object {
        const val TAG = "ComposeTweetDialog"
    }
}
