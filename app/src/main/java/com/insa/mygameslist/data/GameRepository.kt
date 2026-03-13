package com.insa.mygameslist.data

import android.util.Log
import kotlinx.coroutines.async
import kotlinx.datetime.LocalDate
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.RequestBody.Companion.toRequestBody
import kotlin.time.Clock

class GameRepository {

    val clientID = "nbgd9yqjfllcrcqjjdedtqkdt0rbz6"
    val clientSecret = "vk89qh4hhsf1s8ggjblq6jqys5dogy"
    private var cachedToken: String? = null

    suspend fun getToken(): String {
        if (cachedToken == null) {
            cachedToken = getTwitchToken(clientID, clientSecret)
        }
        Log.d("TOKEN",cachedToken!!)
        return cachedToken!!
    }
    suspend fun getGames(): List<Game> {

        val token = getToken()

        val query = """
            fields id,name,summary,genres,platforms,cover,first_release_date;
            limit 100;
        """.trimIndent()

        val body = query.toRequestBody("text/plain".toMediaType())

        val apiGames = RetrofitClient.api.getGames(clientID,"Bearer $token",body)

        val coverIds = apiGames.mapNotNull { it.cover }.distinct()
        val platformIds = apiGames.flatMap { it.platforms }.distinct()

        val covers = getCoversMap(coverIds)
        val genres = getGenresMap()
        val platforms = getPlatformsMap(platformIds)

        return apiGames.map { apiGame ->

            val coverObj = apiGame.cover?.let { covers[it] }

            val coverUrl = coverObj?.url
                ?.replace("t_thumb", "t_cover_big")

            val genreSet = apiGame.genres.mapNotNull { genres[it] }.toSet()

            val platformSet = apiGame.platforms.mapNotNull { platforms[it] }.toSet()

            Game(
                id = apiGame.id,
                name = apiGame.name,
                summary = apiGame.summary ?: "",
                cover = coverObj?.copy(url = coverUrl ?: "") ?: Cover(0,""),
                release = apiGame.first_release_date?.let {
                    kotlin.time.Instant
                        .fromEpochSeconds(it)
                        .toLocalDateTime(TimeZone.UTC)
                        .date
                } ?: LocalDate.fromEpochDays(0),
                genres = genreSet,
                platforms = platformSet,
                rating = 0.0
            )
        }
    }

    private suspend fun getCoversMap(ids: List<Long>): Map<Long, Cover> {
        val token = getToken()
        if (ids.isEmpty()) return emptyMap()

        val query = """
            fields id,url;
            where id = (${ids.joinToString(",")});
            limit 100;
        """.trimIndent()

        val body = query.toRequestBody("text/plain".toMediaType())

        return RetrofitClient.api.getCovers(clientID,"Bearer $token",body)
            .associateBy { it.id }
    }

    private suspend fun getGenresMap(): Map<Long, Genre> {
        val token = getToken()
        val body = "fields id,name;limit 100;".toRequestBody("text/plain".toMediaType())
        return RetrofitClient.api.getGenres(clientID,"Bearer $token",body).associateBy { it.id }
    }

    private suspend fun getPlatformsMap(ids: List<Long>): Map<Long, Platform> {

        if (ids.isEmpty()) return emptyMap()
        val token = getToken()
        val queryPlatforms = """
            fields id,name,platform_logo;
            where id = (${ids.joinToString(",")});
            limit 100;
        """.trimIndent()

        val bodyPlatforms = queryPlatforms.toRequestBody("text/plain".toMediaType())

        val platformsAPI = RetrofitClient.api.getPlatforms(clientID,"Bearer $token",bodyPlatforms)

        val logoIds = platformsAPI.mapNotNull { it.platform_logo }.distinct()

        val logos = getPlatformLogosMap(logoIds)

        return platformsAPI.map {
            Platform(
                id = it.id,
                name = it.name,
                logo = it.platform_logo?.let { logoId ->
                    logos[logoId]?.let { logo ->
                        val newUrl = logo.url
                            .replace("t_thumb", "t_logo_med")
                        logo.copy(url = newUrl)
                    }
                }
            )
        }.associateBy { it.id }
    }

    private suspend fun getPlatformLogosMap(ids: List<Long>): Map<Long, Logo> {

        if (ids.isEmpty()) return emptyMap()
        val token = getToken()
        val query = """
            fields id,url;
            where id = (${ids.joinToString(",")});
            limit 100;
            """.trimIndent()

        val body = query.toRequestBody("text/plain".toMediaType())

        return RetrofitClient.api.getLogos(clientID,"Bearer $token",body).associateBy { it.id }
    }
}