package com.lpddr5.nzhaibao.tool

import android.annotation.SuppressLint
import android.widget.Toast
import com.lpddr5.nzhaibao.ZeBeApplication
import java.security.MessageDigest
import java.security.NoSuchAlgorithmException


@SuppressLint("ShowToast")
fun String.toast(duration: Int = Toast.LENGTH_SHORT) {
    Toast.makeText(ZeBeApplication.context, this, duration).show()
}

@SuppressLint("ShowToast")
fun Int.toast(duration: Int = Toast.LENGTH_SHORT) {
    Toast.makeText(ZeBeApplication.context, this.toString(), duration).show()
}

fun String.intermediate(firstText: String, lastText: String): String {
    var firstIndex = 0
    var lastIndex = 0
    return if (this != "") {
        firstIndex = this.indexOf(firstText) + firstText.length
        lastIndex = if (lastText == "") {
            this.substring(firstIndex).length + firstIndex
        } else {
            this.indexOf(lastText, firstIndex)
        }
        this.substring(firstIndex, lastIndex)
    } else {
        ""
    }
}

fun md5(string: String): String {
    try {
        val instance:MessageDigest = MessageDigest.getInstance("MD5")//获取md5加密对象
        val digest:ByteArray = instance.digest(string.toByteArray())//对字符串加密，返回字节数组
        val sb = StringBuffer()
        for (b in digest) {
            val i :Int = b.toInt() and 0xff//获取低八位有效值
            var hexString = Integer.toHexString(i)//将整数转化为16进制
            if (hexString.length < 2) {
                hexString = "0$hexString"//如果是一位的话，补0
            }
            sb.append(hexString)
        }
        return sb.toString()

    } catch (e: NoSuchAlgorithmException) {
        e.printStackTrace()
    }
    return ""
}
