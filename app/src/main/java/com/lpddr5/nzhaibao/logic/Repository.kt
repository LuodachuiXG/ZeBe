package com.lpddr5.nzhaibao.logic

import androidx.lifecycle.liveData
import com.lpddr5.nzhaibao.LogUtil
import com.lpddr5.nzhaibao.tool.intermediate
import com.lpddr5.nzhaibao.logic.dao.ZeBeDao
import com.lpddr5.nzhaibao.logic.model.MyLike
import com.lpddr5.nzhaibao.logic.model.NineFiveMMWork
import com.lpddr5.nzhaibao.logic.model.User
import com.lpddr5.nzhaibao.logic.network.ZeBeNetwork
import kotlinx.coroutines.Dispatchers
import org.jsoup.Jsoup
import java.lang.Exception
import java.lang.RuntimeException
import kotlin.coroutines.CoroutineContext

object Repository {

    /**
     * 设置最后运行软件时版本
     */
    fun setLastTimeRunningVersion() = ZeBeDao.setLastTimeRunningVersion()

    /**
     * 获取最后运行软件时版本
     */
    fun getLastTimeRunningVersion() = ZeBeDao.getLastTimeRunningVersion()

    /**
     * 设置当前验证码倒计时,type=0代表将注册邮箱设置，type=1代表将忘记密码的邮箱设置
     */
    fun setCurrentCodeCountDown(count: Int, email: String, type: Int) = ZeBeDao.setCurrentCodeCountDown(count, email, type)

    /**
     * 获取当前验证码倒计时
     */
    fun getCurrentCodeCountDown() = ZeBeDao.getCurrentCodeCountDown()

    /**
     * 获取当前验证码倒计时的触发注册邮箱
     */
    fun getCurrentCodeCountDownEmailBySignUp() = ZeBeDao.getCurrentCodeCountDownEmailBySignUp()

    /**
     * 获取当前验证码倒计时的触发忘记密码邮箱
     */
    fun getCurrentCodeCountDownEmailByForgetPassword() = ZeBeDao.getCurrentCodeCountDownEmailByForgetPassword()

    /**
     * 将当前验证码倒计时和触发邮箱清空，type=0为注册，type=1为忘记密码
     */
    fun clearCurrentCodeCountDownAndEmail(type: Int) = ZeBeDao.clearCurrentCodeCountDownAndEmail(type)

    /**
     * 将自定义数据保存到本地
     */
    fun setData(key: String, value: String) = ZeBeDao.setData(key, value)

    /**
     * 读取自定义数据
     */
    fun getData(key: String) = ZeBeDao.getData(key)

    /**
     * 通过类型和页数，获取95MM的专辑集合
     */
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

    /**
     * 通过读取work页面上的（1/41）字段，来计算出每一张照片的网址
     */
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

    /**
     * 通过每张图片的网址，来获取图片地址列表
     */
    fun getImageUrlsByWorkUrls(workUrls: ArrayList<String>) = fire(Dispatchers.IO) {
        val imageList = ArrayList<String>()
        for (i in 0 until workUrls.size) {
            val webString = ZeBeNetwork.getWebString(workUrls[i])
            val imageUrl = webString.intermediate("og:image\" content=\"", "\"")
            imageList.add(imageUrl)
        }
        Result.success(imageList)
    }

    /**
     * 获取最新版本信息
     */
    fun getLatestVersion() = fire(Dispatchers.IO) {
        val versionResponse = ZeBeNetwork.getLatestVersion()
        if (versionResponse.id != -1) {
            Result.success(versionResponse)
        } else {
            Result.failure(RuntimeException("response is $versionResponse"))
        }
    }

    /**
     * 登录账号，登录成功返回用户User对象，失败返回id为-1的User对象
     */
    fun loginUser(map: Map<String, String>) = fire(Dispatchers.IO) {
        val user: User = ZeBeNetwork.loginUser(map)
        if (user.zeBeUser_id != -1) {
            Result.success(user)
        } else {
            Result.failure(RuntimeException("user does not exist"))
        }
    }

    /**
     * 发送验证码，0注册，1忘记
     */
    fun sendCode(map: Map<String, String>) = fire(Dispatchers.IO) {
        val serviceResult = ZeBeNetwork.sendCode(map)
        if (serviceResult.request == "sendCode") {
            Result.success(serviceResult)
        } else {
            Result.failure(RuntimeException("request is not sendCode"))
        }
    }

    /**
     * 注册账号，成功返回1，验证码错误返回-2，其他错误返回-1
     */
    fun signUpUser(map: Map<String, String>) = fire(Dispatchers.IO) {
        val serviceResult = ZeBeNetwork.signUpUser(map)
        if (serviceResult.request == "signUpUser") {
            Result.success(serviceResult)
        } else {
            Result.failure(RuntimeException("request is not signUpUser"))
        }

    }

    /**
     * 更改密码，成功返回1，验证码错误返回-2，其他错误返回-1
     */
    fun updatePassword(map: Map<String, String>) = fire(Dispatchers.IO) {
        val serviceResult = ZeBeNetwork.updatePassword(map)
        if (serviceResult.request == "updatePassword") {
            Result.success(serviceResult)
        } else {
            Result.failure(RuntimeException("request is not updatePassword"))
        }

    }

    /**
     * 通过邮箱获取用户’我的喜欢‘
     */
    fun getMyLike(email: String) = fire(Dispatchers.IO) {
        val myLikes = ZeBeNetwork.getMyLikeByEmail(email)
        Result.success(myLikes)
    }

    /**
     * 通过邮箱添加用户’我的喜欢‘
     */
    fun addMyLike(myLike: MyLike) = fire(Dispatchers.IO) {
        val url = myLike.zeBeLike_url
        val title = myLike.zeBeLike_title
        val imgUrl = myLike.zeBeLike_imgUrl
        val p = myLike.zeBeLike_p
        val type = myLike.zeBeLike_type
        val email = myLike.zeBeLike_email

        val serviceResult = ZeBeNetwork.addMyLikeByEmail(url, title, imgUrl, p, type, email)
        if (serviceResult.request == "addMyLikeByEmail") {
            Result.success(serviceResult)
        } else {
            Result.failure(RuntimeException("request is not addMyLikeByEmail"))
        }
    }

    /**
     * 通过邮箱和URL删除用户’我的喜欢‘
     */
    fun removeMyLike(map: Map<String,String>) = fire(Dispatchers.IO) {
        val serviceResult =
            ZeBeNetwork.removeMyLikeByUrlAndEmail(map["url"].toString(), map["email"].toString())
        if (serviceResult.request == "removeMyLikeByUrlAndEmail") {
            Result.success(serviceResult)
        } else {
            Result.failure(RuntimeException("request is not removeMyLikeByUrlAndEmail"))
        }
    }

    /**
     * 通过相册集URL和邮箱，判断当前相册集是否在邮箱用户的’我的喜欢‘中。
     */
    fun isLikeByUrlAndEmail(map: Map<String,String>) = fire(Dispatchers.IO) {
        val serviceResult =
            ZeBeNetwork.isLikeByUrlAndEmail(map["url"].toString(),map["email"].toString())
        if (serviceResult.request == "isLikeByUrlAndEmail") {
            Result.success(serviceResult)
        } else {
            Result.failure(RuntimeException("request is not isLikeByUrlAndEmail"))
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