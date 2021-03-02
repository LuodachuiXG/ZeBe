package com.lpddr5.nzhaibao.logic

import androidx.lifecycle.liveData
import com.lpddr5.nzhaibao.LogUtil
import com.lpddr5.nzhaibao.intermediate
import com.lpddr5.nzhaibao.logic.dao.ZeBeDao
import com.lpddr5.nzhaibao.logic.model.NineFiveMMWork
import com.lpddr5.nzhaibao.logic.network.ZeBeNetwork
import kotlinx.coroutines.Dispatchers
import org.jsoup.Jsoup
import java.lang.Exception
import java.lang.RuntimeException
import kotlin.coroutines.CoroutineContext

object Repository {

    fun setLastTimeRunningVersion() = ZeBeDao.setLastTimeRunningVersion()

    fun getLastTimeRunningVersion() = ZeBeDao.getLastTimeRunningVersion()

    fun getWorkListsByCategoryAndPage(category: String, page: Int) = fire(Dispatchers.IO) {
        val result = ZeBeNetwork.getWorkListsByCategoryAndPage(category, page)
        if (result != null) {
            val divs = result.toString().split("<div class=\"col-6 col-md-3 d-flex\">")
            val workList = ArrayList<NineFiveMMWork>()
            for (i in 1 until divs.size) {
                val div = Jsoup.parse(divs[i])
                val url = div.select("a").first().attr("href")
                val title = div.select("a").first().attr("title")
                val imgUrl = div.select("a").first().attr("data-bg")
                val p = div.select(".list-tag>span").text()
                val work = NineFiveMMWork(url, title, imgUrl, p)
                workList.add(work)
            }
            Result.success(workList)
        } else {
            Result.failure(RuntimeException("result is null or empty"))
        }
    }

    // 通过读取work页面上的（1/41）字段，来计算出每一张照片的网址
    fun getWorkUrls(url: String) = fire(Dispatchers.IO) {
        val list = ArrayList<String>()
        val source = ZeBeNetwork.getWebDocument(url)
        if (source.toString() != "") {
            val text = source.selectFirst("h1").text().intermediate("1/", "）")
            val oldUrl = url.intermediate("", ".html")
            for (i in 1 .. text.toInt()) {
                list.add("$oldUrl/$i.html")
            }
            Result.success(list)
        } else {
            Result.failure(RuntimeException("result is empty"))
        }

    }

    // 通过每张图片的网址，来获取图片地址列表
    fun getImageUrlsByWorkUrls(workUrls: ArrayList<String>) = fire(Dispatchers.IO) {
        val imageList = ArrayList<String>()
        for (i in 0 until workUrls.size) {
            val webString = ZeBeNetwork.getWebString(workUrls[i])
            val imageUrl = webString.intermediate("og:image\" content=\"", "\"")
            imageList.add(imageUrl)
        }
        Result.success(imageList)
    }

    // 获取最新版本信息
    fun getLatestVersion() = fire(Dispatchers.IO) {
        val versionResponse = ZeBeNetwork.getLatestVersion()
        if (versionResponse.id != -1) {
            Result.success(versionResponse)
        } else {
            Result.failure(RuntimeException("response is $versionResponse"))
        }
    }

    private fun <T> fire(context: CoroutineContext, block: suspend () -> Result<T>) = liveData<Result<T>>(context) {
        val result = try {
            block()
        } catch (e: Exception) {
            Result.failure<T>(e)
        }
        emit(result)
    }
}