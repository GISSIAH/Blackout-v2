package com.example.bottomnavbardemo.models

import com.google.gson.annotations.SerializedName

data class blackoutModel(
    @SerializedName("from") val from: String?,
    @SerializedName("to") val to: String?,
    @SerializedName("duration") val duration: Float,
)