package com.insa.mygameslist.view

import android.content.Context
import androidx.lifecycle.ViewModel
import com.insa.mygameslist.data.Game
import com.insa.mygameslist.data.IGDB
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.serialization.json.Json
import java.io.File

class GameViewModel : ViewModel() {
    private val _games = MutableStateFlow<List<Game>>(emptyList())
    val games: StateFlow<List<Game>> = _games

    fun loadGames(context: Context) {
        _games.value = IGDB.games
        for (f in loadFavorites(context)) {
            _games.update { list ->
                list.map { game ->
                    if (game.id == f) {
                        game.copy(isFavorite = true)
                    }
                    else game
                }
            }
        }
    }

    fun toggleFavorite(context: Context, gameId: Long) {
        _games.update { list ->
            list.map { game ->
                if (game.id == gameId) {
                    saveFavorite(context, gameId, !game.isFavorite)
                    game.copy(isFavorite = !game.isFavorite)
                }
                else game
            }
        }
    }

    fun loadFavorites(context: Context): List<Long> {
        val file = File(context.filesDir, "favorites.json")
        if (!file.exists()) return List<Long>(0) { 0L }
        return Json.decodeFromString(file.readText())
    }

    private fun saveFavorite(context: Context, gameId: Long, isFavorite: Boolean) {
        val favorites = loadFavorites(context).toMutableList()

        if (isFavorite) {
            if (!favorites.contains(gameId)) {
                favorites += gameId
            }
        } else {
            if (favorites.contains(gameId)) {
                favorites -= gameId
            }
        }

        val file = File(context.filesDir, "favorites.json")
        file.writeText(Json.encodeToString(favorites))
    }
}