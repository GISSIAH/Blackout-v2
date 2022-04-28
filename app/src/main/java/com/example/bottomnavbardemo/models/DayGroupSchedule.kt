package com.example.loadshedding.models

import com.example.bottomnavbardemo.models.timeModel
import com.google.gson.JsonObject
import com.google.gson.annotations.SerializedName

data class DayGroupSchedule (
    @SerializedName("name") val name: String?,
    @SerializedName("time") val time: timeModel
        )

