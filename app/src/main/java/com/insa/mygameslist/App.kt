package com.insa.mygameslist

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.insa.mygameslist.pages.GameList
import com.insa.mygameslist.pages.GamePage
import com.insa.mygameslist.ui.theme.MyGamesListTheme
import com.insa.mygameslist.view.GameViewModel

@Composable
fun App() {
    val navController = rememberNavController()
    val viewModel : GameViewModel = viewModel()

    var searchQuery by remember { mutableStateOf("") }
    val context = LocalContext.current

    LaunchedEffect(Unit) {
        viewModel.loadGames(context = context)
    }

    MyGamesListTheme {
        NavHost(
            navController,
            startDestination = "list"
        ) {
            composable(
                "list"
            ) {
                GameList(
                    context = context,
                    viewModel = viewModel,
                    onGameClicked = { gameId ->
                        navController.navigate("game/$gameId")
                    },
                    query = searchQuery,
                    onQueryChange = { newQuery -> searchQuery = newQuery }
                )
            }
            composable(
                "game/{gameId}"
            ) { backStackEntry ->
                val gameId = backStackEntry.arguments?.getString("gameId")?.toLongOrNull()
                if (gameId != null) {
                    GamePage(context, gameId = gameId,viewModel = viewModel,)
                }
            }
        }
    }
}