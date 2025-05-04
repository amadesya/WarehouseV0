package com.example.warehousev0.api

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object ApiClient {
    //private const val BASE_URL = "http://10.0.2.2/"  // Если на реальном устройсте меняй адрес, основной ip через /ipconfig
    private const val BASE_URL = "http://192.168.0.107/" //Так же требуется поменять по пути xml/network_security_config

    private val retrofit = Retrofit.Builder()
        .baseUrl(BASE_URL)
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    fun getApiService(): ApiService = retrofit.create(ApiService::class.java)
}
