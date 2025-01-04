package com.example.subscriptioncard.presentation.card_display

import android.util.Log
import androidx.compose.runtime.mutableStateListOf
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.subscriptioncard.domain.model.SubCard
import com.example.subscriptioncard.domain.repository.CardRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CardDisplayViewModel @Inject constructor(
    private val repository: CardRepository,
    savedStateHandle: SavedStateHandle
): ViewModel(){

    private val _checked = mutableStateListOf<Long>()
    val checked: List<Long> = _checked

    var cardName: String = ""
    var stumpNum: Int = 0

    var currentCardId: Int? = null
        private set

    init {
        savedStateHandle.get<Int>("cardId")?.let { cardId ->
            if (cardId != -1) {
                viewModelScope.launch {
                    repository.getCardById(cardId)?.also { subCard ->
                        currentCardId = subCard.id
                        cardName = subCard.name
                        stumpNum = subCard.stumpNum
                        _checked.addAll(subCard.checked.toList())
                    }
                }
            }
            else {
                Log.d("subcard","problem")
            }
        }
    }

    fun onIncreaseChecked(amount: Int) {
        val addNum: Int = increaseChecked(amount, stumpNum, _checked) - checked.size
        val currentTime = System.currentTimeMillis()
        for (i in 1..addNum) {
            _checked.add(currentTime)
        }

        viewModelScope.launch {
            val subCard = repository.getCardById(currentCardId!!)
            val createdDate = if (subCard == null) { 0 } else {
                subCard?.createdTimeStamp
            }
            val newHistory = subCard?.history?.toList()?.plus((Pair(currentTime, addNum)))
            repository.insertCard(
                SubCard(
                    name = cardName,
                    stumpNum = stumpNum,
                    checked = _checked.toList(),
                    createdTimeStamp = createdDate?: 0,
                    editedTimeStamp = System.currentTimeMillis(),
                    history = newHistory?: listOf(),
                    id = currentCardId
                )
            )
        }
    }

    fun onRemoveCheck(index: Int) {
        if (index < 0 || index >= checked.size) {
            return
        }
        _checked.removeAt(index)
        val currentTime = System.currentTimeMillis()
        viewModelScope.launch {
            val subCard = repository.getCardById(currentCardId!!)
            val createdDate = if (subCard == null) { 0 } else {
                subCard?.createdTimeStamp
            }
            val newHistory = subCard?.history?.toList()?.plus((Pair(currentTime, -1)))
            repository.insertCard(
                SubCard(
                    name = cardName,
                    stumpNum = stumpNum,
                    checked = _checked.toList(),
                    createdTimeStamp = createdDate?: 0,
                    editedTimeStamp = System.currentTimeMillis(),
                    history = newHistory?: listOf(),
                    id = currentCardId
                )
            )
        }
    }

    private fun increaseChecked(amount: Int, maxCount: Int, currentChecked: List<Long>): Int {
        if (currentChecked.size + amount <= maxCount) {
            if (amount < 0) {
                return currentChecked.size
            }
            return currentChecked.size + amount
        }
        return maxCount
    }
}