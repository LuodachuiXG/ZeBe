package com.lpddr5.nzhaibao.ui.workDisplay

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.ViewModel
import com.lpddr5.nzhaibao.logic.Repository
import com.lpddr5.nzhaibao.logic.model.NineFiveMMWork
import com.lpddr5.nzhaibao.ui.ninefivemm.NineFiveMMAdapter

class WorkDisplayViewModel : ViewModel() {

    // 对Work中所有网页地址进行保存
    var workUrls = ArrayList<String>()

    // 对Work中所有图片地址进行保存
    var imageUrls = ArrayList<String>()

    var adapter: WorkDisplayAdapter? = null

    private val searchWorkUrlLiveDate = MutableLiveData<String>()
    val workUrlsLiveData = Transformations.switchMap(searchWorkUrlLiveDate) {
        Repository.getWorkUrls(it)
    }

    private val searchImageUrlsLiveDate = MutableLiveData<ArrayList<String>>()
    val imageUrlsLiveData = Transformations.switchMap(searchImageUrlsLiveDate) {
        Repository.getImageUrlsByWorkUrls(it)
    }

    fun getWorkUrls(url: String) {
        searchWorkUrlLiveDate.value = url
    }

    fun getImageUrls(list: ArrayList<String>) {
        searchImageUrlsLiveDate.value = list
    }
}