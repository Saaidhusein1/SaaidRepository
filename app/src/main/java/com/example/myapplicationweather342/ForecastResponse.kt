package com.example.myapplicationweather342

import com.google.gson.annotations.SerializedName
import com.example.myapplicationweather342.Weather // ✅ Import the Weather class

data class ForecastResponse(
    @SerializedName("daily") val daily: List<DailyForecast>
)

data class DailyForecast(
    @SerializedName("dt") val dt: Long,
    @SerializedName("temp") val temp: Temperature,
    @SerializedName("weather") val weather: List<Weather>
)

data class Temperature(
    @SerializedName("min") val min: Double,
    @SerializedName("max") val max: Double,
    @SerializedName("day") val day: Double
)
