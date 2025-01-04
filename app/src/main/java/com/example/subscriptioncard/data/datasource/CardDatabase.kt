package com.example.subscriptioncard.data.datasource

import android.util.Log
import androidx.room.*
import com.example.subscriptioncard.domain.model.SubCard
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

@Database(
    entities = [SubCard::class],
    version = 1,
    exportSchema = false,
)
@TypeConverters(Converters::class)
abstract class CardDatabase: RoomDatabase() {

    abstract val cardDao: CardDao

    companion object {
        const val DATABASE_NAME = "subcards_db1"
    }
}

class Converters {
    @TypeConverter
    fun fromListIntToString(longList: List<Long>): String = longList.toString()
    @TypeConverter
    fun toListIntFromString(stringList: String): List<Long> {
        val result = ArrayList<Long>()
        val split = stringList.replace("[","").replace("]","")
            .replace(" ","").split(",")
        for (n in split) {
            try {
                result.add(n.toLong())
            } catch (e: Exception) {

            }
        }
        return result
    }


    @TypeConverter
    fun fromListPairToString(pairList: List<Pair<Long, Int>>): String {
        val resultArray = ArrayList<String>()
        for (p in pairList) {
            resultArray.add("(${p.first};${p.second})")
        }
        return resultArray.toString()
    }
    @TypeConverter
    fun toListPairFromString(pairList: String): List<Pair<Long, Int>> {
        val result = ArrayList<Pair<Long, Int>>()
        val split = pairList.replace("[","").replace("]","")
            .replace(" ","").split(",")
        for (n in split) {
            val pairSplit = n.replace("(","").replace(")","")
                .replace(" ","").split(";")
            try {
                result.add(Pair(pairSplit[0].toLong(), pairSplit[1].toInt()))
            } catch (e: Exception) {

            }
        }
        return result.toList()
    }
}