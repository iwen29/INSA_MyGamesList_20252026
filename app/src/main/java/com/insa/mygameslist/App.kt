package com.insa.mygameslist

import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
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
import kotlinx.coroutines.launch

@Composable
fun App() {
    val navController = rememberNavController()
    val viewModel : GameViewModel = viewModel()

    var searchQuery by remember { mutableStateOf("") }
    val context = LocalContext.current

    val gamesState = viewModel.games.collectAsState()
    val games = gamesState.value

    val filteredGames = (if (searchQuery.isNotEmpty()) {
        games.filter { game ->
            game.name.contains(searchQuery, ignoreCase = true) || game.genres.any { it.name.contains(searchQuery, ignoreCase = true) } || game.platforms.any { it.name.contains(searchQuery, ignoreCase = true) }
        }
    } else {
        games
    }).sortedBy { it.name }.sortedBy { !it.isFavorite }

    val pagerState = rememberPagerState() { filteredGames.size }
    val coroutineScope = rememberCoroutineScope()

//    LaunchedEffect(Unit) {
//        viewModel.loadGames(context = context)
//    }

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
                        navController.navigate("game")
                        coroutineScope.launch {
                            pagerState.scrollToPage(filteredGames.indexOfFirst { it.id == gameId })
                        }

                    },
                    query = searchQuery,
                    onQueryChange = { newQuery -> searchQuery = newQuery },
                    games = filteredGames
                )
            }
            composable(
                "game"
            ) {
                HorizontalPager(pagerState) { page ->
                    GamePage(context, gameId = filteredGames[page].id,viewModel = viewModel,)
                }
            }
        }
    }
}