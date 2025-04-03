package com.example.myapplicationweather342

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import com.example.myapplicationweather342.WeatherResponse
import com.example.myapplicationweather342.ForecastResponse

class WeatherViewModel : ViewModel() {
    private val _weatherData = MutableStateFlow<WeatherResponse?>(null)
    val weatherData: StateFlow<WeatherResponse?> = _weatherData

    private val _forecastData = MutableStateFlow<ForecastResponse?>(null)
    val forecastData: StateFlow<ForecastResponse?> = _forecastData

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage

    fun fetchWeatherByZip(zip: String, apiKey: String) {
        viewModelScope.launch {
            try {
                val response = WeatherApiClient.apiService.getWeatherByZip(zip, apiKey)
                _weatherData.value = response

                // ✅ Use ForecastApiClient for One Call 3.0
                fetchForecastByCoordinates(response.coord.lat, response.coord.lon, apiKey)
            } catch (e: Exception) {
                _weatherData.value = null
                _errorMessage.value = "Invalid ZIP Code"
                Log.e("WeatherViewModel", "Weather fetch error", e)
            }
        }
    }

    fun fetchForecastByCoordinates(lat: Double, lon: Double, apiKey: String) {
        viewModelScope.launch {
            try {
                val forecast = ForecastApiClient.apiService.getForecastByCoordinates(
                    lat, lon,
                    "current,minutely,hourly,alerts", apiKey
                )
                _forecastData.value = forecast
            } catch (e: Exception) {
                _forecastData.value = null
                Log.e("WeatherViewModel", "Forecast fetch error", e)
            }
        }
    }

    fun clearError() {
        _errorMessage.value = null
    }
}




