package com.example.subscriptioncard.domain.model

sealed class CardOrder(val orderType: OrderType) {
    class Name(orderType: OrderType): CardOrder(orderType)
    class CreatedDate(orderType: OrderType): CardOrder(orderType)
    class EditedDate(orderType: OrderType): CardOrder(orderType)

    fun copy(orderType: OrderType): CardOrder {
        return when (this) {
            is Name -> Name(orderType)
            is CreatedDate -> CreatedDate(orderType)
            is EditedDate -> EditedDate(orderType)
        }
    }
}

sealed class OrderType {
    object Ascending: OrderType()
    object Descending: OrderType()
}