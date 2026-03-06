package com.insa.mygameslist.data

import kotlinx.serialization.json.Json
import okhttp3.MediaType.Companion.toMediaType
import retrofit2.Retrofit
import retrofit2.converter.kotlinx.serialization.asConverterFactory

object RetrofitClient {

    private val json = Json {
        ignoreUnknownKeys = true
    }
    private val retrofit = Retrofit.Builder()
        .baseUrl("https://api.igdb.com/v4/")
        .addConverterFactory(json.asConverterFactory("application/json".toMediaType()))
        .build()

    val api: IGDBAPI = retrofit.create(IGDBAPI::class.java)
}