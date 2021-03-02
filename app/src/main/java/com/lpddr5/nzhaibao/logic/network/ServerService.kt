package com.lpddr5.nzhaibao.logic.network


import com.lpddr5.nzhaibao.logic.model.LatestVersion
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

interface ServerService {

    @GET("getLatestVersion")
    fun getLatestVersion(): Call<LatestVersion>

}