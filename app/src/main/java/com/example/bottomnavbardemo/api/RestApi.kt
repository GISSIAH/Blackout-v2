package com.example.bottomnavbardemo.api


import com.example.loadshedding.models.*
import retrofit2.Call
import retrofit2.Response
import retrofit2.http.*

interface RestApi {

    @Headers("Content-Type: application/json")


    @GET("/user/locate")
    fun getUserLocationGroup(@Query("lat")lat : Float,@Query("lng")lng : Float ):  Call<userLocationGroup>

    @GET("/area/schedule/{name}")
    fun getGroupSchedule(@Path("name")group:String): Call<groupSchedule>

    @GET("/schedule/today/{name}")
    fun getTodayGroupSchedule(@Path("name")group:String): Call<todayGroupSchedule>

    @GET("/all/schedule/")
    fun getAllSchedule(): Call<allSchedule>

    @GET("/area/center/{name}")
    fun getCenterofArea(@Path("name") name:String) : Call<areaCenter>


}