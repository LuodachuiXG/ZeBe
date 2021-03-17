package com.lpddr5.nzhaibao.logic.network

import com.lpddr5.nzhaibao.logic.model.User
import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlin.coroutines.suspendCoroutine

object ZeBeNetwork {

    // 服务器通信网络接口
    private val serverService = ServiceCreator.create<ServerService>()

    private const val NINE_FIVE_URL = "https://www.95mm.net/"

    // 获取作品列表，通过种类和页数
    fun getWorkListsByCategoryAndPage(category: String, page: Int): Document? {
       return Jsoup.connect(NINE_FIVE_URL + "home-ajax/index.html?tabcid=$category&page=$page").get()
    }

    // 获取网页源码
    fun getWebDocument(url: String): Document{
        return Jsoup.connect(url).get()
    }

    // 获取网页源码
    fun getWebString(url: String): String{
        return Jsoup.connect(url).get().toString()
    }

    // 获取最新版本信息
    suspend fun getLatestVersion() = serverService.getLatestVersion().await()

    // 登录账号，登录成功返回用户User对象
    suspend fun loginUser(map: Map<String, String>) =
        serverService.loginUser(map["email"].toString(), map["password"].toString()).await()

    // 发送验证码，0注册，1忘记
    suspend fun sendCode(map: Map<String, String>) =
        serverService.sendCode(map["email"].toString(), map["type"].toString()).await()

    // 注册账号，成功返回1，验证码错误返回-2，其他错误返回-1
    suspend fun signUpUser(map: Map<String, String>) =
        serverService.signUpUser(map["name"].toString(), map["password"].toString(),
            map["email"].toString(), map["code"].toString()).await()

    // 更改密码，成功返回1，验证码错误返回-2，其他错误返回-1
    suspend fun updatePassword(map: Map<String, String>) =
        serverService.updatePassword(map["email"].toString(), map["password"].toString(), map["code"].toString()).await()

    // 通过邮箱获取用户’我的喜欢‘
    suspend fun getMyLikeByEmail(email: String) =
        serverService.getMyLikeByEmail(email).await()

    // 通过邮箱添加用户’我的喜欢‘
    suspend fun addMyLikeByEmail(url: String, title: String, imgUrl: String,
                                 p: String, type: String, email: String) =
        serverService.addMyLikeByEmail(url, title, imgUrl, p, type, email).await()

    // 通过邮箱和URL删除用户’我的喜欢‘
    suspend fun removeMyLikeByUrlAndEmail(id: String, email: String) =
        serverService.removeMyLikeByUrlAndEmail(id, email).await()

    // 通过相册集URL和邮箱，判断当前相册集是否在邮箱用户的’我的喜欢‘中。
    suspend fun isLikeByUrlAndEmail(url: String, email: String) =
        serverService.isLikeByUrlAndEmail(url, email).await()

    private suspend fun <T> Call<T>.await(): T {
        return suspendCoroutine { continuation ->
            enqueue(object : Callback<T> {
                override fun onResponse(call: Call<T>, response: Response<T>) {
                    val body = response.body()
                    if (body != null) continuation.resume(body)
                    else continuation.resumeWithException(
                        RuntimeException("response body is null"))
                }

                override fun onFailure(call: Call<T>, t: Throwable) {
                    continuation.resumeWithException(t)
                }
            })
        }
    }
}