package com.example.dailywin.notification

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import com.example.dailywin.data.model.Habit
import java.time.LocalTime
import java.time.ZoneId
import java.util.Calendar

object NotificationUtils {

    fun scheduleNotification(context: Context, habit: Habit) {
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val intent = Intent(context, HabitNotificationReceiver::class.java).apply {
            putExtra("habitName", habit.name)
            putExtra("notificationId", habit.id.hashCode())
        }
        val pendingIntent = PendingIntent.getBroadcast(
            context,
            habit.id.hashCode(),
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val time = LocalTime.parse(habit.time)
        val calendar = Calendar.getInstance().apply {
            set(Calendar.HOUR_OF_DAY, time.hour)
            set(Calendar.MINUTE, time.minute)
            set(Calendar.SECOND, 0)
        }

        when (habit.frequency) {
            com.example.dailywin.data.model.Frequency.DAILY -> {
                alarmManager.setRepeating(
                    AlarmManager.RTC_WAKEUP,
                    calendar.timeInMillis,
                    AlarmManager.INTERVAL_DAY,
                    pendingIntent
                )
            }
            com.example.dailywin.data.model.Frequency.WEEKLY -> {
                habit.daysOfWeek.forEach { day ->
                    val dayOfWeek = when (day) {
                        "L" -> Calendar.MONDAY
                        "M" -> Calendar.TUESDAY
                        "X" -> Calendar.WEDNESDAY
                        "J" -> Calendar.THURSDAY
                        "V" -> Calendar.FRIDAY
                        "S" -> Calendar.SATURDAY
                        "D" -> Calendar.SUNDAY
                        else -> -1
                    }
                    if (dayOfWeek != -1) {
                        val newCalendar = calendar.clone() as Calendar
                        newCalendar.set(Calendar.DAY_OF_WEEK, dayOfWeek)
                        alarmManager.setRepeating(
                            AlarmManager.RTC_WAKEUP,
                            newCalendar.timeInMillis,
                            AlarmManager.INTERVAL_DAY * 7,
                            pendingIntent
                        )
                    }
                }
            }
            com.example.dailywin.data.model.Frequency.MONTHLY -> {
                alarmManager.setRepeating(
                    AlarmManager.RTC_WAKEUP,
                    calendar.timeInMillis,
                    AlarmManager.INTERVAL_DAY * calendar.getActualMaximum(Calendar.DAY_OF_MONTH),
                    pendingIntent
                )
            }
        }
    }

    fun cancelNotification(context: Context, habit: Habit) {
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val intent = Intent(context, HabitNotificationReceiver::class.java)
        val pendingIntent = PendingIntent.getBroadcast(
            context,
            habit.id.hashCode(),
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
        alarmManager.cancel(pendingIntent)
    }
}
