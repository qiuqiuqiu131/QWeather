package com.qiuqiuqiu.weatherPredicate.tools

import android.annotation.SuppressLint
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.format.DateTimeFormatter

fun String.toTimeWithPeriod(): String {
    return try {
        val timePart = this.substringAfter('T').substringBefore('+')
        val (hourStr, minuteStr) = timePart.split("-").first().split(':')
        val hour = hourStr.toInt()

        val period =
            when (hour) {
                in 0..5 -> "凌晨"
                in 6..8 -> "清晨"
                in 9..11 -> "上午"
                12 -> "中午"
                in 13..17 -> "下午"
                in 18..21 -> "晚上"
                else -> "半夜"
            }

        val displayHour =
            when {
                hour == 0 || hour == 12 -> 12
                hour > 12 -> hour - 12
                else -> hour
            }

        "$period$displayHour:$minuteStr"
    } catch (e: Exception) {
        this
    }
}

@SuppressLint("NewApi")
fun String.toDayLabel(): String {
    val date = LocalDate.parse(this)
    val today = LocalDate.now()
    val diff = date.toEpochDay() - today.toEpochDay()
    val weekStr =
        when (date.dayOfWeek) {
            DayOfWeek.MONDAY -> "周一"
            DayOfWeek.TUESDAY -> "周二"
            DayOfWeek.WEDNESDAY -> "周三"
            DayOfWeek.THURSDAY -> "周四"
            DayOfWeek.FRIDAY -> "周五"
            DayOfWeek.SATURDAY -> "周六"
            DayOfWeek.SUNDAY -> "周日"
        }
    return when (diff) {
        -1L -> "昨天"
        0L -> "今天"
        1L -> "明天"
        else -> weekStr
    }
}

@SuppressLint("NewApi")
fun String.toMonthDay(): String {
    val date = LocalDate.parse(this)
    return date.format(DateTimeFormatter.ofPattern("MM/dd"))
}

@SuppressLint("NewApi")
fun String.isToday(): Boolean {
    return try {
        val date = LocalDate.parse(this)
        date == LocalDate.now()
    } catch (e: Exception) {
        false
    }
}
