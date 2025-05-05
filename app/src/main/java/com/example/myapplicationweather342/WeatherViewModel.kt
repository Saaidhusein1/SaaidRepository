package com.example.myapplicationweather342

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class WeatherViewModel(
    private val weatherApi: WeatherApiService = WeatherApiClient.apiService,
    private val forecastApi: ForecastApiService = ForecastApiClient.apiService
) : ViewModel() {

    private val _weatherData = MutableStateFlow<WeatherResponse?>(null)
    val weatherData: StateFlow<WeatherResponse?> = _weatherData

    private val _forecastData = MutableStateFlow<ForecastResponse?>(null)
    val forecastData: StateFlow<ForecastResponse?> = _forecastData

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage

    fun fetchWeatherByZip(zipCode: String, apiKey: String) {
        viewModelScope.launch {
            try {
                val response = weatherApi.getWeatherByZip(zipCode, apiKey)
                Log.d("WeatherViewModel", "✅ Weather fetched by ZIP: ${response.cityName}")
                _weatherData.value = response
                fetchForecast(response.coord.lat, response.coord.lon, apiKey)
            } catch (e: Exception) {
                Log.e("WeatherViewModel", "❌ Error fetching by ZIP: ${e.message}")
                _errorMessage.value = "Failed to fetch weather: ${e.message ?: "Unknown error"}"
            }
        }
    }

    fun fetchWeatherByCoordinates(lat: Double, lon: Double, apiKey: String) {
        viewModelScope.launch {
            try {
                Log.d("WeatherViewModel", "🌍 Fetching weather for coordinates: ($lat, $lon)")
                val response = weatherApi.getWeatherByCoordinates(lat, lon, apiKey)
                Log.d("WeatherViewModel", "✅ Weather fetched by coordinates: ${response.cityName}")
                _weatherData.value = response
                fetchForecast(lat, lon, apiKey)
            } catch (e: Exception) {
                Log.e("WeatherViewModel", "❌ Error fetching by coordinates: ${e.message}")
                _errorMessage.value = "Failed to fetch weather by location: ${e.message ?: "Unknown error"}"
            }
        }
    }

    private fun fetchForecast(lat: Double, lon: Double, apiKey: String) {
        viewModelScope.launch {
            try {
                Log.d("WeatherViewModel", "🔮 Fetching forecast for: ($lat, $lon)")
                val forecast = forecastApi.getForecast(lat, lon, apiKey = apiKey)
                Log.d("WeatherViewModel", "✅ Forecast fetched: ${forecast.list.size} items")
                _forecastData.value = forecast
            } catch (e: Exception) {
                Log.e("WeatherViewModel", "❌ Error fetching forecast: ${e.message}")
                _errorMessage.value = "Failed to fetch forecast: ${e.message ?: "Unknown error"}"
            }
        }
    }

    fun clearError() {
        _errorMessage.value = null
    }
}
