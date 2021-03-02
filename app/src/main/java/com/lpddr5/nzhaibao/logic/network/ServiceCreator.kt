package com.lpddr5.nzhaibao.logic.network

import com.lpddr5.nzhaibao.LogUtil
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object ServiceCreator {

    private const val SERVER_URL = "https://lpddr5.cn/zebe/"

    private fun retrofit(): Retrofit {
        return Retrofit.Builder()
            .baseUrl(SERVER_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    fun <T> create(serviceClass: Class<T>):T = retrofit().create(serviceClass)

    inline fun <reified T> create(): T = create(T::class.java)
}