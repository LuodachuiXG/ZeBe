package com.lpddr5.nzhaibao.logic.network


import com.lpddr5.nzhaibao.logic.model.LatestVersion
import com.lpddr5.nzhaibao.logic.model.MyLike
import com.lpddr5.nzhaibao.logic.model.ServiceResult
import com.lpddr5.nzhaibao.logic.model.User
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Query

interface ServerService {

    /**
     * 获取最新版本信息
     */
    @GET("getLatestVersion")
    fun getLatestVersion(): Call<LatestVersion>

    /**
     * 登录
     */
    @POST("loginUser")
    fun loginUser(@Query("email") email: String,
                  @Query("password") password: String): Call<User>

    /**
     * 发送验证码
     */
    @POST("sendCode")
    fun sendCode(@Query("email") email: String,
                 @Query("type") type: String): Call<ServiceResult>

    /**
     * 注册
     */
    @POST("signUpUser")
    fun signUpUser(@Query("name") name: String,
                   @Query("password") password: String,
                   @Query("email") email: String,
                   @Query("code") code: String): Call<ServiceResult>

    /**
     * 更新密码
     */
    @POST("updatePassword")
    fun updatePassword(@Query("email") email: String,
                       @Query("password") password: String,
                       @Query("code") code: String): Call<ServiceResult>

    /**
     * 通过邮箱获取’我的喜欢‘
     */
    @POST("getMyLikeByEmail")
    fun getMyLikeByEmail(@Query("email") email: String): Call<List<MyLike>>

    /**
     * 通过邮箱添加’我的喜欢‘
     */
    @POST("addMyLikeByEmail")
    fun addMyLikeByEmail(@Query("url") url: String,
                         @Query("title") title: String,
                         @Query("imgUrl") imgUrl: String,
                         @Query("p") p: String,
                         @Query("type") type: String,
                         @Query("email") email: String): Call<ServiceResult>

    /**
     * 通过邮箱删除’我的喜欢‘
     */
    @POST("removeMyLikeByUrlAndEmail")
    fun removeMyLikeByUrlAndEmail(@Query("url") url: String,
                                 @Query("email") title: String): Call<ServiceResult>

    /**
     * 通过相册集URL和邮箱，判断当前相册集是否在邮箱用户的’我的喜欢‘中。
     */
    @POST("isLikeByUrlAndEmail")
    fun isLikeByUrlAndEmail(@Query("url") url: String,
                            @Query("email") email: String): Call<ServiceResult>
}