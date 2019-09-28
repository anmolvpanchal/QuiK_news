package com.`in`.weareindian.quiknews.Api

import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.http.GET



interface ApiClient {

    @GET("read.php")
    fun getTopPosts(): Call<ResponseBody>
}