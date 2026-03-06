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

    suspend fun getGames(): List<Game> {

        val query = """
            fields id,name,summary,genres,platforms,cover,first_release_date;
            limit 100;
        """.trimIndent()

        val body = query.toRequestBody("text/plain".toMediaType())

        val apiGames = RetrofitClient.api.getGames(body)

        val coverIds = apiGames.mapNotNull { it.cover }.distinct()
        val platformIds = apiGames.flatMap { it.platforms }.distinct()

        val covers = getCoversMap(coverIds)
        val genres = getGenresMap()
        val platforms = getPlatformsMap(platformIds)

        return apiGames.map { apiGame ->

            val coverObj = apiGame.cover?.let { covers[it] }

            val genreSet = apiGame.genres.mapNotNull { genres[it] }.toSet()

            val platformSet = apiGame.platforms.mapNotNull { platforms[it] }.toSet()

            Game(
                id = apiGame.id,
                name = apiGame.name,
                summary = apiGame.summary ?: "",
                cover = coverObj ?: Cover(0, ""),
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
        if (ids.isEmpty()) return emptyMap()
        Log.d("CoverIDS", ids.toString())

        val query = """
        fields id,url;
        where id = (${ids.joinToString(",")});
        limit 100;
    """.trimIndent()

        val body = query.toRequestBody("text/plain".toMediaType())

        return RetrofitClient.api.getCovers(body)
            .associateBy { it.id }
    }

    private suspend fun getGenresMap(): Map<Long, Genre> {
        val body = "fields id,name;limit 100;".toRequestBody("text/plain".toMediaType())
        return RetrofitClient.api.getGenres(body).associateBy { it.id }
    }

    private suspend fun getPlatformsMap(ids: List<Long>): Map<Long, Platform> {

        if (ids.isEmpty()) return emptyMap()

        val queryPlatforms = """
        fields id,name,platform_logo;
        where id = (${ids.joinToString(",")});
        limit 100;
    """.trimIndent()

        val bodyPlatforms = queryPlatforms.toRequestBody("text/plain".toMediaType())

        val platformsAPI = RetrofitClient.api.getPlatforms(bodyPlatforms)

        val logoIds = platformsAPI.mapNotNull { it.platform_logo }.distinct()

        val logos = getPlatformLogosMap(logoIds)

        Log.d("LOGOS", logos.toString())

        return platformsAPI.map {
            Platform(
                id = it.id,
                name = it.name,
                logo = it.platform_logo ?.let { platform_logo -> logos[platform_logo] }
            )
        }.associateBy { it.id }
    }

    private suspend fun getPlatformLogosMap(ids: List<Long>): Map<Long, Logo> {

        if (ids.isEmpty()) return emptyMap()

        val query = """
        fields id,url;
        where id = (${ids.joinToString(",")});
        limit 100;
        """.trimIndent()

        val body = query.toRequestBody("text/plain".toMediaType())

        return RetrofitClient.api.getLogos(body).associateBy { it.id }
    }
}