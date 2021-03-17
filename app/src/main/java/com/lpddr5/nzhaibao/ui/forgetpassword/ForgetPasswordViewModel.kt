package com.lpddr5.nzhaibao.ui.forgetpassword

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.ViewModel
import com.lpddr5.nzhaibao.logic.Repository
import com.lpddr5.nzhaibao.tool.md5

class ForgetPasswordViewModel : ViewModel() {

    private val sendCodeLiveData = MutableLiveData<Map<String, String>>()
    val sendCode = Transformations.switchMap(sendCodeLiveData) {
        Repository.sendCode(it)
    }

    private val updatePasswordLiveData = MutableLiveData<Map<String, String>>()
    val updatePassword = Transformations.switchMap(updatePasswordLiveData) {
        Repository.updatePassword(it)
    }

    fun sendCode(email: String) {
        val map = mapOf("email" to email, "type" to "1")
        sendCodeLiveData.value = map
    }

    fun updatePassword(email: String, password: String, code: String) {
        val map = mapOf("email" to email, "password" to md5(password), "code" to code)
        updatePasswordLiveData.value = map
    }
}