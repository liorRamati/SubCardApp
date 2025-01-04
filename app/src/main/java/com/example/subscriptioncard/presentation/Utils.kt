package com.example.subscriptioncard.presentation

import java.text.SimpleDateFormat
import java.util.*

fun getFormattedDate(milliseconds: Long): String {
    val date = Date(milliseconds)
    val formatter = SimpleDateFormat("dd-MM-yy HH:mm", Locale.getDefault())
    return formatter.format(date)
}