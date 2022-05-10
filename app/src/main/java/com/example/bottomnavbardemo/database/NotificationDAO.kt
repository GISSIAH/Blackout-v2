package com.example.bottomnavbardemo.database

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import com.example.bottomnavbardemo.models.notification

@Dao
interface NotificationDAO {
    @Query("SELECT * FROM notification")
    fun getAll(): List<notification>


    @Query("SELECT * FROM notification WHERE uid LIKE :id")
    fun findById(id:String): notification

    @Insert
    fun insertOne(notification: notification)

    @Delete
    fun delete(user: notification)
}