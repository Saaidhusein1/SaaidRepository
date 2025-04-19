package com.example.myapplicationweather342

import retrofit2.http.GET
import retrofit2.http.Query

interface ForecastApiService {
    @GET("forecast")
    suspend fun getForecast(
        @Query("lat") lat: Double,
        @Query("lon") lon: Double,
        @Query("appid") apiKey: String,
        @Query("units") units: String = "metric"
    ): ForecastResponse
}
