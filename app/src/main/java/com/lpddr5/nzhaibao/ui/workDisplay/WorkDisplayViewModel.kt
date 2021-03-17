package com.lpddr5.nzhaibao.ui.workDisplay

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.ViewModel
import com.lpddr5.nzhaibao.logic.Repository
import com.lpddr5.nzhaibao.logic.model.MyLike
import com.lpddr5.nzhaibao.logic.model.NineFiveMMWork
import com.lpddr5.nzhaibao.ui.ninefivemm.NineFiveMMAdapter

class WorkDisplayViewModel : ViewModel() {

    // 对Work中所有网页地址进行保存
    var workUrls = ArrayList<String>()

    // 对Work中所有图片地址进行保存
    var imageUrls = ArrayList<String>()

    var adapter: WorkDisplayAdapter? = null

    // 当前用户email
    var email = ""

    // 存储相册集标题、封面图、相册集地址等
    // 因为Intent传过来的work可能是NiveFiveMMWork或MyLike，需要转换后取出需要用到的变量
    var mTitle = ""
    var imgUrl = ""
    var url = ""
    var p = ""

    // like按钮的Flag，在向服务器检索当前相册集是否喜欢完成之前点击like按钮不进行操作
    var flagLikeBtn = 0

    // 标记当前相册集是否“已喜欢”
    var flagLike = 0

    // 通过相册集URL，读取页面上的（1/41）字段，来计算出每一张照片的网址
    private val searchWorkUrlLiveData = MutableLiveData<String>()
    val workUrlsLiveData = Transformations.switchMap(searchWorkUrlLiveData) {
        Repository.getWorkUrls(it)
    }

    // 通过每张图片的网址，来获取图片地址列表
    private val searchImageUrlsLiveData = MutableLiveData<ArrayList<String>>()
    val imageUrlsLiveData = Transformations.switchMap(searchImageUrlsLiveData) {
        Repository.getImageUrlsByWorkUrls(it)
    }

    // 通过相册集URL和邮箱来判断当前相册集是否已经喜欢
    private val isLikeLiveData = MutableLiveData<Map<String,String>>()
    val isLike = Transformations.switchMap(isLikeLiveData) {
        Repository.isLikeByUrlAndEmail(it)
    }

    // 通过相册集URL和邮箱来判断当前相册集是否已经喜欢
    private val addLikeLiveData = MutableLiveData<MyLike>()
    val addLike = Transformations.switchMap(addLikeLiveData) {
        Repository.addMyLike(it)
    }

    // 通过相册集URL和邮箱来删除“我的喜欢”
    private val removeLikeLiveData = MutableLiveData<Map<String,String>>()
    val removeLike = Transformations.switchMap(removeLikeLiveData) {
        Repository.removeMyLike(it)
    }

    fun getWorkUrls(url: String) {
        searchWorkUrlLiveData.value = url
    }

    fun getImageUrls(list: ArrayList<String>) {
        searchImageUrlsLiveData.value = list
    }

    fun getIsLike(url: String, email: String) {
        val map = mapOf("url" to url, "email" to email)
        isLikeLiveData.value = map
    }

    fun addMyLike(url: String, title: String, imgUrl: String,
                    p: String, type: String, email: String) {
        val myLike = MyLike(0, url, title, imgUrl, p, type, email)
        addLikeLiveData.value = myLike
    }

    fun removeMyLike(url: String, email: String) {
        val map = mapOf("url" to url, "email" to email)
        removeLikeLiveData.value = map
    }
}