package com.lpddr5.nzhaibao.ui.home

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.ViewModel
import com.lpddr5.nzhaibao.logic.Repository
import com.lpddr5.nzhaibao.logic.model.MyLike
import com.lpddr5.nzhaibao.ui.ninefivemm.NineFiveMMAdapter

class HomeLikeViewModel : ViewModel() {

    var adapter: HomeLikeAdapter? = null

    var myLikeList = ArrayList<MyLike>()

    var email = ""

    private val myLikeLiveData = MutableLiveData<String>()
    val myLike = Transformations.switchMap(myLikeLiveData) {
        Repository.getMyLike(it)
    }

    fun getMyLike() {
        myLikeLiveData.value = email
    }
}