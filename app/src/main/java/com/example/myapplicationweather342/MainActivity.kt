package com.example.myapplicationweather342

import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import androidx.navigation.compose.*
import kotlinx.coroutines.flow.collectLatest

class MainActivity : ComponentActivity() {
    private val viewModel: WeatherViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            AppNavigation(viewModel)
        }
    }
}

@Composable
fun AppNavigation(viewModel: WeatherViewModel) {
    val navController = rememberNavController()
    NavHost(navController = navController, startDestination = "weather") {
        composable("weather") { WeatherScreen(viewModel, navController) }
        composable("forecast") { ForecastScreen(viewModel, navController) }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WeatherScreen(viewModel: WeatherViewModel, navController: NavController) {
    val context = LocalContext.current
    val weatherState by viewModel.weatherData.collectAsState()
    val errorMessage by viewModel.errorMessage.collectAsState()

    var zipCode by remember { mutableStateOf("") }

    // 🔔 Handle error messages with toast
    LaunchedEffect(errorMessage) {
        errorMessage?.let {
            Toast.makeText(context, it, Toast.LENGTH_SHORT).show()
            viewModel.clearError()
        }
    }

    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {

        TopAppBar(
            title = { Text(stringResource(R.string.app_name), color = Color.Black) },
            modifier = Modifier
                .fillMaxWidth()
                .background(Color.Gray)
        )

        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(
            value = zipCode,
            onValueChange = {
                if (it.length <= 5 && it.all { c -> c.isDigit() }) zipCode = it
            },
            label = { Text(stringResource(R.string.enter_zip)) },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            singleLine = true,
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(12.dp))

        Button(
            onClick = {
                if (zipCode.length == 5) {
                    viewModel.fetchWeatherByZip(zipCode, "bbd0cf71d119762444df04c15a584eff")
                } else {
                    Toast.makeText(context, "Enter a valid 5-digit ZIP", Toast.LENGTH_SHORT).show()
                }
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Fetch Weather")
        }

        Spacer(modifier = Modifier.height(12.dp))

        Button(
            onClick = {
                if (weatherState != null) {
                    navController.navigate("forecast")
                } else {
                    Toast.makeText(context, "Load current weather first", Toast.LENGTH_SHORT).show()
                }
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(stringResource(R.string.show_forecast))
        }

        Spacer(modifier = Modifier.height(24.dp))

        weatherState?.let { weather ->
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(weather.cityName, fontSize = 18.sp, fontWeight = FontWeight.Medium)
                Spacer(modifier = Modifier.height(8.dp))
                Text("${weather.main.temperature}°", fontSize = 64.sp, fontWeight = FontWeight.Bold)
                Text("Feels like ${weather.main.temperature}°", fontSize = 14.sp, color = Color.Gray)
                Spacer(modifier = Modifier.height(8.dp))
                Image(
                    painter = painterResource(id = R.mipmap.ic_launcher_foreground),
                    contentDescription = "Weather Icon",
                    modifier = Modifier.size(50.dp)
                )
                Spacer(modifier = Modifier.height(16.dp))
                Text("Humidity: ${weather.main.humidity}%", fontSize = 16.sp)
                Text("Description: ${weather.weather[0].description}", fontSize = 16.sp)
            }
        } ?: Text(
            text = "No weather data yet.",
            modifier = Modifier.align(Alignment.CenterHorizontally)
        )
    }
}


