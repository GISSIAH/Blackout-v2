package com.example.loadshedding.models

import com.google.gson.JsonObject
import com.google.gson.annotations.SerializedName

data class areaGeojson (
        @SerializedName("type") val typ: String?,
        @SerializedName("geometry") val geometry: JsonObject?,
        @SerializedName("properties") val properties: JsonObject?,
)