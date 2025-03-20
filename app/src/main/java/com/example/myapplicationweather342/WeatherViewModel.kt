package com.example.myapplicationweather342

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class WeatherViewModel : ViewModel() {
    private val _weatherData = MutableStateFlow<WeatherResponse?>(null)
    val weatherData: StateFlow<WeatherResponse?> = _weatherData

    // ✅ Corrected function name
    fun fetchWeatherByCoordinates(lat: Double, lon: Double, apiKey: String) {
        viewModelScope.launch {
            try {
                val response = WeatherApiClient.apiService.getWeatherByCoordinates(lat, lon, apiKey)
                _weatherData.value = response
            } catch (e: Exception) {
                _weatherData.value = null
                println("❌ API ERROR: ${e.message}")  // ✅ Log the error in Logcat
            }
        }
    }
}


