package com.example.myapplicationweather342

import retrofit2.http.GET
import retrofit2.http.Query
import retrofit2.http.Headers

interface WeatherApiService {
    @GET("weather")
    suspend fun getWeatherByZip(
        @Query("zip") zip: String,
        @Query("appid") apiKey: String,
        @Query("units") units: String = "metric"
    ): WeatherResponse

    @GET("onecall")
    suspend fun getForecastByCoordinates(
        @Query("lat") lat: Double,
        @Query("lon") lon: Double,
        @Query("exclude") exclude: String,
        @Query("appid") apiKey: String,
        @Query("units") units: String = "metric"
    ): ForecastResponse
}
