package com.example.myapplicationweather342

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun ForecastScreen(viewModel: WeatherViewModel, navController: NavController) {
    val forecastState = viewModel.forecastData.collectAsState().value

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        // 🔙 Back button and title
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp)
        ) {
            IconButton(onClick = { navController.popBackStack() }) {
                Icon(
                    painter = painterResource(id = R.drawable.ic_arrow_back),
                    contentDescription = "Back"
                )
            }
            Text(
                text = "5-Day Forecast",
                style = MaterialTheme.typography.headlineSmall,
                modifier = Modifier.padding(start = 8.dp)
            )
        }

        // 🌤 Forecast list
        if (forecastState != null && forecastState.list.isNotEmpty()) {
            LazyColumn {
                items(forecastState.list) { forecast ->
                    ForecastItem(forecast)
                }
            }
        } else {
            Text("No forecast data available.")
        }
    }
}

@Composable
fun ForecastItem(forecast: ForecastItem) {
    val sdf = remember { SimpleDateFormat("EEE, MMM d • h a", Locale.getDefault()) }
    val dateText = sdf.format(Date(forecast.dt * 1000L))

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
    ) {
        Text("Time: $dateText", style = MaterialTheme.typography.bodyLarge)
        Text("Temp: ${forecast.main.temperature}°", style = MaterialTheme.typography.bodyMedium)
        Text("Humidity: ${forecast.main.humidity}%", style = MaterialTheme.typography.bodyMedium)
        Text(
            "Description: ${forecast.weather.firstOrNull()?.description ?: "N/A"}",
            style = MaterialTheme.typography.bodyMedium
        )
    }
}
