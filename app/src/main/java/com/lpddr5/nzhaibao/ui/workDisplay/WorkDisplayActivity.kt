package com.lpddr5.nzhaibao.ui.workDisplay

import android.content.Context
import android.content.Intent
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.os.Parcelable
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
import com.lpddr5.nzhaibao.R
import com.lpddr5.nzhaibao.ZeBeApplication
import com.lpddr5.nzhaibao.ZeBeApplication.Companion.context
import com.lpddr5.nzhaibao.databinding.ActivityWorkDisplayBinding
import com.lpddr5.nzhaibao.logic.Repository
import com.lpddr5.nzhaibao.logic.model.MyLike
import com.lpddr5.nzhaibao.logic.model.NineFiveMMWork
import com.lpddr5.nzhaibao.tool.toast
import com.lpddr5.nzhaibao.ui.login.LoginActivity


class WorkDisplayActivity : AppCompatActivity() {

    private val viewModel by lazy { ViewModelProvider(this).get(WorkDisplayViewModel::class.java) }

    private lateinit var binding: ActivityWorkDisplayBinding

    private lateinit var work: Parcelable

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityWorkDisplayBinding.inflate(layoutInflater)
        setContentView(binding.root)
        work = intent.getParcelableExtra("work")!!

        // 判断当前work变量是NineFiveMMWork还是MyLike
        if (work is NineFiveMMWork) {
            val mWork = work as NineFiveMMWork
            viewModel.mTitle = mWork.title
            viewModel.imgUrl = mWork.imgUrl
            viewModel.url = mWork.url
            viewModel.p = mWork.p
        } else if (work is MyLike) {
            val mWork = work as MyLike
            viewModel.mTitle = mWork.zeBeLike_title
            viewModel.imgUrl = mWork.zeBeLike_imgUrl
            viewModel.url = mWork.zeBeLike_url
            viewModel.p = mWork.zeBeLike_p
        }

        setSupportActionBar(binding.workDisplayToolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        binding.collapsingToolbar.title = viewModel.mTitle

        // Like按钮单击事件
        binding.workDisplayLike.setOnClickListener {
            // 在向服务器检索当前相册集是否已经喜欢完成之前点击like按钮不进行操作
            if (viewModel.flagLikeBtn != 0) {
                LogUtil.e(this, viewModel.email)
                if (viewModel.email == "") {
                    "请先登录".toast()
                    LoginActivity.startActivity(this)
                    return@setOnClickListener
                }

                when (viewModel.flagLike) {
                    0 -> {
                        // 添加喜欢
                        viewModel.addMyLike(viewModel.url, viewModel.mTitle,
                            viewModel.imgUrl, viewModel.p,
                            ZeBeApplication.NINE_FIVE_MM, viewModel.email)
                    }
                    1 -> {
                        // 取消喜欢
                        viewModel.removeMyLike(viewModel.url, viewModel.email)
                    }
                }
            }
        }

        // 加载顶部相册集封面图片
        Glide.with(this).load(viewModel.imgUrl).into(object : CustomTarget<Drawable>(){
            override fun onResourceReady(resource: Drawable, transition: Transition<in Drawable>?) {
                binding.collapsingToolbar.background = resource
            }

            override fun onLoadCleared(placeholder: Drawable?) {}
        })

        val layoutManager = LinearLayoutManager(this)
        binding.recyclerView.layoutManager = layoutManager
        viewModel.adapter = WorkDisplayAdapter(this, viewModel.imageUrls)
        binding.recyclerView.adapter = viewModel.adapter

        // 通过相册集URL，读取页面上的（1/41）字段，来计算出每一张照片的网址观察
        viewModel.workUrlsLiveData.observe(this, {
            val list = it.getOrNull()
            if (list != null && list.size >= 1) {
                viewModel.workUrls = list
                viewModel.getImageUrls(list)
//                "正在加载，请稍等，后续将会优化速度".toast(Toast.LENGTH_LONG)
            } else {
                "加载失败".toast()
            }
        })

        // 通过每张图片的网址，来获取图片地址列表观察
        viewModel.imageUrlsLiveData.observe(this, {
            val list = it.getOrNull()
            if (list != null && list.size >= 1) {
                viewModel.imageUrls.clear()
                viewModel.imageUrls.addAll(list)
                LogUtil.v(this, viewModel.imageUrls.toString())
                viewModel.adapter?.notifyDataSetChanged()
                binding.collapsingToolbar.title = viewModel.mTitle + "(${viewModel.imageUrls.size})"
            } else {
                "加载失败".toast()
            }
        })

        // 判断当前相册集是否喜欢观察
        viewModel.isLike.observe(this) {
            val serviceResult = it.getOrNull()
            if (serviceResult != null && serviceResult.result != "-1") {
                // 已经喜欢，切换Like按钮图标
                binding.workDisplayLike.setImageResource(R.drawable.ic_like)
                viewModel.flagLike = 1
            }
            viewModel.flagLikeBtn = 1
        }

        // 添加喜欢事件观察
        viewModel.addLike.observe(this) {
            val serviceResult = it.getOrNull()
            if (serviceResult != null && serviceResult.result.toInt() >= 1) {
                // 添加喜欢成功
                binding.workDisplayLike.setImageResource(R.drawable.ic_like)
                viewModel.flagLike = 1
            } else {
                "操作失败".toast()
            }
        }

        // 取消喜欢事件观察
        viewModel.removeLike.observe(this) {
            val serviceResult = it.getOrNull()
            if (serviceResult != null && serviceResult.result.toInt() >= 1) {
                // 取消喜欢成功
                binding.workDisplayLike.setImageResource(R.drawable.ic_like_gone)
                viewModel.flagLike = 0
            } else {
                "操作失败".toast()
            }
        }

        // 通过URL加载图片
        viewModel.getWorkUrls(viewModel.url)

        // 通过URL和邮箱判断当前相册集是否已经喜欢
        viewModel.getIsLike(viewModel.url, Repository.getData("email"))
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

    override fun onResume() {
        super.onResume()
        // 在onResume中刷新email，因为用户可能在点击like后跳转登录页登录并返回
        viewModel.email = Repository.getData("email")
    }

    companion object {
        fun startActivity(context: Context, work: Parcelable) {
            val intent = Intent(context, WorkDisplayActivity::class.java)
            intent.putExtra("work", work)
            context.startActivity(intent)
        }
    }
}