package com.lpddr5.nzhaibao.ui.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.GridLayoutManager
import com.lpddr5.nzhaibao.LogUtil
import com.lpddr5.nzhaibao.R
import com.lpddr5.nzhaibao.ZeBeApplication
import com.lpddr5.nzhaibao.databinding.FragmentHomeLikeBinding
import com.lpddr5.nzhaibao.logic.Repository
import com.lpddr5.nzhaibao.tool.toast
import com.lpddr5.nzhaibao.ui.ninefivemm.NineFiveMMAdapter

class HomeLikeFragment : Fragment() {

    private val viewModel by lazy { ViewModelProvider(this).get(HomeLikeViewModel::class.java) }

    private var _binding: FragmentHomeLikeBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHomeLikeBinding.inflate(inflater, container, false)
        return binding.root
    }


    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        // 将登录用户的Email存到ViewModel中
        viewModel.email = Repository.getData("email")

        val layoutManager = GridLayoutManager(ZeBeApplication.context,2)
        binding.fragmentHomeLikeRecyclerView.layoutManager = layoutManager
        viewModel.adapter = HomeLikeAdapter(activity!!, viewModel.myLikeList)
        binding.fragmentHomeLikeRecyclerView.adapter = viewModel.adapter


        binding.fragmentHomeLikeSwipeRefresh.setColorSchemeResources(R.color.colorPrimary)
        binding.fragmentHomeLikeSwipeRefresh.setOnRefreshListener {
            refreshData()
        }

        viewModel.myLike.observe(viewLifecycleOwner) {
            val workList = it.getOrNull()
            if (workList != null && workList.isNotEmpty()) {
                viewModel.myLikeList.clear()
                viewModel.myLikeList.addAll(workList)
                viewModel.adapter?.notifyDataSetChanged()
            } else if (workList.isNullOrEmpty()){
                "你还没有喜欢呀".toast()
            } else {
                "加载失败".toast()
            }
            binding.fragmentHomeLikeSwipeRefresh.isRefreshing = false
        }
        refreshData()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun refreshData() {
        viewModel.getMyLike()
    }
}