package ru.inponomarev.celestademo.integration

import retrofit2.Call
import retrofit2.http.GET

interface AppApi {
    @get:GET("item")
    val items: Call<String?>
}