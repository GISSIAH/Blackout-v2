package com.example.bottomnavbardemo.models

import com.google.gson.annotations.SerializedName

data class timeModel(
    @SerializedName("day") val day: String?,
    @SerializedName("times") val times: List<blackoutModel>
)