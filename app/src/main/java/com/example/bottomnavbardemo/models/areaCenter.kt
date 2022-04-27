package com.example.loadshedding.models

import com.google.gson.annotations.SerializedName

data class areaCenter(
    @SerializedName("time") val time: String?,
    @SerializedName("lat") val lat: Float?,
    @SerializedName("lng") val lng: Float?,
)