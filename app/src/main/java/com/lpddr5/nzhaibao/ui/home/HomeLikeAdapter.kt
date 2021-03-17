package com.lpddr5.nzhaibao.ui.home

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.lpddr5.nzhaibao.databinding.ItemWorkBinding
import com.lpddr5.nzhaibao.logic.model.MyLike
import com.lpddr5.nzhaibao.logic.model.NineFiveMMWork
import com.lpddr5.nzhaibao.ui.workDisplay.WorkDisplayActivity

class HomeLikeAdapter(private val context: Context, private val workList: List<MyLike>) :
    RecyclerView.Adapter<HomeLikeAdapter.ViewHolder>() {

    inner class ViewHolder(binding: ItemWorkBinding) : RecyclerView.ViewHolder(binding.root) {
        val imageView: ImageView = binding.itemWorkImageView
        val textView: TextView = binding.itemWorkTextView
        val textViewPCount: TextView = binding.itemWorkPCount
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemWorkBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        val holder = ViewHolder(binding)
        holder.itemView.setOnClickListener {
            val position = holder.adapterPosition
            val work = workList[position]
            WorkDisplayActivity.startActivity(context, work)
        }
        return holder
    }

    override fun onBindViewHolder(holder: HomeLikeAdapter.ViewHolder, position: Int) {
        val work = workList[position]
        Glide.with(context).load(work.zeBeLike_imgUrl).into(holder.imageView)
        holder.textView.text = work.zeBeLike_title
        holder.textViewPCount.text = work.zeBeLike_p
    }

    override fun getItemCount() = workList.size
}