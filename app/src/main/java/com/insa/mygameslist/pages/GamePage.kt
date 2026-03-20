package com.insa.mygameslist.pages

import android.annotation.SuppressLint
import android.content.Context
import androidx.compose.foundation.basicMarquee
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.systemBars
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.outlined.StarBorder
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults.topAppBarColors
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil3.compose.AsyncImage
import com.insa.mygameslist.R
import com.insa.mygameslist.ui.theme.IBMItalic
import com.insa.mygameslist.ui.theme.IBMRegular
import com.insa.mygameslist.view.GameViewModel

@SuppressLint("DefaultLocale")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GamePage(context: Context, gameId: Long, viewModel: GameViewModel) {
    val games by viewModel.games.collectAsState()
    val game = games.find { it.id == gameId }

    Scaffold(
        topBar = {
            TopAppBar(
                colors = topAppBarColors(
                    containerColor = Color.hsv(100f, 0.47f, 1f),
                    titleContentColor = Color.Black
                ),
                title = { Text(
                    text= game?.name ?: "Game not found",
                    fontFamily = IBMItalic,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.fillMaxWidth().basicMarquee(repeatDelayMillis = 1500, initialDelayMillis = 1500, velocity = 60.dp)
                ) } ,
                actions = {
                    if (game != null) {
                        IconButton(
                            onClick = { viewModel.toggleFavorite(context, game.id) }
                        ) {
                            Icon(
                                imageVector = if (game.isFavorite) Icons.Filled.Star else Icons.Outlined.StarBorder,
                                contentDescription = if (game.isFavorite) "Retirer des favoris" else "Ajouter aux favoris",
                                tint = if (game.isFavorite) Color.hsv(40f, 0.958f, 1f) else Color.Gray
                            )
                        }
                    }
                })
        },
        contentWindowInsets = WindowInsets.systemBars,
        modifier = Modifier.fillMaxSize()
    ) { innerPadding ->
        if (game != null) {
            Column(
                modifier = Modifier.padding(innerPadding).fillMaxWidth().verticalScroll(rememberScrollState()),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Spacer(modifier = Modifier.height(16.dp))

                AsyncImage(
                    model = "https:${game.cover.url}",
                    contentDescription = "Game image",
                    modifier = Modifier
                        .size(250.dp)
                        .padding(end = 12.dp),
                    contentScale = ContentScale.Fit,
                    error = painterResource(R.drawable.broken_image)
                )

                Spacer(modifier = Modifier.height(12.dp))

                Text(
                    text = if (game.rating > 0.0)
                        "Note : %.1f / 10".format(game.rating / 10)
                    else
                        "Aucune note pour le moment",
                    fontFamily = IBMRegular,
                    fontWeight = FontWeight.Bold,
                    fontSize = 15.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    textAlign = TextAlign.Center
                )

                if (!game.genres.isEmpty()) {
                    Spacer(modifier = Modifier.height(4.dp))

                    Text(
                        text = "Genres : ${game.genres.joinToString(", ") { it.name }}",
                        fontFamily = IBMItalic,
                        fontWeight = FontWeight.Normal,
                        fontSize = 13.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        textAlign = TextAlign.Center
                    )
                }
                Spacer(modifier = Modifier.height(13.dp))

                Row(modifier = Modifier.fillMaxWidth().horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.Center){
                    for(p in game.platforms){

                        var imageUrl = "https:${p.logo?.url}"
                        if(imageUrl.endsWith("jpg")){
                            imageUrl = imageUrl.replace("jpg","png")
                        }

                        AsyncImage(
                            model = imageUrl,
                            contentDescription = "Platform image",
                            modifier = Modifier
                                .size(70.dp)
                                .padding(end = 8.dp),
                            contentScale = ContentScale.Fit,
                            error = painterResource(R.drawable.broken_image)
                        )
                    }
                }

                Text(
                    modifier = Modifier.padding(13.dp),
                    text = game.summary,
                    fontFamily = IBMRegular,
                    fontWeight = FontWeight.Normal,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurface,
                )
            }
        } else {
            Text("Game not found")
        }
    }
}