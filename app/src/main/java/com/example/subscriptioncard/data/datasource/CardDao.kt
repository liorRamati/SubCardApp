package com.example.subscriptioncard.data.datasource

import androidx.room.*
import com.example.subscriptioncard.domain.model.SubCard
import kotlinx.coroutines.flow.Flow

@Dao
interface CardDao {

    @Query("SELECT * FROM subcard")
    fun getCards(): Flow<List<SubCard>>

    @Query("SELECT * FROM subcard WHERE id = :id")
    suspend fun getCardById(id: Int): SubCard?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCard(subCard: SubCard)

    @Delete
    suspend fun deleteNote(subCard: SubCard)
}