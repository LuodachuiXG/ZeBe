package com.lpddr5.nzhaibao.ui.ninefivemm

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.ViewModel
import com.lpddr5.nzhaibao.LogUtil
import com.lpddr5.nzhaibao.logic.Repository
import com.lpddr5.nzhaibao.logic.model.NineFiveMMWork

class NineFiveMMViewModel : ViewModel() {

    companion object {
        const val HOT = "热门"
        const val NEWEST = "最新"
        const val CAMPUS_BELLE = "校花"
        const val STAR = "明星"
        const val SEN_XI = "森系"
        const val PURE = "清纯"
        const val TENDER_MODEL = "嫩模"
        const val GIRL = "少女"
    }

    private val searchLiveDate = MutableLiveData<Map<String,String>>()
    val workListsLiveData = Transformations.switchMap(searchLiveDate) {
        Repository.getWorkListsByCategoryAndPage(it["category"].toString(), it["page"].toString().toInt())
    }

    // 对界面上显示的Work数据进行缓存
    val workList = ArrayList<NineFiveMMWork>()

    // 当前页数
    var currentIndex = 1

    var adapter: NineFiveMMAdapter? = null

    fun getWorkListsByCategoryAndPage(category: String, page: Int) {
        val map = mapOf("category" to category, "page" to page.toString())
        searchLiveDate.value = map
    }

    fun gotoPage(category: String, page: Int) {
        workList.clear()
        adapter?.notifyDataSetChanged()
        val map = mapOf("category" to category, "page" to page.toString())
        searchLiveDate.value = map
    }

    override fun onCleared() {
        super.onCleared()
    }
}