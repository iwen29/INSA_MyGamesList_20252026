package com.insa.mygameslist.pages

import android.content.Context
import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemBars
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Search
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults.topAppBarColors
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.insa.mygameslist.components.GameCard
import com.insa.mygameslist.view.GameViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GameList(
    context: Context,
    viewModel: GameViewModel,
    onGameClicked: (Long) -> Unit,
    query: String,
    onQueryChange: (String) -> Unit
) {
    var searchOpen by remember { mutableStateOf(query != "") }
    val focusRequester = remember { FocusRequester() }
    val gamesState = viewModel.games.collectAsState()
    val games = gamesState.value

    LaunchedEffect(searchOpen) {
        if (searchOpen) {
            focusRequester.requestFocus()
        }
    }
    var previousFocusState by remember { mutableStateOf(false) }
    val filteredGames = (if (query.isNotEmpty()) {
        games.filter { game ->
            game.name.contains(query, ignoreCase = true) || game.genres.any { it.name.contains(query, ignoreCase = true) } || game.platforms.any { it.name.contains(query, ignoreCase = true) }
        }
    } else {
        games
    }).sortedBy { it.name }.sortedBy { !it.isFavorite }
    BackHandler(enabled = searchOpen) {
        searchOpen = false
        onQueryChange("")
    }

    return Scaffold(
        topBar = {
            TopAppBar(
                colors = topAppBarColors(
                    containerColor = Color.hsv(209f, 0.47f, 1f),
                    titleContentColor = Color.Black,
                ),
                title = {
                    AnimatedVisibility(searchOpen) {
                        OutlinedTextField(
                            value = query,
                            onValueChange = { onQueryChange(it) },
                            placeholder = { Text("Search games...") },
                            singleLine = true,
                            keyboardOptions = KeyboardOptions(
                                imeAction = ImeAction.None
                            ),
                            modifier = Modifier
                                .focusRequester(focusRequester)
                                .fillMaxWidth(0.95f) // This is not a great workaround to the weird margins but it looks ok enough
                                .onFocusChanged {
                                    if (!it.isFocused && previousFocusState) {
                                        searchOpen = false
                                    }
                                    previousFocusState = it.isFocused
                                }
                        )
                    }
                    AnimatedVisibility(!searchOpen) {
                        Text("My Games List")
                    }
                },
                actions = {
                    AnimatedVisibility(!searchOpen) {
                        IconButton(
                            onClick = {
                                searchOpen = true
                            }
                        ) {
                            Icon(
                                imageVector = Icons.Rounded.Search,
                                contentDescription = "Refresh",
                                tint = Color.Black
                            )
                        }
                    }
                }
            )
        },
        contentWindowInsets = WindowInsets.systemBars,
        modifier = Modifier.fillMaxSize()
    ) { innerPadding ->
        LazyColumn (
            modifier =  Modifier.padding(innerPadding)
        ){
            items(
                items = filteredGames,
                key = { it.id }
            ) { game ->
                GameCard(
                    game = game,
                    onClick = { onGameClicked(game.id) },
                    onFavoriteClick = {viewModel.toggleFavorite(context, game.id)} ,
                    modifier = Modifier.animateItem()
                )
            }
            if (filteredGames.isEmpty()) {
                item {
                    Text(
                        text = "No games found",
                        fontSize = 18.sp,
                        textAlign = TextAlign.Center,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        color = Color.Gray
                    )
                }
            }
        }
    }
}