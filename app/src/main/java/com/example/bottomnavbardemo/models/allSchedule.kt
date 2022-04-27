package com.example.loadshedding.models

import com.google.gson.annotations.SerializedName

data class allSchedule(
    @SerializedName("type") val type: String?,
    @SerializedName("name") val name: String?,
    @SerializedName("features") val features: List<areaGeojson>,
)