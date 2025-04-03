package com.example.myapplicationweather342

import com.google.gson.annotations.SerializedName

data class WeatherResponse(
    @SerializedName("name") val cityName: String,
    @SerializedName("main") val main: Main,
    @SerializedName("weather") val weather: List<Weather>,
    @SerializedName("coord") val coord: Coord
)

data class Main(
    @SerializedName("temp") val temperature: Double,
    @SerializedName("humidity") val humidity: Int
)

data class Coord(
    @SerializedName("lat") val lat: Double,
    @SerializedName("lon") val lon: Double
)

data class Weather(
    @SerializedName("description") val description: String
)

