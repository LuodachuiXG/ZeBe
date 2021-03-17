package com.lpddr5.nzhaibao.logic.dao

import android.content.Context
import androidx.core.content.edit
import com.lpddr5.nzhaibao.ZeBeApplication
import com.lpddr5.nzhaibao.tool.aesDec
import com.lpddr5.nzhaibao.tool.aesEnc
import org.json.JSONObject
import java.lang.Exception

object ZeBeDao {

    // 设置最后运行软件时版本
    fun setLastTimeRunningVersion() {
        sharedPreferences().edit {
            putString("last_time_running_version", ZeBeApplication.getVersionName().toString())
        }
    }

    // 获取最后运行软件时版本
    fun getLastTimeRunningVersion(): String {
        return sharedPreferences().getString("last_time_running_version", "0") ?: "0"
    }

    // 设置当前验证码倒计时,type=0代表将注册邮箱设置，type=1代表将忘记密码的邮箱设置
    fun setCurrentCodeCountDown(count: Int, email: String, type: Int) {
        sharedPreferences().edit {
            putInt("countDown", count)
        }
        val key = if (type == 0) "countDownEmailSignUp" else "countDownEmailForgetPassword"
        if (count <= 0) {
            sharedPreferences().edit {
                putString(key, "")
            }

        } else {
            sharedPreferences().edit {
                putString(key, email)
            }
        }
    }

    // 获取当前验证码倒计时
    fun getCurrentCodeCountDown(): Int {
        return sharedPreferences().getInt("countDown", 0)
    }

    // 获取当前验证码倒计时的触发注册邮箱
    fun getCurrentCodeCountDownEmailBySignUp(): String {
        return sharedPreferences().getString("countDownEmailSignUp", "") ?: ""
    }

    // 获取当前验证码倒计时的触发忘记密码邮箱
    fun getCurrentCodeCountDownEmailByForgetPassword(): String {
        return sharedPreferences().getString("countDownEmailForgetPassword", "") ?: ""
    }

    // 将当前验证码倒计时和触发邮箱清空，type=0为注册，type=1为忘记密码
    fun clearCurrentCodeCountDownAndEmail(type: Int) {
        val key = if (type == 0) "countDownEmailSignUp" else "countDownEmailForgetPassword"
        sharedPreferences().edit {
            putString(key, "")
            putInt("countDown", 0)
        }
    }

    // 将自定义数据保存到本地
    fun setData(key: String, value: String) {
        try {
            val mData = sharedPreferences().getString("data", "")
            if (mData.isNullOrEmpty()) {
                // 在当前设备上第一次调用。新建JSON对象将数据导入后保存到sp
                val json = JSONObject()
                json.put(key, value)
                sharedPreferences().edit {
                    putString("data", aesEnc(json.toString()))
                }
            } else {
                // 在当前设备上不是第一次调用
                val json = JSONObject(aesDec(mData))
                if (json.has(key)) {
                    // 当前key存在
                    json.remove(key)
                    json.put(key, value)
                } else {
                    // 当前key不存在
                    json.put(key, value)
                }
                sharedPreferences().edit {
                    putString("data",  aesEnc(json.toString()))
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }

    }

    // 读取自定义数据
    fun getData(key: String): String {
        try {
            val mData = sharedPreferences().getString("data", "")
            return if (mData.isNullOrEmpty()) {
                // data什么数据都没有，返回空文本
                ""
            } else {
                val json = JSONObject(aesDec(mData))
                if (json.has(key)) {
                    json.getString(key)
                } else {
                    // key不存在，返回空文本
                    ""
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
            return ""
        }
    }

    private fun sharedPreferences() = ZeBeApplication.context.getSharedPreferences("ZeBe", Context.MODE_PRIVATE)
}