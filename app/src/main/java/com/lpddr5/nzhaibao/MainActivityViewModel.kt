package com.lpddr5.nzhaibao

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.ViewModel
import com.lpddr5.nzhaibao.logic.Repository
import com.lpddr5.nzhaibao.logic.model.LatestVersion

class MainActivityViewModel : ViewModel() {

    private val searchLatestVersionLiveDate = MutableLiveData<LatestVersion>()
    val latestVersionLiveData = Transformations.switchMap(searchLatestVersionLiveDate) {
        Repository.getLatestVersion()
    }

    fun getLatestVersion() {
        searchLatestVersionLiveDate.value = searchLatestVersionLiveDate.value
    }
}