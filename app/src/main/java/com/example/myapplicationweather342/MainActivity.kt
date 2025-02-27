package com.example.myapplicationweather342

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            WeatherScreen() // ✅ This should be the main UI
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WeatherScreen() {
    Column(
        modifier = Modifier.fillMaxSize()
    ) {
        // Top Bar
        TopAppBar(
            title = { Text("Weather Finder", color = Color.Black) },
            modifier = Modifier
                .fillMaxWidth()
                .background(Color.Gray),
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Weather Information
        Column(
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text("St. Paul, MN", fontSize = 18.sp, fontWeight = FontWeight.Medium)

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "72°",
                fontSize = 64.sp,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center
            )

            Text("Feels like 78°", fontSize = 14.sp, color = Color.Gray)

            Spacer(modifier = Modifier.height(8.dp))

            // Weather Icon (Replace with your actual icon resource)
            Image(
                painter = painterResource(id = R.mipmap.ic_launcher_foreground), // Change from drawable to mipmap
                contentDescription = "Weather Icon",
                modifier = Modifier.size(50.dp)
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Additional Weather Details
            Column {
                Text("Low 65°", fontSize = 16.sp)
                Text("High 80°", fontSize = 16.sp)
                Text("Humidity 100%", fontSize = 16.sp)
                Text("Pressure 1023 hPa", fontSize = 16.sp)
            }
        }
    }
}
