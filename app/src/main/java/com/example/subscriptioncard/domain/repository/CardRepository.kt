package com.example.subscriptioncard.domain.repository

import com.example.subscriptioncard.domain.model.SubCard
import kotlinx.coroutines.flow.Flow

interface CardRepository {

    fun getCards(): Flow<List<SubCard>>

    suspend fun getCardById(id: Int): SubCard?

    suspend fun insertCard(subCard: SubCard)

    suspend fun  deleteCard(subCard: SubCard)
}