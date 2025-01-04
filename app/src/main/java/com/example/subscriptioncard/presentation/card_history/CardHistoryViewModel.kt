package com.example.subscriptioncard.presentation.card_history

import android.util.Log
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.subscriptioncard.domain.repository.CardRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CardHistoryViewModel @Inject constructor(
    repository: CardRepository,
    savedStateHandle: SavedStateHandle
): ViewModel(){
    var history: List<Pair<Long, Int>> = listOf()
    var cardName: String = ""

    private var currentCardId: Int? = null

    init {
        savedStateHandle.get<Int>("cardId")?.let { cardId ->
            if (cardId != -1) {
                viewModelScope.launch {
                    repository.getCardById(cardId)?.also { subCard ->
                        currentCardId = subCard.id
                        cardName = subCard.name
                        history = subCard.history
                    }
                }
            }
            else {
                Log.d("subcard","problem")
            }
        }
    }
}