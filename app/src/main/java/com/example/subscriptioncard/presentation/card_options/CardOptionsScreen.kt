package com.example.subscriptioncard.presentation.card_options

import android.annotation.SuppressLint
import androidx.compose.animation.*
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Sort
import androidx.compose.material.icons.filled.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusDirection
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.example.subscriptioncard.domain.model.CardOrder
import com.example.subscriptioncard.domain.model.OrderType
import com.example.subscriptioncard.presentation.Screen
import com.example.subscriptioncard.presentation.getFormattedDate
import kotlinx.coroutines.launch
import java.util.*

@SuppressLint("UnusedMaterialScaffoldPaddingParameter")
@OptIn(ExperimentalFoundationApi::class)
@Composable
fun CardOptionsScreen(
    navController: NavController,
    viewModel: OptionsViewModel = hiltViewModel()
) {
    val state = viewModel.state.value
    val scaffoldState = rememberScaffoldState()
    val scope = rememberCoroutineScope()
    var openDialog by remember { mutableStateOf(false) }
    var isNameError by remember { mutableStateOf(false) }
    var isNumError by remember { mutableStateOf(false) }
    Scaffold(
        floatingActionButton = {
            FloatingActionButton(onClick = {
                openDialog = true
            }) {
                Icon(imageVector = Icons.Default.Add, contentDescription = "Add card")
            }
        },
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Your Cards",
                        textAlign = TextAlign.Center,
                        fontWeight = FontWeight.Bold,
                        fontSize = 24.sp,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            )
        },
        scaffoldState = scaffoldState
    ) {
        Column(
            verticalArrangement = Arrangement.spacedBy(8.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.padding(8.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                HideableTextField(
                    text = state.searchText,
                    isSearchActive = state.isSearchActive,
                    onTextChanged = { viewModel.onEvent(OptionsEvent.SearchTextChanged(it)) },
                    onSearchClick = { viewModel.onEvent(OptionsEvent.SearchStatusChanged) },
                    onCloseClick = { viewModel.onEvent(OptionsEvent.SearchStatusChanged) },
                    modifier = Modifier
                        .weight(1f)
                        .height(90.dp)
                )
                IconButton(onClick = { viewModel.onEvent(OptionsEvent.ToggleOrderSection) }) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.Sort,
                        contentDescription = "sort"
                    )
                }
            }
            AnimatedVisibility(
                visible = state.isOrderSectionVisible,
                enter = fadeIn() + slideInVertically(),
                exit = fadeOut() + slideOutVertically()
            ) {
                OrderSection(
                    cardOrder = state.cardOrder,
                    onOrderChanged = { viewModel.onEvent(OptionsEvent.Order(it)) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 16.dp)
                )
            }
            LazyVerticalGrid(
                columns = GridCells.Adaptive(128.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(state.cards) { subCard ->
                    Button(
                        onClick = {
                            navController.navigate(
                                Screen.CardDisplayScreen.route +
                                        "/CardId=${subCard.id}"
                            )
                        },
                        colors = ButtonDefaults.buttonColors(
                            backgroundColor = MaterialTheme.colors.primary
                        )
                    ) {
                        Column() {
                            Box(modifier = Modifier.fillMaxWidth()) {
                                Text(
                                    text = subCard.name,
                                    modifier = Modifier
                                        .padding(end = 32.dp)
                                        .align(Alignment.CenterStart)
                                )
                                IconButton(
                                    onClick = {
                                        viewModel.onEvent(OptionsEvent.DeleteCard(subCard))
                                        scope.launch {
                                            val result =
                                                scaffoldState.snackbarHostState.showSnackbar(
                                                    message = "Card ${viewModel.recentlyDeletedCard?.name} deleted",
                                                    actionLabel = "Undo",
                                                    duration = SnackbarDuration.Indefinite
                                                )
                                            if (result == SnackbarResult.ActionPerformed) {
                                                viewModel.onEvent(OptionsEvent.RestoreCard)
                                            }
                                        }
                                    },
                                    modifier = Modifier.align(Alignment.BottomEnd)
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Delete,
                                        contentDescription = "delete card"
                                    )
                                }
                            }
                            Row(
                                horizontalArrangement = Arrangement.SpaceBetween,
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Text(
                                    text = "${subCard.checked.size}/${subCard.stumpNum}",
                                    style = MaterialTheme.typography.body2
                                )
                                Text(
                                    text = if (state.cardOrder::class ==
                                        CardOrder.EditedDate::class
                                    ) {
                                        getFormattedDate(subCard.editedTimeStamp)
                                    } else {
                                        getFormattedDate(subCard.createdTimeStamp)
                                    },
                                    style = MaterialTheme.typography.body2
                                )
                            }
                        }
                    }
                }
            }
        }

        if (openDialog) {
            NewCardPopup(
                onDismiss = { openDialog = false },
                onAccept = { name, num ->
                    val cardNames = state.cards.map { subCard -> subCard.name }
                    val numValue = num.toIntOrNull() ?: 0
                    if (name == "" || cardNames.contains(name)) {
                        isNameError = true
                    } else if (numValue <= 0) {
                        isNumError = true
                    } else {
                        viewModel.onEvent(OptionsEvent.AddCard(name, numValue))
                        openDialog = false
                    }
                },
                isNameError = isNameError,
                flipNameError = { isNameError = !isNameError },
                isNumError = isNumError,
                flipNumError = { isNumError = !isNumError }
            )
        }
    }
}

@Composable
fun HideableTextField(
    text: String,
    isSearchActive: Boolean,
    onTextChanged: (String) -> Unit,
    onSearchClick: () -> Unit,
    onCloseClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(modifier = modifier) {
        AnimatedVisibility(visible = isSearchActive, enter = fadeIn(), exit = fadeOut()) {
            OutlinedTextField(
                value = text,
                onValueChange = onTextChanged,
                singleLine = true,
                shape = RoundedCornerShape(50.dp),
                placeholder = { Text(text = "Search") },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            )
        }
        AnimatedVisibility(
            visible = !isSearchActive,
            enter = fadeIn(),
            exit = fadeOut(),
            modifier = Modifier.align(Alignment.CenterEnd)
        ) {
            IconButton(onClick = onSearchClick) {
                Icon(imageVector = Icons.Default.Search, contentDescription = "open search")
            }
        }
        AnimatedVisibility(
            visible = isSearchActive,
            enter = fadeIn(),
            exit = fadeOut(),
            modifier = Modifier.align(Alignment.CenterEnd)
        ) {
            IconButton(onClick = onCloseClick) {
                Icon(imageVector = Icons.Default.Close, contentDescription = "close search")
            }
        }
    }
}

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun NewCardPopup(
    onDismiss: () -> Unit,
    onAccept: (String, String) -> Unit,
    isNameError: Boolean,
    flipNameError: () -> Unit,
    isNumError: Boolean,
    flipNumError: () -> Unit
) {
    var inputName by remember { mutableStateOf("") }
    var inputNum by remember { mutableStateOf("") }
    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        val focusManager = LocalFocusManager.current
        Surface(
            color = MaterialTheme.colors.surface
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Spacer(modifier = Modifier.height(8.dp))
                TextFieldWithError(
                    value = inputName,
                    label = { Text(text = "card name:") },
                    onValueChange = { value ->
                        inputName = value
                        if (isNameError) {
                            flipNameError()
                        }
                    },
                    keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next),
                    keyboardActions = KeyboardActions(
                        onNext = { focusManager.moveFocus(FocusDirection.Down) }
                    ),
                    isError = isNameError,
                    errorMessage = if (inputName == "") {"name is empty"} else "name already exists"
                )
                TextFieldWithError(
                    value = inputNum,
                    label = { Text(text = "pick number of stumps:") },
                    onValueChange = { value ->
                        inputNum = value.filter { it.isDigit() }
                        if (isNumError) {
                            flipNumError()
                        }
                    },
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Number,
                        imeAction = ImeAction.Go
                    ),
                    keyboardActions = KeyboardActions(onGo = {
                        onAccept(inputName, inputNum)
                    }),
                    isError = isNumError,
                    errorMessage = "stump number should be positive"
                )
                Row (modifier = Modifier.padding(16.dp)) {
                    Button(onClick = {
                        onAccept(inputName, inputNum)
                    }) {
                        Text(text = "ok")
                    }
                    Spacer(modifier = Modifier.weight(0.5f))
                    Button(onClick = onDismiss) {
                        Text(text = "cancel")
                    }
                }
            }
        }
    }
}

@Composable
fun TextFieldWithError(
    value: String,
    label: @Composable (() -> Unit)? = null,
    onValueChange: (String) -> Unit,
    keyboardOptions: KeyboardOptions = KeyboardOptions.Default,
    keyboardActions: KeyboardActions = KeyboardActions(),
    isError: Boolean = false,
    errorMessage: String = "Error",
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
    ) {
        TextField(
            value = value,
            label = label,
            singleLine = true,
            onValueChange = onValueChange,
            keyboardOptions = keyboardOptions,
            keyboardActions = keyboardActions,
            trailingIcon = {
                   if (isError) {
                       Icon(
                           Icons.Filled.Error,
                           contentDescription = "error",
                           tint = MaterialTheme.colors.error
                       )
                   }
            },
            colors = TextFieldDefaults.textFieldColors(
                backgroundColor = MaterialTheme.colors.background,
                focusedLabelColor = MaterialTheme.colors.onBackground,
                focusedIndicatorColor = MaterialTheme.colors.onBackground,
                cursorColor = MaterialTheme.colors.onBackground
            ),
            modifier = Modifier.fillMaxWidth()
        )
        if (isError) {
            Text(
                text = errorMessage,
                color = MaterialTheme.colors.error,
                style = MaterialTheme.typography.caption,
                modifier = Modifier.padding(start = 16.dp)
            )
        }
    }
}

@Composable
fun OrderSection(
    cardOrder: CardOrder = CardOrder.Name(OrderType.Descending),
    onOrderChanged: (CardOrder) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier) {
        Row(modifier = Modifier.fillMaxWidth()) {
            NamedRadioButton(
                text = "Name",
                selected = cardOrder is CardOrder.Name,
                onSelect = { onOrderChanged(CardOrder.Name(cardOrder.orderType)) }
            )
            Spacer(modifier = Modifier.width(4.dp))
            NamedRadioButton(
                text = "Created Date",
                selected = cardOrder is CardOrder.CreatedDate,
                onSelect = { onOrderChanged(CardOrder.CreatedDate(cardOrder.orderType)) }
            )
            Spacer(modifier = Modifier.width(4.dp))
            NamedRadioButton(
                text = "Edited Date",
                selected = cardOrder is CardOrder.EditedDate,
                onSelect = { onOrderChanged(CardOrder.EditedDate(cardOrder.orderType)) }
            )
        }
        Spacer(modifier = Modifier.height(16.dp))
        Row(modifier = Modifier.fillMaxWidth()) {
            NamedRadioButton(
                text = "Ascending",
                selected = cardOrder.orderType is OrderType.Ascending,
                onSelect = { onOrderChanged(cardOrder.copy(OrderType.Ascending)) }
            )
            Spacer(modifier = Modifier.width(4.dp))
            NamedRadioButton(
                text = "Descending",
                selected = cardOrder.orderType is OrderType.Descending,
                onSelect = { onOrderChanged(cardOrder.copy(OrderType.Descending)) }
            )
        }
    }
}

@Composable
fun NamedRadioButton(
    text: String,
    selected: Boolean,
    onSelect: () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(verticalAlignment = Alignment.CenterVertically, modifier = modifier) {
        RadioButton(selected = selected, onClick = onSelect)
        Text(text = text, style = MaterialTheme.typography.body1)
    }
}


