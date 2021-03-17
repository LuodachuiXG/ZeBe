package com.lpddr5.nzhaibao.ui.signup

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.ViewModel
import com.lpddr5.nzhaibao.logic.Repository
import com.lpddr5.nzhaibao.tool.md5

class SignUpViewModel : ViewModel() {

    private val sendCodeLiveData = MutableLiveData<Map<String, String>>()
    val sendCode = Transformations.switchMap(sendCodeLiveData) {
        Repository.sendCode(it)
    }

    private val signUpUserLiveData = MutableLiveData<Map<String, String>>()
    val signUpUser = Transformations.switchMap(signUpUserLiveData) {
        Repository.signUpUser(it)
    }

    fun sendCode(email: String) {
        val map = mapOf("email" to email, "type" to "0")
        sendCodeLiveData.value = map
    }

    fun signUpUser(name: String, password: String, email: String, code: String) {
        val map = mapOf("name" to name, "password" to md5(password), "email" to email, "code" to code)
        signUpUserLiveData.value = map
    }
}