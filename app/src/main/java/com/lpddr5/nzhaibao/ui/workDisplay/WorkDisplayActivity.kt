package com.lpddr5.nzhaibao.ui.workDisplay

import android.content.Context
import android.content.Intent
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.view.MenuItem
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.bumptech.glide.request.target.CustomTarget
import com.bumptech.glide.request.target.SimpleTarget
import com.bumptech.glide.request.transition.Transition
import com.lpddr5.nzhaibao.LogUtil
import com.lpddr5.nzhaibao.ZeBeApplication
import com.lpddr5.nzhaibao.ZeBeApplication.Companion.context
import com.lpddr5.nzhaibao.databinding.ActivityWorkDisplayBinding
import com.lpddr5.nzhaibao.logic.model.NineFiveMMWork
import com.lpddr5.nzhaibao.toast


class WorkDisplayActivity : AppCompatActivity() {

    private val viewModel by lazy { ViewModelProvider(this).get(WorkDisplayViewModel::class.java) }

    private lateinit var binding: ActivityWorkDisplayBinding

    private lateinit var work: NineFiveMMWork

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityWorkDisplayBinding.inflate(layoutInflater)
        setContentView(binding.root)
        work = intent.getParcelableExtra("work")!!


        setSupportActionBar(binding.workDisplayToolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        binding.collapsingToolbar.title = work.title

        binding.workDisplayLike.setOnClickListener {
            "Like...开发中".toast()
        }

        Glide.with(this).load(work.imgUrl).into(object : CustomTarget<Drawable>(){
            override fun onResourceReady(resource: Drawable, transition: Transition<in Drawable>?) {
                binding.collapsingToolbar.background = resource
            }

            override fun onLoadCleared(placeholder: Drawable?) {}
        })

        val layoutManager = LinearLayoutManager(this)
        binding.recyclerView.layoutManager = layoutManager
        viewModel.adapter = WorkDisplayAdapter(this, viewModel.imageUrls)
        binding.recyclerView.adapter = viewModel.adapter

        viewModel.workUrlsLiveData.observe(this, {
            val list = it.getOrNull()
            if (list != null && list.size >= 1) {
                viewModel.workUrls = list
                viewModel.getImageUrls(list)
                "正在加载，请稍等，后续将会优化速度".toast(Toast.LENGTH_LONG)
            } else {
                "加载失败".toast()
            }
        })
        viewModel.imageUrlsLiveData.observe(this, {
            val list = it.getOrNull()
            if (list != null && list.size >= 1) {
                viewModel.imageUrls.clear()
                viewModel.imageUrls.addAll(list)
                LogUtil.e(this, viewModel.imageUrls.toString())
                viewModel.adapter?.notifyDataSetChanged()
                binding.collapsingToolbar.title = work.title + "(${viewModel.imageUrls.size})"
            } else {
                "加载失败".toast()
            }
        })

        viewModel.getWorkUrls(work.url)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> {
                finish()
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }

    companion object {
        fun startActivity(context: Context, nineFiveMMWork: NineFiveMMWork) {
            val intent = Intent(context, WorkDisplayActivity::class.java)
            intent.putExtra("work", nineFiveMMWork)
            context.startActivity(intent)
        }
    }
}