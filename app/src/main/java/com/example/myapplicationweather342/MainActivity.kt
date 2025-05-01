package com.example.myapplicationweather342

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.myapplicationweather342.ui.theme.MyApplicationWeather342Theme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MyApplicationWeather342Theme {
                Surface(modifier = Modifier.fillMaxSize()) {
                    StaticWeatherScreen()
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StaticWeatherScreen() {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.app_name)) }
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .padding(24.dp)
                .fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Location
            Text(text = stringResource(R.string.city_name), fontSize = 20.sp)

            Spacer(modifier = Modifier.height(16.dp))

            // Temperature and Icon
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(text = stringResource(R.string.temp_main), fontSize = 64.sp)
                    Text(text = stringResource(R.string.temp_feels_like), fontSize = 14.sp)
                }

                Spacer(modifier = Modifier.width(16.dp))

                Image(
                    painter = painterResource(id = R.mipmap.ic_launcher_foreground),
                    contentDescription = stringResource(R.string.sunny_description),
                    modifier = Modifier.size(64.dp)
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Other weather details
            Text(text = stringResource(R.string.temp_low), fontSize = 16.sp)
            Text(text = stringResource(R.string.temp_high), fontSize = 16.sp)
            Text(text = stringResource(R.string.humidity), fontSize = 16.sp)
            Text(text = stringResource(R.string.pressure), fontSize = 16.sp)
        }
    }
}
