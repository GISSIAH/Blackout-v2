package com.example.loadshedding.models


import com.google.gson.annotations.SerializedName

data class userLocationGroup(
    @SerializedName("area") val area: String?,
    @SerializedName("group") val group: String?,
    @SerializedName("st_within") val st_within: String?,
)