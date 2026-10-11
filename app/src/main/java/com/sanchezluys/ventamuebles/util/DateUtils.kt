package com.sanchezluys.ventamuebles.util

import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

object DateUtils {

    private val locale = Locale("es", "CO")

    fun formatCurrency(amount: Double): String {
        val format = NumberFormat.getCurrencyInstance(locale)
        format.maximumFractionDigits = 0
        return format.format(amount)
    }

    fun formatDateTime(timestamp: Long): String {
        val sdf = SimpleDateFormat("dd/MM/yyyy HH:mm", locale)
        return sdf.format(Date(timestamp))
    }

    fun formatDate(timestamp: Long): String {
        val sdf = SimpleDateFormat("dd 'de' MMMM, yyyy", locale)
        return sdf.format(Date(timestamp))
    }

    fun formatShortDate(timestamp: Long): String {
        val sdf = SimpleDateFormat("dd/MM/yyyy", locale)
        return sdf.format(Date(timestamp))
    }

    fun formatTime(timestamp: Long): String {
        val sdf = SimpleDateFormat("hh:mm a", locale)
        return sdf.format(Date(timestamp))
    }

    fun getStartOfDay(timestamp: Long = System.currentTimeMillis()): Long {
        val calendar = Calendar.getInstance().apply {
            timeInMillis = timestamp
            set(Calendar.HOUR_OF_DAY, 0)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }
        return calendar.timeInMillis
    }

    fun getEndOfDay(timestamp: Long = System.currentTimeMillis()): Long {
        val calendar = Calendar.getInstance().apply {
            timeInMillis = timestamp
            set(Calendar.HOUR_OF_DAY, 23)
            set(Calendar.MINUTE, 59)
            set(Calendar.SECOND, 59)
            set(Calendar.MILLISECOND, 999)
        }
        return calendar.timeInMillis
    }

    fun getYesterdayStartAndEnd(): Pair<Long, Long> {
        val calendar = Calendar.getInstance().apply {
            add(Calendar.DAY_OF_YEAR, -1)
        }
        val start = getStartOfDay(calendar.timeInMillis)
        val end = getEndOfDay(calendar.timeInMillis)
        return Pair(start, end)
    }

    fun getThisWeekStartAndEnd(): Pair<Long, Long> {
        val calendar = Calendar.getInstance().apply {
            firstDayOfWeek = Calendar.MONDAY
            set(Calendar.DAY_OF_WEEK, Calendar.MONDAY)
            set(Calendar.HOUR_OF_DAY, 0)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }
        val start = calendar.timeInMillis
        val end = getEndOfDay(System.currentTimeMillis())
        return Pair(start, end)
    }

    fun getThisMonthStartAndEnd(): Pair<Long, Long> {
        val calendar = Calendar.getInstance().apply {
            set(Calendar.DAY_OF_MONTH, 1)
            set(Calendar.HOUR_OF_DAY, 0)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }
        val start = calendar.timeInMillis
        val end = getEndOfDay(System.currentTimeMillis())
        return Pair(start, end)
    }
}
