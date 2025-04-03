package com.example.myapplicationweather342

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun ForecastScreen(viewModel: WeatherViewModel, navController: NavController) {
    val forecast = viewModel.forecastData.collectAsState().value

    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        Text("16-Day Forecast", style = MaterialTheme.typography.headlineSmall)

        Spacer(modifier = Modifier.height(8.dp))

        forecast?.daily?.let { days ->
            LazyColumn {
                items(days) { day ->
                    ForecastItem(day)
                }
            }
        } ?: Text("No forecast data available.")
    }
}

@Composable
fun ForecastItem(day: DailyForecast) {
    val date = remember(day.dt) {
        SimpleDateFormat("EEE, MMM d", Locale.getDefault()).format(Date(day.dt * 1000))
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 6.dp),
        elevation = CardDefaults.cardElevation(4.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(text = date, style = MaterialTheme.typography.titleMedium)
            Text(text = "Day: ${day.temp.day}°C | Min: ${day.temp.min}°C | Max: ${day.temp.max}°C")
            Text(text = day.weather.firstOrNull()?.description ?: "")
        }
    }
}
