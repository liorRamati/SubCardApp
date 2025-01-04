package com.example.subscriptioncard.presentation.card_display

import android.annotation.SuppressLint
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.itemsIndexed
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.chargemap.compose.numberpicker.NumberPicker
import com.example.subscriptioncard.presentation.Screen
import com.example.subscriptioncard.presentation.getFormattedDate
import com.example.subscriptioncard.R

@SuppressLint("UnusedMaterialScaffoldPaddingParameter")
@OptIn(ExperimentalFoundationApi::class)
@Composable
fun CardDisplayScreen(
    navController: NavController,
    viewModel: CardDisplayViewModel = hiltViewModel()
) {
    var openDialog by remember { mutableStateOf(false) }
    var openConfirm by remember { mutableStateOf(false) }
    val cardName = viewModel.cardName
    val stumpNum = viewModel.stumpNum
    val checked = viewModel.checked
    var confirmFun: () -> Unit = {}
    var message = ""

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Row(modifier = Modifier.fillMaxWidth()) {
                        Spacer(modifier = Modifier.weight(1f))
                        Text(
                            text = cardName,
                            fontSize = 24.sp,
                            fontWeight = FontWeight.Bold,
                        )
                        Spacer(modifier = Modifier.weight(1f))
                        Text(
                            text = "${checked.size}/$stumpNum",
                            fontSize = 24.sp,
                            fontWeight = FontWeight.Bold,
                        )
                        Spacer(modifier = Modifier.weight(1f))
                    }
                },
                navigationIcon = {
                    IconButton(onClick = { navController.navigateUp() }) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = stringResource(R.string.back_button)
                        )
                    }
                }
            )
        }
    ) {
        Column {
            Spacer(modifier = Modifier.height(16.dp))
            Row(modifier = Modifier.fillMaxWidth()) {
                Spacer(modifier = Modifier.weight(0.5f))
                Button(
                    onClick = {
                        openConfirm = true
                        confirmFun = { viewModel.onIncreaseChecked(1) }
                        message = "Do you want to add a check stump?"
                              },
                    modifier = Modifier.weight(1f)
                ) {
                    Text(text = stringResource(R.string.one_tick_button))
                }
                Spacer(modifier = Modifier.weight(0.5f))
                Button(
                    onClick = { openDialog = true },
                    modifier = Modifier.weight(1f)
                ) {
                    Text(text = stringResource(R.string.multi_tick_button))
                }
                Spacer(modifier = Modifier.weight(0.5f))
            }
            Spacer(modifier = Modifier.height(16.dp))
            LazyVerticalGrid(
                columns = GridCells.Adaptive(128.dp),
                modifier = Modifier.weight(1f)
            ) {
                itemsIndexed(checked) { index, currentTimeStamp ->
                    Box(modifier = Modifier.size(128.dp)) {
                        Icon(
                            painter = painterResource(R.drawable.ic_baseline_check_box_24),
                            contentDescription = null,
                            tint = MaterialTheme.colors.primary,
                            modifier = Modifier
                                .clickable {
                                    openConfirm = true
                                    confirmFun = { viewModel.onRemoveCheck(index) }
                                    message = "Do you want to remove the checked stump?"
                                }
                                .size(128.dp)
                                .align(Alignment.TopCenter)
                        )
                        Text(
                            text = getFormattedDate(currentTimeStamp),
                            color = MaterialTheme.colors.primary,
                            style = MaterialTheme.typography.caption,
                            modifier = Modifier.align(Alignment.BottomCenter)
                        )
                    }
                }
                items(stumpNum - checked.size) {
                    Icon(
                        painter = painterResource(R.drawable.ic_baseline_check_box_outline_blank_24),
                        contentDescription = null,
                        tint = MaterialTheme.colors.primary,
                        modifier = Modifier
                            .clickable {
                                openConfirm = true
                                confirmFun = { viewModel.onIncreaseChecked(1) }
                                message = "Do you want to add a check stump?"
                            }
                            .size(128.dp)
                    )
                }
            }
            Spacer(modifier = Modifier.height(16.dp))
            Row(
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                Button(
                    onClick = { navController.navigate(Screen.CardHistoryScreen.route +
                        "/cardId=${viewModel.currentCardId}") },
                    modifier = Modifier.padding(bottom = 16.dp)
                ) {
                    Text(text = "history")
                }
            }
            if (openConfirm) {
                ConfirmationDialog(
                    onAccept = confirmFun,
                    onDismiss = { openConfirm = false },
                    message = message
                )
            }
            if (openDialog) {
                NumberPickerPopup(
                    maxValue = stumpNum - checked.size,
                    onDismiss = { openDialog = false },
                    onAccept = {
                        viewModel.onIncreaseChecked(it)
                    }
                )
            }
        }
    }
}

@Composable
fun NumberPickerPopup(maxValue: Int, onDismiss: () -> Unit, onAccept: (Int) -> Unit) {
    var pickerValue by remember { mutableStateOf(0) }
    Dialog(onDismissRequest = onDismiss) {
        Surface(color = MaterialTheme.colors.surface) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.padding(bottom = 16.dp)
            ) {
                NumberPicker(
                    value = pickerValue,
                    onValueChange = { pickerValue = it },
                    range = 0..maxValue,
                    textStyle = TextStyle(color = MaterialTheme.colors.onBackground)
                )
                Row(modifier = Modifier.fillMaxWidth()) {
                    Spacer(modifier = Modifier.weight(0.5f))
                    Button(
                        onClick = {
                            onDismiss()
                            onAccept(pickerValue)
                        },
                        modifier = Modifier.weight(1f)
                    ) {
                        Text(text = "ok")
                    }
                    Spacer(modifier = Modifier.weight(0.5f))
                    Button(
                        onClick = onDismiss,
                        modifier = Modifier.weight(1f)
                    ) {
                        Text(text = "cancel")
                    }
                    Spacer(modifier = Modifier.weight(0.5f))
                }
            }
        }
    }
}

@Composable
fun ConfirmationDialog(
    onAccept: () -> Unit,
    onDismiss: () -> Unit,
    message: String
) {
    Dialog(onDismissRequest = onDismiss) {
        Surface(color = MaterialTheme.colors.surface) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(16.dp),
                modifier = Modifier.padding(16.dp)
            ) {
                Text(text = message, style = MaterialTheme.typography.h1)
                Row(modifier = Modifier.fillMaxWidth()) {
                    Spacer(modifier = Modifier.weight(0.5f))
                    Button(
                        onClick = {
                            onDismiss()
                            onAccept()
                        },
                        modifier = Modifier.weight(1f)
                    ) {
                        Text(text = "ok")
                    }
                    Spacer(modifier = Modifier.weight(0.5f))
                    Button(
                        onClick = onDismiss,
                        modifier = Modifier.weight(1f)
                    ) {
                        Text(text = "cancel")
                    }
                    Spacer(modifier = Modifier.weight(0.5f))
                }
            }
        }
    }
}