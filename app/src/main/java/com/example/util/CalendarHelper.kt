package com.example.util

import android.content.ContentUris
import android.content.Context
import android.database.Cursor
import android.provider.CalendarContract
import com.example.ui.widget.CalendarEvent
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

object CalendarHelper {

    suspend fun getUpcomingEvents(context: Context): List<CalendarEvent> {
        return withContext(Dispatchers.IO) {
            val events = mutableListOf<CalendarEvent>()
            val now = System.currentTimeMillis()
            
            // Look ahead for the next 24 hours
            val end = now + 24 * 60 * 60 * 1000L

            val projection = arrayOf(
                CalendarContract.Instances.EVENT_ID,
                CalendarContract.Instances.TITLE,
                CalendarContract.Instances.BEGIN,
                CalendarContract.Instances.EVENT_LOCATION
            )

            val builder = CalendarContract.Instances.CONTENT_URI.buildUpon()
            ContentUris.appendId(builder, now)
            ContentUris.appendId(builder, end)

            // Select only events that have not been completely cancelled
            val selection = "${CalendarContract.Instances.VISIBLE} = 1"

            val cursor: Cursor? = context.contentResolver.query(
                builder.build(),
                projection,
                selection,
                null,
                "${CalendarContract.Instances.BEGIN} ASC LIMIT 10"
            )

            cursor?.use {
                val idIndex = it.getColumnIndex(CalendarContract.Instances.EVENT_ID)
                val titleIndex = it.getColumnIndex(CalendarContract.Instances.TITLE)
                val beginIndex = it.getColumnIndex(CalendarContract.Instances.BEGIN)
                val locationIndex = it.getColumnIndex(CalendarContract.Instances.EVENT_LOCATION)

                val timeFormat = SimpleDateFormat("hh:mm a", Locale.getDefault())
                val todayCalendar = Calendar.getInstance()

                while (it.moveToNext()) {
                    val eventId = it.getLong(idIndex).toString()
                    val title = it.getString(titleIndex) ?: "Untitled Event"
                    val beginTime = it.getLong(beginIndex)
                    val location = it.getString(locationIndex)

                    val eventCalendar = Calendar.getInstance().apply { timeInMillis = beginTime }
                    val isToday = todayCalendar.get(Calendar.DAY_OF_YEAR) == eventCalendar.get(Calendar.DAY_OF_YEAR) &&
                            todayCalendar.get(Calendar.YEAR) == eventCalendar.get(Calendar.YEAR)

                    val timeString = timeFormat.format(Date(beginTime))

                    events.add(
                        CalendarEvent(
                            id = eventId,
                            title = title,
                            time = timeString,
                            location = location,
                            isToday = isToday
                        )
                    )
                }
            }

            events
        }
    }
}
