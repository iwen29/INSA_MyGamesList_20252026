package com.insa.mygameslist.data
import okhttp3.RequestBody
import retrofit2.http.Body
import retrofit2.http.Header
import retrofit2.http.Headers
import retrofit2.http.POST

interface IGDBAPI {
    @Headers(
        "Client-ID: nbgd9yqjfllcrcqjjdedtqkdt0rbz6",
        "Authorization: Bearer jy6renofyduikl6xqy8kzuynhsc277"
    )

    @POST("games")
    suspend fun getGames(
        @Body body: RequestBody
    ): List<GameAPI>

    @Headers(
        "Client-ID: nbgd9yqjfllcrcqjjdedtqkdt0rbz6",
        "Authorization: Bearer jy6renofyduikl6xqy8kzuynhsc277"
    )
    @POST("platforms")
    suspend fun getPlatforms(
        @Body body: RequestBody
    ): List<PlatformAPI>

    @Headers(
        "Client-ID: nbgd9yqjfllcrcqjjdedtqkdt0rbz6",
        "Authorization: Bearer jy6renofyduikl6xqy8kzuynhsc277"
    )
    @POST("covers")
    suspend fun getCovers(
        @Body body: RequestBody
    ): List<Cover>

    @Headers(
        "Client-ID: nbgd9yqjfllcrcqjjdedtqkdt0rbz6",
        "Authorization: Bearer jy6renofyduikl6xqy8kzuynhsc277"
    )
    @POST("genres")
    suspend fun getGenres(
        @Body body: RequestBody
    ) : List<Genre>

    @Headers(
        "Client-ID: nbgd9yqjfllcrcqjjdedtqkdt0rbz6",
        "Authorization: Bearer jy6renofyduikl6xqy8kzuynhsc277"
    )
    @POST("platform_logos")
    suspend fun getLogos(
        @Body body: RequestBody
    ): List<Logo>
}