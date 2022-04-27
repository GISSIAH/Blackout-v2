package com.example.loadshedding.models

import com.google.gson.annotations.SerializedName

data class groupSchedule(
    @SerializedName("id") val id: String?,
    @SerializedName("name") val name: String?,
    @SerializedName("date_updated") val date_updated: String?,
    @SerializedName("schedule") val schedule: Array<String>,
    @SerializedName("__v") val v: String?
)
