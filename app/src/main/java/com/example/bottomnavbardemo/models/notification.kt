package com.example.bottomnavbardemo.models

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import org.joda.time.DateTime

@Entity
data class notification(
    @PrimaryKey val uid: String,
    @ColumnInfo(name = "title")val title:String,
    @ColumnInfo(name = "body")val body:String,
    @ColumnInfo(name = "date") val date: String
)