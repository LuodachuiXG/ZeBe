package com.lpddr5.nzhaibao.ui.ninefivemm

import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.lpddr5.nzhaibao.R
import com.lpddr5.nzhaibao.ZeBeApplication
import com.lpddr5.nzhaibao.databinding.FragmentNinefivemmBinding
import com.lpddr5.nzhaibao.toast
import java.util.*

class NineFiveMMFragment() : Fragment() {

    private val viewModel by lazy { ViewModelProvider(this).get(NineFiveMMViewModel::class.java) }

    private var _binding: FragmentNinefivemmBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentNinefivemmBinding.inflate(inflater, container, false)
        ZeBeApplication.nineFiveMMViewModel = viewModel
        return binding.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        val layoutManager = GridLayoutManager(ZeBeApplication.context,2)
        binding.recyclerView.layoutManager = layoutManager
        viewModel.adapter = NineFiveMMAdapter(activity!!, viewModel.workList)
        binding.recyclerView.adapter = viewModel.adapter

        binding.recyclerView.addOnScrollListener(object : RecyclerOnScrollListener() {
            override fun loadMore() {
                // 设置flag = 1，向上滑动，监听事件会继续触发，但是不会继续加载数据
                setFlag(1)
                // 设置 FootView 的初始状态
                viewModel.adapter!!.setFootState(2)

                Timer().schedule(object : TimerTask() {
                    override fun run() {
                        activity!!.runOnUiThread {
                            viewModel.currentIndex = viewModel.currentIndex + 1
                            refreshData(viewModel.currentIndex)
                            viewModel.adapter!!.setFootState(3)
                            setFlag(0) // 此次数据加载完毕，设置flag = 0，以便下次数据可以加载
                        }
                    }
                }, 200)
            }
        })


        binding.swipeRefresh.setColorSchemeResources(R.color.colorPrimary)
        binding.swipeRefresh.setOnRefreshListener {
            viewModel.currentIndex = 1
            viewModel.workList.clear()
            viewModel.adapter?.notifyDataSetChanged()
            refreshData(viewModel.currentIndex)
        }

        viewModel.workListsLiveData.observe(viewLifecycleOwner) {
            val workList = it.getOrNull()
            if (workList != null && workList.size >= 1) {
                viewModel.workList.addAll(workList)
                viewModel.adapter?.notifyItemRangeChanged(1, viewModel.workList.size)
                activity!!.title =
                    ZeBeApplication.currentCategory + " (${viewModel.workList.size}/第${viewModel.currentIndex}页)"
            } else {
                "已加载全部".toast()
                viewModel.adapter!!.setFootState(4) // 没有更多数据加载了
                it.exceptionOrNull()?.printStackTrace()
            }
            binding.swipeRefresh.isRefreshing = false
        }
        refreshData()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }


    fun refreshData(page: Int = 1) {
        viewModel.getWorkListsByCategoryAndPage(ZeBeApplication.currentCategory, page)
    }
}



