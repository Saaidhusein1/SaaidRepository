package com.example.myapplicationweather342

import android.app.*
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.os.Build
import android.os.IBinder
import android.util.Log
import android.widget.Toast
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat
import com.google.android.gms.location.*
import kotlinx.coroutines.*
import retrofit2.HttpException

class LocationWeatherService : Service() {

    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private val apiKey = "bbd0cf71d119762444df04c15a584eff"
    private val serviceScope = CoroutineScope(SupervisorJob() + Dispatchers.IO)

    override fun onCreate() {
        super.onCreate()

        Toast.makeText(this, "\uD83D\uDCCD Location Service Started", Toast.LENGTH_SHORT).show()
        Log.d("WeatherService", "✅ Service created")

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
        startForeground(NOTIFICATION_ID, getInitialNotification("Loading weather...", "--", "--"))

        getLocationAndUpdateWeather()
    }

    private fun getLocationAndUpdateWeather() {
        val request = LocationRequest.Builder(Priority.PRIORITY_HIGH_ACCURACY, 5000L) // every 5s
            .setMinUpdateIntervalMillis(2000L) // at least every 2s
            .build()

        val locationCallback = object : LocationCallback() {
            override fun onLocationResult(result: LocationResult) {
                val location = result.lastLocation
                Log.d("WeatherService", "✅ onLocationResult() received: $location")

                if (location != null) {
                    fetchWeather(location)
                } else {
                    getLastKnownLocationFallback()
                }
            }
        }

        val hasFine = ContextCompat.checkSelfPermission(
            this, android.Manifest.permission.ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED

        val hasCoarse = ContextCompat.checkSelfPermission(
            this, android.Manifest.permission.ACCESS_COARSE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED

        if (hasFine || hasCoarse) {
            Log.d("WeatherService", "✅ Permissions granted. Requesting location updates...")
            try {
                fusedLocationClient.requestLocationUpdates(request, locationCallback, mainLooper)
            } catch (e: SecurityException) {
                Log.e("WeatherService", "❌ SecurityException during requestLocationUpdates", e)
                updateNotificationWithFailure()
            }
        } else {
            Log.e("WeatherService", "❌ Location permissions not granted")
        }
    }

    private fun getLastKnownLocationFallback() {
        Log.d("WeatherService", "⏪ Trying getLastLocation fallback...")
        try {
            fusedLocationClient.lastLocation.addOnSuccessListener { location ->
                if (location != null) {
                    fetchWeather(location)
                } else {
                    Log.e("WeatherService", "❌ Last known location is null")
                    updateNotificationWithFailure()
                }
            }
        } catch (e: SecurityException) {
            Log.e("WeatherService", "❌ SecurityException during getLastLocation", e)
            updateNotificationWithFailure()
        }
    }

    private fun fetchWeather(location: Location) {
        serviceScope.launch {
            try {
                Log.d("WeatherService", "🌐 Fetching weather for ${location.latitude}, ${location.longitude}")

                val weather = WeatherApiClient.apiService.getWeatherByCoordinates(
                    lat = location.latitude,
                    lon = location.longitude,
                    apiKey = apiKey
                )

                val temp = "${weather.main.temperature}\u00B0"
                val desc = weather.weather.firstOrNull()?.description ?: "Unknown"
                val city = weather.cityName

                val notification = getInitialNotification(desc, temp, city)
                val manager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager
                manager.notify(NOTIFICATION_ID, notification)

                Log.d("WeatherService", "✅ Weather updated for $city")

                val broadcastIntent = Intent(ACTION_LOCATION_WEATHER).apply {
                    putExtra(EXTRA_LAT, location.latitude)
                    putExtra(EXTRA_LON, location.longitude)
                }
                sendBroadcast(broadcastIntent)

            } catch (e: HttpException) {
                Log.e("WeatherService", "❌ HTTP error fetching weather", e)
                updateNotificationWithFailure()
            } catch (e: Exception) {
                Log.e("WeatherService", "❌ Unknown error fetching weather", e)
                updateNotificationWithFailure()
            }
        }
    }

    private fun updateNotificationWithFailure() {
        val notification = getInitialNotification("Unable to get weather", "--", "--")
        val manager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager
        manager.notify(NOTIFICATION_ID, notification)
    }

    private fun getInitialNotification(desc: String, temp: String, city: String): Notification {
        val channelId = "weather_notification_channel"

        val intent = Intent(this, MainActivity::class.java)
        val pendingIntent = PendingIntent.getActivity(
            this, 0, intent, PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        )

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                channelId,
                "Weather Updates",
                NotificationManager.IMPORTANCE_LOW
            )
            val manager = getSystemService(NotificationManager::class.java)
            manager.createNotificationChannel(channel)
        }

        return NotificationCompat.Builder(this, channelId)
            .setContentTitle("$city • $temp")
            .setContentText(desc)
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setContentIntent(pendingIntent)
            .setOngoing(true)
            .build()
    }

    override fun onBind(intent: Intent?): IBinder? = null

    override fun onDestroy() {
        Log.d("WeatherService", "🛑 Service destroyed")
        serviceScope.cancel()
        super.onDestroy()
    }

    companion object {
        const val NOTIFICATION_ID = 1
        const val ACTION_LOCATION_WEATHER = "com.example.myapplicationweather342.LOCATION_WEATHER"
        const val EXTRA_LAT = "extra_latitude"
        const val EXTRA_LON = "extra_longitude"
    }
}
