package com.example.myapplicationweather342

import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.flow.first
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class WeatherViewModelTest {

    private lateinit var viewModel: WeatherViewModel

    // Fake Weather API
    private val fakeWeatherApi = object : WeatherApiService {
        override suspend fun getWeatherByZip(zip: String, apiKey: String): WeatherResponse {
            return when (zip) {
                "55104" -> WeatherResponse(
                    cityName = "Saint Paul",
                    main = Main(temp = 20.5),
                    weather = listOf(Weather(description = "Cloudy")),
                    coord = Coord(lat = 44.95, lon = -93.09)
                )
                "00000" -> throw Exception("Invalid ZIP")
                else -> WeatherResponse(
                    cityName = "FallbackCity",
                    main = Main(temp = 0.0),
                    weather = listOf(Weather(description = "Unknown")),
                    coord = Coord(lat = 0.0, lon = 0.0)
                )
            }
        }

        override suspend fun getWeatherByCoordinates(lat: Double, lon: Double, apiKey: String): WeatherResponse {
            return WeatherResponse(
                cityName = "LocationCity",
                main = Main(temp = 22.0),
                weather = listOf(Weather(description = "Sunny")),
                coord = Coord(lat, lon)
            )
        }
    }

    // Fake Forecast API
    private val fakeForecastApi = object : ForecastApiService {
        override suspend fun getForecast(lat: Double, lon: Double, apiKey: String): ForecastResponse {
            return ForecastResponse(
                list = listOf(
                    ForecastItem(
                        dtTxt = "2024-01-01 12:00:00",
                        main = Main(temp = 5.5),
                        weather = listOf(Weather(description = "Light snow"))
                    )
                )
            )
        }
    }

    @Before
    fun setUp() {
        viewModel = WeatherViewModel(fakeWeatherApi, fakeForecastApi)
    }

    @Test
    fun fetchWeatherByZip_success_setsWeatherAndForecast() = runTest {
        viewModel.fetchWeatherByZip("55104", "test_api_key")
        val weather = viewModel.weatherData.first()
        val forecast = viewModel.forecastData.first()

        assertNotNull(weather)
        assertEquals("Saint Paul", weather?.cityName)
        assertEquals(20.5, weather?.main?.temp, 0.01)
        assertEquals("Cloudy", weather?.weather?.first()?.description)

        assertNotNull(forecast)
        assertEquals(1, forecast?.list?.size)
        assertEquals("Light snow", forecast?.list?.first()?.weather?.first()?.description)
    }

    @Test
    fun fetchWeatherByZip_invalidZip_setsErrorMessage() = runTest {
        viewModel.fetchWeatherByZip("00000", "test_api_key")
        val error = viewModel.errorMessage.first()
        assertNotNull(error)
        assertTrue(error!!.contains("Failed to fetch weather"))
    }

    @Test
    fun clearError_resetsErrorMessage() = runTest {
        viewModel.fetchWeatherByZip("00000", "test_api_key")
        assertNotNull(viewModel.errorMessage.first())
        viewModel.clearError()
        assertNull(viewModel.errorMessage.first())
    }

    @Test
    fun fetchWeatherByCoordinates_success_setsWeatherAndForecast() = runTest {
        viewModel.fetchWeatherByCoordinates(44.0, -93.0, "test_api_key")
        val weather = viewModel.weatherData.first()
        val forecast = viewModel.forecastData.first()

        assertEquals("LocationCity", weather?.cityName)
        assertEquals("Sunny", weather?.weather?.first()?.description)
        assertEquals("Light snow", forecast?.list?.first()?.weather?.first()?.description)
    }
}
