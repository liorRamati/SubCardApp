package com.example.subscriptioncard.presentation.card_options

import com.example.subscriptioncard.domain.model.CardOrder
import com.example.subscriptioncard.domain.model.SubCard

sealed class OptionsEvent {
    data class DeleteCard(val subCard: SubCard): OptionsEvent()
    object RestoreCard: OptionsEvent()
    data class AddCard(val name: String, val stumpNum: Int): OptionsEvent()
    data class SearchTextChanged(val text: String): OptionsEvent()
    object SearchStatusChanged: OptionsEvent()
    data class Order(val cardOrder: CardOrder): OptionsEvent()
    object ToggleOrderSection: OptionsEvent()
}