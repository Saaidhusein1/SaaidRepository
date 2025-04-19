package com.example.myapplicationweather342

import android.Manifest
import kotlinx.coroutines.delay
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.*

class MainActivity : ComponentActivity() {
    private val viewModel: WeatherViewModel by viewModels()

    private val permissionLauncher =
        registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) { permissions ->
            val denied = permissions.filterValues { !it }.keys
            if (denied.isEmpty()) {
                startLocationWeatherService()
                Toast.makeText(this, "All permissions granted", Toast.LENGTH_SHORT).show()
            } else {
                val deniedText = denied.joinToString(", ") { it.substringAfterLast('.') }
                Toast.makeText(this, "$deniedText permission(s) denied", Toast.LENGTH_LONG).show()
            }
        }

    private val locationWeatherReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            if (intent?.action == LocationWeatherService.ACTION_LOCATION_WEATHER) {
                val lat = intent.getDoubleExtra(LocationWeatherService.EXTRA_LAT, Double.NaN)
                val lon = intent.getDoubleExtra(LocationWeatherService.EXTRA_LON, Double.NaN)

                Log.d("MainActivity", "📡 Received broadcast with lat=$lat lon=$lon")

                if (!lat.isNaN() && !lon.isNaN()) {
                    viewModel.fetchWeatherByCoordinates(lat, lon, "bbd0cf71d119762444df04c15a584eff")
                }
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val filter = IntentFilter(LocationWeatherService.ACTION_LOCATION_WEATHER)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            registerReceiver(locationWeatherReceiver, filter, Context.RECEIVER_EXPORTED)
        } else {
            registerReceiver(locationWeatherReceiver, filter)
        }

        setContent {
            AppNavigation(viewModel)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        unregisterReceiver(locationWeatherReceiver)
    }

    fun requestLocationPermissions() {
        val permissions = mutableListOf(
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_COARSE_LOCATION
        )

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            permissions.add(Manifest.permission.POST_NOTIFICATIONS)
        }

        permissionLauncher.launch(permissions.toTypedArray())
    }

    private fun startLocationWeatherService() {
        val intent = Intent(this, LocationWeatherService::class.java)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            startForegroundService(intent)
        } else {
            startService(intent)
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
    val activity = context as? MainActivity
    val weatherState by viewModel.weatherData.collectAsState()
    val errorMessage by viewModel.errorMessage.collectAsState()

    val zipErrorText = stringResource(R.string.error_zip_length)
    val forecastErrorText = stringResource(R.string.error_load_weather_first)
    val myLocationDescription = stringResource(R.string.my_location_description)
    val appName = stringResource(R.string.app_name)
    val enterZip = stringResource(R.string.enter_zip)
    val fetchWeather = stringResource(R.string.button_fetch_weather)
    val showForecast = stringResource(R.string.show_forecast)
    val noWeatherData = stringResource(R.string.no_weather_data)

    var zipCode by remember { mutableStateOf("") }
    val focusRequester = remember { FocusRequester() }
    val keyboardController = LocalSoftwareKeyboardController.current

    LaunchedEffect(errorMessage) {
        errorMessage?.let {
            Toast.makeText(context, it, Toast.LENGTH_SHORT).show()
            viewModel.clearError()
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        TopAppBar(
            title = { Text(appName, color = Color.Black) },
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
            label = { Text(enterZip) },
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Number,
                imeAction = ImeAction.Done
            ),
            keyboardActions = KeyboardActions(
                onDone = { keyboardController?.hide() }
            ),
            singleLine = true,
            modifier = Modifier
                .fillMaxWidth()
                .focusRequester(focusRequester)
        )

        Spacer(modifier = Modifier.height(12.dp))

        Row(modifier = Modifier.fillMaxWidth()) {
            Button(
                onClick = {
                    if (zipCode.length == 5) {
                        viewModel.fetchWeatherByZip(zipCode, "bbd0cf71d119762444df04c15a584eff")
                    } else {
                        Toast.makeText(context, zipErrorText, Toast.LENGTH_SHORT).show()
                    }
                },
                modifier = Modifier
                    .weight(1f)
                    .padding(end = 8.dp)
            ) {
                Text(fetchWeather)
            }

            IconButton(
                onClick = {
                    activity?.requestLocationPermissions()
                },
                modifier = Modifier.align(Alignment.CenterVertically)
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.ic_my_location),
                    contentDescription = myLocationDescription
                )
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        Button(
            onClick = {
                if (weatherState != null) {
                    navController.navigate("forecast")
                } else {
                    Toast.makeText(context, forecastErrorText, Toast.LENGTH_SHORT).show()
                }
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(showForecast)
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
            text = noWeatherData,
            modifier = Modifier.align(Alignment.CenterHorizontally)
        )
    }

    LaunchedEffect(Unit) {
        delay(300)
        focusRequester.requestFocus()
    }
}
