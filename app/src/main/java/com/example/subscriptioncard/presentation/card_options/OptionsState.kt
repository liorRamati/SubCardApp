package com.example.subscriptioncard.presentation.card_options

import com.example.subscriptioncard.domain.model.CardOrder
import com.example.subscriptioncard.domain.model.OrderType
import com.example.subscriptioncard.domain.model.SubCard

data class OptionsState(
    val cards: List<SubCard> = emptyList(),
    val searchText: String = "",
    val isSearchActive: Boolean = false,
    val cardOrder: CardOrder = CardOrder.Name(OrderType.Descending),
    val isOrderSectionVisible: Boolean = false
)