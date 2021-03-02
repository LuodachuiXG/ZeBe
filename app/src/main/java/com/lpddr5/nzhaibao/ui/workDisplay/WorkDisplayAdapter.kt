package com.lpddr5.nzhaibao.ui.workDisplay

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DecodeFormat
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.request.RequestOptions
import com.bumptech.glide.request.target.Target.SIZE_ORIGINAL
import com.lpddr5.nzhaibao.LogUtil
import com.lpddr5.nzhaibao.databinding.ItemWorkDisplayBinding
import com.lpddr5.nzhaibao.logic.model.NineFiveMMWork
import com.lpddr5.nzhaibao.ui.imageDisplay.ImageDisplayActivity

class WorkDisplayAdapter(private val context: Context, private val imageUrlList: List<String>) :
    RecyclerView.Adapter<WorkDisplayAdapter.ViewHolder>() {

    private val requestOptions = RequestOptions()
        .diskCacheStrategy(DiskCacheStrategy.ALL)
        .format(DecodeFormat.PREFER_RGB_565)

    inner class ViewHolder(binding: ItemWorkDisplayBinding) : RecyclerView.ViewHolder(binding.root) {
        val imageView: ImageView = binding.itemWorkImageView
        val textViewId: TextView = binding.itemWorkId
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemWorkDisplayBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        val holder = ViewHolder(binding)
        holder.imageView.setOnClickListener {
            ImageDisplayActivity.startActivity(context, holder.imageView.drawable, holder.adapterPosition)
        }
        return ViewHolder(binding)
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: WorkDisplayAdapter.ViewHolder, position: Int) {
        val imageUrl = imageUrlList[position]
        LogUtil.v(this, imageUrl)


        Glide.with(context).load(imageUrl).into(holder.imageView)
        holder.textViewId.text = "${position + 1}/${imageUrlList.size}"
    }

    override fun getItemCount() = imageUrlList.size

}