package com.bryan.sharepoint.ui.adapter

import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import coil.load
import com.bryan.sharepoint.R

class ImageGridAdapter(
    private val images: MutableList<String>,
    private val editable: Boolean,
    private val onRemoveClick: ((Int) -> Unit)? = null
) : RecyclerView.Adapter<ImageGridAdapter.ImageViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ImageViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_image, parent, false)
        return ImageViewHolder(view)
    }

    override fun onBindViewHolder(holder: ImageViewHolder, position: Int) {
        holder.bind(images[position], position)
    }

    override fun getItemCount(): Int = images.size

    fun addImage(imageUri: String) {
        if (images.size < 4) {
            images.add(imageUri)
            notifyItemInserted(images.size - 1)
        }
    }

    fun getImages(): List<String> = images.toList()

    fun removeImage(position: Int) {
        if (position in 0 until images.size) {
            images.removeAt(position)
            notifyDataSetChanged()
        }
    }

    inner class ImageViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val ivImage: ImageView = itemView.findViewById(R.id.ivImage)
        private val btnRemove: ImageButton = itemView.findViewById(R.id.btnRemove)

        fun bind(imageUri: String, position: Int) {
            ivImage.load(Uri.parse(imageUri)) {
                crossfade(true)
                placeholder(R.drawable.ic_image)
            }

            if (editable) {
                btnRemove.visibility = View.VISIBLE
                btnRemove.setOnClickListener {
                    onRemoveClick?.invoke(position)
                }
            } else {
                btnRemove.visibility = View.GONE
            }
        }
    }
}
