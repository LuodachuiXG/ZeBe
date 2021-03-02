package com.lpddr5.nzhaibao.ui.ninefivemm

import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

abstract class RecyclerOnScrollListener : RecyclerView.OnScrollListener() {

    private var flag = 0

    override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
        super.onScrollStateChanged(recyclerView, newState)
        val layout = recyclerView.layoutManager as LinearLayoutManager
        val lastPositionCompletely = layout.findLastCompletelyVisibleItemPosition()
        if (lastPositionCompletely == layout.itemCount - 1 && flag == 0) {
            loadMore()
        }
    }

    abstract fun loadMore()

    // 设置标记防止多次向上滑动，多次调用loadMore()
    fun setFlag(flag: Int) {
        this.flag = flag
    }
}