package me.senseiju.inventoryrefund.utils

import java.text.SimpleDateFormat
import java.util.concurrent.TimeUnit

val dateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss")

val timeRegex = Regex("\\d+[a-zA-Z]")

fun millisecondsToHMS(milliseconds: Long) : String {
    return String.format("%02d:%02d:%02d", TimeUnit.MILLISECONDS.toHours(milliseconds),
        TimeUnit.MILLISECONDS.toMinutes(milliseconds) - TimeUnit.HOURS.toMinutes(TimeUnit.MILLISECONDS.toHours(milliseconds)),
        TimeUnit.MILLISECONDS.toSeconds(milliseconds) - TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(milliseconds))
    )
}