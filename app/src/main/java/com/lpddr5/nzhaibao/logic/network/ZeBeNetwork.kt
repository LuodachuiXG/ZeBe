package com.lpddr5.nzhaibao.logic.network

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