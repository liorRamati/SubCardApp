package com.example.subscriptioncard.domain.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverter
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

@Entity
data class SubCard(
    val name: String,
    val stumpNum: Int,
    val checked: List<Long> = listOf(),
    val createdTimeStamp: Long = System.currentTimeMillis(),
    val editedTimeStamp: Long = System.currentTimeMillis(),
    val history: List<Pair<Long, Int>> = listOf(),
    @PrimaryKey val id: Int? = null
)

class InvalidCardException(message: String): Exception(message)