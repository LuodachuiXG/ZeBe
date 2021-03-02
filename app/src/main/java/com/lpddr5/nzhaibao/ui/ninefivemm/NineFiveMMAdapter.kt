package com.lpddr5.nzhaibao.ui.ninefivemm

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.lpddr5.nzhaibao.databinding.ItemWorkBinding
import com.lpddr5.nzhaibao.logic.model.NineFiveMMWork
import com.lpddr5.nzhaibao.ui.workDisplay.WorkDisplayActivity

class NineFiveMMAdapter(private val context: Context, private val workList: List<NineFiveMMWork>) :
    RecyclerView.Adapter<NineFiveMMAdapter.ViewHolder>() {

    private val FOOT_VIEW = 1
    private val NORMAL_VIEW = 0
    private val FOOT_START = 2
    private val FOOT_END = 3
    private val FOOT_NO_MORE = 4
    private var footState = 0

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

    override fun onBindViewHolder(holder: NineFiveMMAdapter.ViewHolder, position: Int) {
        val work = workList[position]
        Glide.with(context).load(work.imgUrl).into(holder.imageView)
        holder.textView.text = work.title
        holder.textViewPCount.text = work.p
    }

    override fun getItemCount() = workList.size

    // 设置不同的 footState
    fun setFootState(state: Int) {
        footState = state
    }
}