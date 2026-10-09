package com.example.util

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONObject
import java.net.HttpURLConnection
import java.net.URL

data class LiveWeatherData(
    val tempC: Double,
    val tempF: Int,
    val condition: String,
    val maxTempF: Int,
    val minTempF: Int
)

object WeatherApiHelper {

    suspend fun fetchLiveWeather(latitude: Double, longitude: Double): LiveWeatherData? {
        return withContext(Dispatchers.IO) {
            try {
                val urlString = "https://api.open-meteo.com/v1/forecast?latitude=$latitude&longitude=$longitude&current_weather=true&daily=temperature_2m_max,temperature_2m_min&timezone=auto"
                val url = URL(urlString)
                val connection = url.openConnection() as HttpURLConnection
                connection.requestMethod = "GET"
                connection.connectTimeout = 6000
                connection.readTimeout = 6000

                if (connection.responseCode == 200) {
                    val responseText = connection.inputStream.bufferedReader().use { it.readText() }
                    val json = JSONObject(responseText)

                    val currentWeather = json.getJSONObject("current_weather")
                    val currentTempC = currentWeather.getDouble("temperature")
                    val weatherCode = currentWeather.getInt("weathercode")

                    val daily = json.optJSONObject("daily")
                    val maxTempC = daily?.optJSONArray("temperature_2m_max")?.optDouble(0, currentTempC + 3) ?: (currentTempC + 3)
                    val minTempC = daily?.optJSONArray("temperature_2m_min")?.optDouble(0, currentTempC - 3) ?: (currentTempC - 3)

                    val tempF = ((currentTempC * 9 / 5) + 32).toInt()
                    val maxTempF = ((maxTempC * 9 / 5) + 32).toInt()
                    val minTempF = ((minTempC * 9 / 5) + 32).toInt()

                    val condition = mapWeatherCodeToCondition(weatherCode)

                    LiveWeatherData(
                        tempC = currentTempC,
                        tempF = tempF,
                        condition = condition,
                        maxTempF = maxTempF,
                        minTempF = minTempF
                    )
                } else {
                    null
                }
            } catch (e: Exception) {
                e.printStackTrace()
                null
            }
        }
    }

    private fun mapWeatherCodeToCondition(code: Int): String {
        return when (code) {
            0 -> "Clear Sky"
            1, 2 -> "Partly Cloudy"
            3 -> "Overcast"
            45, 48 -> "Foggy"
            51, 53, 55 -> "Light Drizzle"
            61, 63, 65 -> "Rain Showers"
            71, 73, 75 -> "Snow"
            80, 81, 82 -> "Rain Showers"
            95, 96, 99 -> "Thunderstorm"
            else -> "Partly Cloudy"
        }
    }
}
