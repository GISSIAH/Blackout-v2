package com.example.bottomnavbardemo.api



import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object ServiceBuilder {
    private val client = OkHttpClient.Builder().build()

    private val retrofit by lazy {
        Retrofit.Builder()
            .baseUrl("https://blackout-api.onrender.com/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }
    val api: RestApi by lazy {
        retrofit.create(RestApi::class.java)
    }

}