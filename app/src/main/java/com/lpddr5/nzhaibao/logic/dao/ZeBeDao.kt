package com.lpddr5.nzhaibao.logic.dao

import android.content.Context
import androidx.core.content.edit
import com.lpddr5.nzhaibao.ZeBeApplication

object ZeBeDao {

    fun setLastTimeRunningVersion() {
        sharedPreferences().edit {
            putString("last_time_running_version", ZeBeApplication.getVersionName().toString())
        }
    }

    fun getLastTimeRunningVersion(): String {
        return sharedPreferences().getString("last_time_running_version", "0") ?: "0"
    }

    private fun sharedPreferences() = ZeBeApplication.context.getSharedPreferences("ZeBe", Context.MODE_PRIVATE)
}