package com.example.subscriptioncard.data.repository

import com.example.subscriptioncard.data.datasource.CardDao
import com.example.subscriptioncard.domain.model.SubCard
import com.example.subscriptioncard.domain.repository.CardRepository
import kotlinx.coroutines.flow.Flow

class CardRepositoryImpl(
    private val dao: CardDao
): CardRepository {

    override fun getCards(): Flow<List<SubCard>> {
        return dao.getCards()
    }

    override suspend fun getCardById(id: Int): SubCard? {
        return dao.getCardById(id)
    }

    override suspend fun insertCard(subCard: SubCard) {
        dao.insertCard(subCard)
    }

    override suspend fun deleteCard(subCard: SubCard) {
        dao.deleteNote(subCard)
    }
}