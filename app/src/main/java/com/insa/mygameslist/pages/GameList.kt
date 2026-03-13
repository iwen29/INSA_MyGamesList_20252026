package com.insa.mygameslist.pages

import android.content.Context
import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemBars
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Search
import androidx.compose.material.icons.rounded.Sell
import androidx.compose.material3.CircularProgressIndicator
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
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.mutableStateSetOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.insa.mygameslist.components.GameCard
import com.insa.mygameslist.components.TagsDialog
import com.insa.mygameslist.data.Game
import com.insa.mygameslist.view.GameViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GameList(
    context: Context,
    viewModel: GameViewModel,
    onGameClicked: (Long) -> Unit,
    query: String,
    onQueryChange: (String) -> Unit,
    tags: Set<String>,
    setTags: (Set<String>) -> Unit,
    games: List<Game>
) {
    var searchOpen by remember { mutableStateOf(query != "") }
    val focusRequester = remember { FocusRequester() }

    var tagsDialogOpen by remember { mutableStateOf(false) }

    LaunchedEffect(searchOpen) {
        if (searchOpen) {
            focusRequester.requestFocus()
        }
    }
    var previousFocusState by remember { mutableStateOf(false) }

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
                        // TODO: Make dark theme compatible
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
                        Text(
                            "My Games List",
                            fontWeight = FontWeight.Medium
                        )
                    }
                },
                actions = {
                    AnimatedVisibility(!searchOpen) {
                        Row() {
                            IconButton(
                                onClick = {
                                    tagsDialogOpen = true
                                }
                            ) {
                                Icon(
                                    imageVector = Icons.Rounded.Sell,
                                    contentDescription = "Filter by tags",
                                    tint = Color.Black
                                )
                            }
                            IconButton(
                                onClick = {
                                    searchOpen = true
                                }
                            ) {
                                Icon(
                                    imageVector = Icons.Rounded.Search,
                                    contentDescription = "Search",
                                    tint = Color.Black
                                )
                            }
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
                items = games,
                key = { it.id }
            ) { game ->
                GameCard(
                    game = game,
                    onClick = { onGameClicked(game.id) },
                    onFavoriteClick = {viewModel.toggleFavorite(context, game.id)} ,
                    modifier = Modifier.animateItem()
                )
            }
        }
        if (games.isEmpty() && (query.isNotEmpty() || tags.isNotEmpty())) {
            Text(
                text = "No games found",
                fontSize = 18.sp,
                textAlign = TextAlign.Center,
                modifier = Modifier
                    .fillMaxSize()
                    .wrapContentSize(Alignment.Center)
                    .padding(16.dp),
                color = Color.Gray
            )
        } else if (games.isEmpty()) { // This is not the proper way to do this but it works
            CircularProgressIndicator(
                modifier = Modifier
                    .fillMaxSize()
                    .wrapContentSize(Alignment.Center)
                    .padding(16.dp)
            )
        }
        if (tagsDialogOpen) {
            TagsDialog(
                onDismissRequest = { tagsDialogOpen = false },
                tags = tags,
                setTags = setTags
            )
        }
    }
}