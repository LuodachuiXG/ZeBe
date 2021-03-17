package com.lpddr5.nzhaibao.ui.login

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.ViewModel
import com.lpddr5.nzhaibao.logic.Repository
import com.lpddr5.nzhaibao.logic.model.User
import com.lpddr5.nzhaibao.tool.md5

class LoginViewModel : ViewModel() {

    private val loginLiveData = MutableLiveData<Map<String, String>>()
    val userLoginLiveData = Transformations.switchMap(loginLiveData) {
        Repository.loginUser(it)
    }

    fun loginUser(account: String, password: String) {
        val map = mapOf("email" to account, "password" to md5(password))
        loginLiveData.value = map
    }
}