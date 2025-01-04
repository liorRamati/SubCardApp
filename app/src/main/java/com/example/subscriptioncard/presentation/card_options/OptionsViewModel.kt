package com.example.subscriptioncard.presentation.card_options

import android.util.Log
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.subscriptioncard.domain.model.CardOrder
import com.example.subscriptioncard.domain.model.OrderType
import com.example.subscriptioncard.domain.model.SubCard
import com.example.subscriptioncard.domain.repository.CardRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class OptionsViewModel @Inject constructor(
    private val repository: CardRepository
): ViewModel() {
    private val _state = mutableStateOf(OptionsState())
    val state: State<OptionsState> = _state

    var recentlyDeletedCard: SubCard? = null
        private set

    private var getCardsJob: Job? = null

    init {
        getFilteredCards()
    }

    fun onEvent(event: OptionsEvent) {
        when (event) {
            is OptionsEvent.DeleteCard -> {
                viewModelScope.launch {
                    recentlyDeletedCard = event.subCard
                    repository.deleteCard(event.subCard)
                }
            }
            is OptionsEvent.RestoreCard -> {
                viewModelScope.launch {
                    repository.insertCard(recentlyDeletedCard ?: return@launch)
                    recentlyDeletedCard = null
                }
            }
            is OptionsEvent.AddCard -> {
                viewModelScope.launch {
                    repository.insertCard(SubCard(event.name, event.stumpNum))
                }
            }
            is OptionsEvent.SearchTextChanged -> {
                getFilteredCards(event.text, _state.value.cardOrder)
            }
            is OptionsEvent.SearchStatusChanged -> {
                val searchStatus = !state.value.isSearchActive
                val searchText = if (searchStatus) { state.value.searchText } else { "" }
                _state.value = state.value.copy(
                    isSearchActive = searchStatus,
                    searchText = searchText
                )
                getFilteredCards(cardOrder = _state.value.cardOrder)
            }
            is OptionsEvent.Order -> {
                if (state.value.cardOrder::class == event.cardOrder::class &&
                    state.value.cardOrder.orderType == event.cardOrder.orderType) {
                    return
                }
                getFilteredCards(cardOrder = event.cardOrder)
            }
            is OptionsEvent.ToggleOrderSection -> {
                _state.value = state.value.copy(
                    isOrderSectionVisible = !state.value.isOrderSectionVisible
                )
            }
        }
    }

    private fun getFilteredCards(
        text: String = "",
        cardOrder: CardOrder = CardOrder.Name(orderType = OrderType.Ascending)
    ) {
        getCardsJob?.cancel()
        getCardsJob = getOrderedCards(cardOrder)
            .onEach { cards ->
                _state.value = state.value.copy(
                    cards = cards.filter { it.name.trim().lowercase().contains(text.lowercase()) },
                    searchText = text,
                    cardOrder = cardOrder
                )
            }.launchIn(viewModelScope)
    }

    private fun getOrderedCards(
        cardOrder: CardOrder = CardOrder.Name(orderType = OrderType.Ascending)
    ): Flow<List<SubCard>> {
        return repository.getCards().map{ cards ->
            when(cardOrder.orderType) {
                is OrderType.Ascending -> {
                    when(cardOrder) {
                        is CardOrder.Name -> cards.sortedBy { it.name.lowercase() }
                        is CardOrder.CreatedDate -> cards.sortedBy { it.createdTimeStamp }
                        is CardOrder.EditedDate -> cards.sortedBy { it.editedTimeStamp }
                    }
                }
                is OrderType.Descending -> {
                    when(cardOrder) {
                        is CardOrder.Name -> cards.sortedByDescending { it.name.lowercase() }
                        is CardOrder.CreatedDate -> cards.sortedByDescending { it.createdTimeStamp }
                        is CardOrder.EditedDate -> cards.sortedByDescending { it.editedTimeStamp }
                    }
                }
            }
        }
    }
}