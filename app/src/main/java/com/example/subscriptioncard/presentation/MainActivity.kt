package com.example.subscriptioncard.presentation

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.navigation.NavArgument
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.subscriptioncard.presentation.card_display.CardDisplayScreen
import com.example.subscriptioncard.presentation.card_history.CardHistoryScreen
import com.example.subscriptioncard.presentation.card_options.CardOptionsScreen
import com.example.subscriptioncard.ui.theme.SubscriptionCardTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            SubscriptionCardTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    color = MaterialTheme.colors.background
                ) {
                    val navController = rememberNavController()
                    NavHost(
                        navController = navController,
                        startDestination = Screen.OptionsScreen.route
                    ) {
                        composable(route = Screen.OptionsScreen.route) {
                            CardOptionsScreen(navController = navController)
                        }
                        composable(
                            route = Screen.CardDisplayScreen.route +
                                    "/cardId={cardId}",
                            arguments = listOf(
                                navArgument(
                                    name = "cardId"
                                ) {
                                    type = NavType.IntType
                                    defaultValue = -1
                                }
                            )
                        ) {
                            CardDisplayScreen(navController = navController)
                        }
                        composable(route = Screen.CardHistoryScreen.route +
                                "/cardId={cardId}",
                            arguments = listOf(
                                navArgument(
                                    name = "cardId"
                                ) {
                                    type = NavType.IntType
                                    defaultValue = -1
                                }
                            )
                        ){
                            CardHistoryScreen(navController = navController)
                        }
                    }
                }
            }
        }
    }
}