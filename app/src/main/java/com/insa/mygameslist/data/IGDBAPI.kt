package com.insa.mygameslist.data
import okhttp3.RequestBody
import retrofit2.http.Body
import retrofit2.http.Header
import retrofit2.http.Headers
import retrofit2.http.POST

interface IGDBAPI {



    @POST("games")
    suspend fun getGames(
        @Header("Client-ID") clientId: String,
        @Header("Authorization") authorization: String,
        @Body body: RequestBody
    ): List<GameAPI>

    @POST("platforms")
    suspend fun getPlatforms(
        @Header("Client-ID") clientId: String,
        @Header("Authorization") authorization: String,
        @Body body: RequestBody
    ): List<PlatformAPI>

    @POST("covers")
    suspend fun getCovers(
        @Header("Client-ID") clientId: String,
        @Header("Authorization") authorization: String,
        @Body body: RequestBody
    ): List<Cover>

    @POST("genres")
    suspend fun getGenres(
        @Header("Client-ID") clientId: String,
        @Header("Authorization") authorization: String,
        @Body body: RequestBody
    ) : List<Genre>

    @POST("platform_logos")
    suspend fun getLogos(
        @Header("Client-ID") clientId: String,
        @Header("Authorization") authorization: String,
        @Body body: RequestBody
    ): List<Logo>
}