package com.chattingapp.foodrecipeuidemo.date

import android.util.Log
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.TimeZone

object CalculateDate {

    fun formatDateForUser(dateString: String): String {
        val formatter = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.getDefault()).apply {
            timeZone = TimeZone.getTimeZone("Europe/Istanbul") // Set to Turkey timezone
        }
        val dateTime: Date
        try {
            dateTime = formatter.parse(dateString) ?: return "Invalid date"
        } catch (e: ParseException) {
            return "Parse error"
        }

        val now = Date()


        val durationMillis = now.time - dateTime.time

        val durationSeconds = durationMillis / 1000
        val durationMinutes = durationSeconds / 60
        val durationHours = durationMinutes / 60
        val durationDays = durationHours / 24
        return when {
            durationDays > 0 -> "${durationDays} day(s) ago"
            durationHours > 0 -> "${durationHours} hour(s) ago"
            durationMinutes > 0 -> "${durationMinutes} minute(s) ago"
            else -> "Just now"
        }
    }
}
