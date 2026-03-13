package com.insa.mygameslist.data

import android.util.Log
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.FormBody
import okhttp3.OkHttpClient
import okhttp3.Request
import org.json.JSONObject

suspend fun getTwitchToken(clientID: String, clientSecret: String): String {
    return withContext(Dispatchers.IO) {
        val client = OkHttpClient()

        val url = "https://id.twitch.tv/oauth2/token" +
                "?client_id=$clientID" +
                "&client_secret=$clientSecret" +
                "&grant_type=client_credentials"

        val request = Request.Builder()
            .url(url)
            .post(FormBody.Builder().build())
            .build()

        val response = client.newCall(request).execute()
        val body = response.body.string()

        val json = JSONObject(body)
        json.getString("access_token")

    }
}