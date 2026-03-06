package com.insa.mygameslist.data

import android.content.Context
import android.util.Log
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.insa.mygameslist.R
import kotlinx.datetime.LocalDate
import kotlinx.serialization.Serializable

//object IGDB {
//    lateinit var games: List<Game>
//
//    lateinit var platforms: List<Platform>
//
//    lateinit var covers: List<Cover>
//
//    lateinit var genres: List<Genre>
//
//    lateinit var logos: List<Logo>
//
//    fun load(context: Context) {
//        covers = Gson().fromJson(
//            context.resources.openRawResource(R.raw.covers).bufferedReader(),
//            object : TypeToken<List<Cover>>() {}.type
//        )
//
//        genres = Gson().fromJson(
//            context.resources.openRawResource(R.raw.genres).bufferedReader(),
//            object : TypeToken<List<Genre>>() {}.type
//        )
//
//        logos = Gson().fromJson(
//            context.resources.openRawResource(R.raw.platform_logos).bufferedReader(),
//            object : TypeToken<List<Logo>>() {}.type
//        )
//
//        val jsonPlatforms: List<JsonPlatform> = Gson().fromJson(
//            context.resources.openRawResource(R.raw.platforms).bufferedReader(),
//            object : TypeToken<List<JsonPlatform>>() {}.type
//        )
//
//        val jsonGames: List<JsonGame> = Gson().fromJson(
//            context.resources.openRawResource(R.raw.games).bufferedReader(),
//            object : TypeToken<List<JsonGame>>() {}.type
//        )
//
//        platforms = ArrayList<Platform>()
//
//        for (p in jsonPlatforms) {
//            val logo = try {
//                if (p.platform_logo != null) {
//                    logos.first { it.id == p.platform_logo }
//                } else null
//            } catch (e: NoSuchElementException) {
//                null
//            }
//            platforms += Platform(p.id, p.name, logo)
//        }
//
//        games = ArrayList<Game>()
//
//        for (g in jsonGames) {
//            val cover = covers.first { it.id == g.cover }
//            val gameGenres = genres.filter { g.genres.contains(it.id) }.toSet()
//            val gamePlatforms = platforms.filter { g.platforms.contains(it.id) }.toSet()
//            val release = LocalDate.fromEpochDays(g.first_release_date / 86400)
//            games += Game(g.id, cover, release, gameGenres, g.name, gamePlatforms, g.summary, g.total_rating)
//        }
//
//        // for (g in games) {
//        //     Log.d("JEU", g.toString())
//        // }
//    }
//}
@Serializable
data class Cover(val id: Long, val url: String)

@Serializable
data class Genre(val id: Long, val name: String)

@Serializable
data class Logo(val id: Long, val url: String)

@Serializable
data class JsonPlatform(val id: Long, val name: String, val platform_logo: Long?)

@Serializable
data class Platform(val id: Long, val name: String, val logo: Logo?)

@Serializable
data class PlatformAPI(val id: Long, val name: String, val platform_logo: Long? = null)

@Serializable
data class JsonGame(val id: Long, val cover: Long, val first_release_date: Long, val genres: List<Long>, val name: String, val platforms: List<Long>, val summary: String, val total_rating: Double)

@Serializable
data class Game(val id: Long, val cover: Cover, val release: LocalDate, val genres: Set<Genre>, val name: String, val platforms: Set<Platform>, val summary: String, val rating: Double, val isFavorite : Boolean = false)

@Serializable
data class GameAPI(
    val id: Long,
    val name: String,
    val summary: String? = null,
    val cover: Long? = null,
    val first_release_date: Long? = null,
    val genres: List<Long> = emptyList(),
    val platforms: List<Long> = emptyList(),
    val isFavorite: Boolean = false
)