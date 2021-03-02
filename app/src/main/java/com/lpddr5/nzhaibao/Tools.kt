package com.lpddr5.nzhaibao

import android.annotation.SuppressLint
import android.content.pm.PackageInfo
import android.content.pm.PackageManager
import android.widget.Toast


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
